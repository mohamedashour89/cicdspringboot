package com.example.demo.controller;

import com.example.demo.service.SalesforceApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalesforceController {

    @Autowired
    private SalesforceApiService apiService;

    @PostMapping("/createAccount")
    public Map createAccount() throws Exception { // create account and return full details of the created account
        return apiService.createAndReturnFullAccount();
    }
}

