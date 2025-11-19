package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SalesforceApiService {

    @Autowired
    private SalesforceAuthService authService;

    private RestTemplate rest = new RestTemplate();

    // 1) PUBLIC - Get Access Token from Salesforce using JWT
    public String getAccessToken() throws Exception {

        String jwt = authService.generateJWT();
        String tokenUrl = "https://login.salesforce.com/services/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = 
                "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer" +
                "&assertion=" + jwt;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                rest.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }

    // 2) PRIVATE - Insert Account (returns ID only)
    private String createAccountInternal(String token) {

        String url = "https://empathetic-badger-x02bsh-dev-ed.trailblaze.my.salesforce.com"
                + "/services/data/v61.0/sobjects/Account";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Insert Account with more fields
        Map<String, Object> body = Map.of(
                "Name", "ashour",
                "Phone", "010-1234-5678",
                "Website", "https://mohamedcorp.com",
                "Industry", "Technology",
                "AccountNumber", "A231111"
                    
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                rest.postForEntity(url, entity, Map.class);

        return (String) response.getBody().get("id");
    }

    // 3) PUBLIC - Fetch Account Details using ID
    public Map getAccountById(String token, String id) {

        String query =
                "SELECT Id, Name, Phone, Website, Industry, AccountNumber " +
                "FROM Account WHERE Id = '" + id + "'";

        String url = "https://empathetic-badger-x02bsh-dev-ed.trailblaze.my.salesforce.com"
                + "/services/data/v61.0/query/?q=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = rest.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        // Return only the first record (simplified)
        return ((java.util.List<Map>) response.getBody().get("records")).get(0);
    }

    // 4) PUBLIC - Main entry for controller
    public Map createAndReturnFullAccount() throws Exception {

        String token = getAccessToken();

        // STEP 1: Insert Account (private)
        String id = createAccountInternal(token);

        // STEP 2: Fetch full account details
        return getAccountById(token, id);
    }
}
