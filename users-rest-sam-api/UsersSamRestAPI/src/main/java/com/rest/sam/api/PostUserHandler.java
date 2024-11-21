package com.rest.sam.api;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class PostUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{


    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Start Executing handle Request method for POST operation to add users");
        String requestBody = apiGatewayProxyRequestEvent.getBody();
        Gson gson = new Gson();
        Map<String, String> userDetails = gson.fromJson(requestBody, Map.class);
        userDetails.put("userId", UUID.randomUUID().toString());
        //TODO: process userDetails to persist in db
        Map returnValue = new HashMap();
        returnValue.put("userId", userDetails.get("userId"));
        returnValue.put("firstname", userDetails.get("firstname"));
        returnValue.put("lastname", userDetails.get("lastname"));

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map responseHeader = new HashMap();
        responseHeader.put("Content-Type", "application/json");
        response.withStatusCode(200)
                .withBody(gson.toJson(returnValue, Map.class))
                .withHeaders(responseHeader);
        logger.log("End Executing handle Request method for POST operation to add users");
        return response;
    }
}
