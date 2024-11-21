package com.lambda.api.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;

import java.util.Map;

public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, String> pathParameters = input.getPathParameters();
        String userId = pathParameters.get("userId");
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        JsonObject json = new JsonObject();
        json.addProperty("firstname","Avinash");
        json.addProperty("lastname","Tiwari");
        json.addProperty("id",userId);
        responseEvent.withStatusCode(200).withBody(json.toString());
        return responseEvent;
    }
}
