package com.noeticworld.sgw.util;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class TokenManager {
    public static String accessToken;
    public static String refreshToken;
   static Logger log = LoggerFactory.getLogger(TokenManager.class.getName());

    public static String getToken() throws URISyntaxException {

        RestTemplate restTemplate=new RestTemplate();
        log.info("GetToken Called");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/x-www-form-urlencoded");
        headers.set("Connection","keep-alive");
        headers.set("Authorization","Basic UnBaN0JFY3Y3ZllWdE9VdnhvNXpvWTJHZFBZYTp2dnVNSkF6VXFQSDl3QnZ5dUdkdmNTdjdIa2Nh");
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        HttpEntity<Map<String, Object>> entity = new HttpEntity(form, headers);
        ResponseEntity<String> str= restTemplate.postForEntity(new URI("https://apimtest.jazz.com.pk:8282/token"),entity,String.class);
        JSONObject json = new JSONObject(str.getBody());
        System.out.println(str.getStatusCode()+" "+str.getBody());
        accessToken=json.getString("access_token");
        return json.getString("access_token");
    }

    public static String refreshToken(){

        return "";
    }


}