package com.aws.rest.lambda.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.springframework.stereotype.Service;

@Service
public class LambdaService {
    private final AWSLambda lambdaClient;

    public LambdaService() {
        this.lambdaClient = AWSLambdaClientBuilder.defaultClient();
    }

public String invokeLambda(String functionName, String payload){
    InvokeRequest invokeRequest = new InvokeRequest()
            .withFunctionName(functionName)
            .withPayload(payload);
    InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);
    return new String(invokeResult.getPayload().array());
}
}
