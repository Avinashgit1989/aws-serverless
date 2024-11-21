package com.aws.rest.lambda.controller;

import com.aws.rest.lambda.service.LambdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lambda")
public class LambdaController {
    @Autowired
    LambdaService lambdaService;
    @PostMapping("/invoke")
    public String invokeLambda(@RequestParam String functionName, @RequestBody String payload){
        return lambdaService.invokeLambda(functionName, payload);
    }
}
