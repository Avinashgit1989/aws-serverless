package com.appsdeveloperblog.aws.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.appsdeveloperblog.aws.lambda.error.ErrorResponse;
import com.appsdeveloperblog.aws.lambda.service.CognitoUserService;
import com.appsdeveloperblog.aws.lambda.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class AddUserToGroupHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;
    private final String userPoolId;
    public AddUserToGroupHandler(){
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.userPoolId = Utils.decryptKey("MY_COGNITO_POOL_ID");

    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            JsonObject requestBody = JsonParser.parseString(input.getBody()).getAsJsonObject();
            String groupName = requestBody.get("group").getAsString();
            String userName = input.getPathParameters().get("userName");
            JsonObject addUserToGroupResult = cognitoUserService.addUserToGroup(groupName, userName, userPoolId);
       responseEvent.withBody(new Gson().toJson(addUserToGroupResult, JsonObject.class));
       responseEvent.withStatusCode(200);
        }catch (AwsServiceException exception){
            logger.log(exception.awsErrorDetails().errorMessage());
            ErrorResponse errorResponse = new ErrorResponse(exception.awsErrorDetails().errorMessage());
            String errorResponseJsonString = new Gson().toJson(errorResponse, ErrorResponse.class);
            responseEvent.withBody(errorResponseJsonString);
        }catch (Exception exception){
            logger.log(exception.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            //String errorResponseJsonString = new Gson().toJson(errorResponse, ErrorResponse.class);
            String errorResponseJsonString = new GsonBuilder().serializeNulls()
                    .create().toJson(errorResponse, ErrorResponse.class);
            responseEvent.withBody(errorResponseJsonString);
            responseEvent.withStatusCode(500);

        }
        return responseEvent;
    }
}
