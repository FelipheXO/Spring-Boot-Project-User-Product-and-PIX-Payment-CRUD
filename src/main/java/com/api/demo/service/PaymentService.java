package com.api.demo.service;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api.demo.entity.Orders;
import com.api.demo.entity.Product;
import com.api.demo.entity.Users;
import com.api.demo.models.Costumer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

@Service
public class PaymentService {

    @Value("${api.pagarme}")
    private String baseURL;
    @Value("${key.pagarme}")
    private String token;

    public String tokenBase64() {
        return "Basic " + token;
    }

    /**
     * @param email
     * @return
     */
    public Costumer get(Users users) {

        System.out.println(users.getCpf());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", users.getEmail());
        jsonObject.addProperty("name", users.getName());
        jsonObject.addProperty("document", users.getCpf().replace(".", "").replace("-", ""));
        jsonObject.addProperty("document_type", "CPF");
        jsonObject.addProperty("type", "individual");

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObject);

        HttpResponse<String> createCustomerResponse = Unirest.post("https://api.pagar.me/core/v5/customers")
                .body(jsonString)
                .header("accept", "application/json")
                .header("authorization", tokenBase64())
                .asString();

        System.out.println(createCustomerResponse.getBody());

        if (createCustomerResponse.getStatus() == 200) {
            return gson.fromJson(createCustomerResponse.getBody(), Costumer.class);
        }

        return null;

    }

    public Orders create(Costumer costumer, Product product) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer_id", costumer.getId());

        JsonArray itemsArray = new JsonArray();
        JsonObject itemsObject = new JsonObject();
        int amount = (int) product.getPrice() * 100;
        itemsObject.addProperty("amount", amount);
        itemsObject.addProperty("description", product.getName());
        itemsObject.addProperty("quantity", 1);
        itemsObject.addProperty("code", product.getId());
        itemsArray.add(itemsObject);

        jsonObject.add("items", itemsArray);

        JsonArray paymentsArray = new JsonArray();
        JsonObject paymentsObject = new JsonObject();
        paymentsObject.addProperty("payment_method", "pix");

        JsonObject pixObject = new JsonObject();
        pixObject.addProperty("expires_in", 1800);
        paymentsObject.add("Pix", pixObject);

        paymentsArray.add(paymentsObject);
        jsonObject.add("payments", paymentsArray);

        HttpResponse<String> createOrderResponse = Unirest.post("https://api.pagar.me/core/v5/orders")
                .body(jsonObject)
                .header("accept", "application/json")
                .header("authorization", tokenBase64())
                .asString();

        if (createOrderResponse.getStatus() == 200) {
            JSONObject obj = new JSONObject(createOrderResponse.getBody());
            String id = obj.getString("id");
            BigInteger amountBigInteger = obj.getBigInteger("amount");

            JSONObject lastTransaction = obj.getJSONArray("charges").getJSONObject(0).getJSONObject("last_transaction");
            String pixProviderTid = lastTransaction.getString("pix_provider_tid");
            String qrCodeUrl = lastTransaction.getString("qr_code_url");

            Orders order = new Orders();
            order.setId_order(id);
            BigInteger divisor = new BigInteger("100");
            BigInteger result = amountBigInteger.divide(divisor);
            order.setAmount(result.doubleValue());
            order.setPix(pixProviderTid);
            order.setQr_code_url(qrCodeUrl);
            order.setStatus("pending");
            return order;
        }

        return null;

    }

}
