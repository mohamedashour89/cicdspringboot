package com.example.demo.controller;

import org.slf4j.Logger;

import com.example.demo.service.SalesforceApiService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalesforceController {

    private static final Logger log = LoggerFactory.getLogger(SalesforceController.class);


    @Autowired
    private SalesforceApiService apiService;

    @PostMapping("/createAccount")
    public Map createAccount() throws Exception { // create account and return full details 
        log.info("Creating account");
        
        Map accountDetails = apiService.createAndReturnFullAccount();
        
        log.info("Account created successfully: {}", accountDetails);
        
        return accountDetails;
    }
}

