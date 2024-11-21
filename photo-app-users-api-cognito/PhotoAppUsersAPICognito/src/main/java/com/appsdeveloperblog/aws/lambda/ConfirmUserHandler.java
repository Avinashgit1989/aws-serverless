package com.appsdeveloperblog.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.appsdeveloperblog.aws.lambda.service.CognitoUserService;
import com.appsdeveloperblog.aws.lambda.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class ConfirmUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CognitoUserService cognitoUserService;
    private final String appClientId;
    private final String appClientSecret;
    public ConfirmUserHandler(){
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientId = Utils.decryptKey("MY_COGNITO_POOL_APP_CLIENT_ID");
        this.appClientSecret = Utils.decryptKey("MY_COGNITO_POOL_APP_CLIENT_SECRET");

    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try{
            String requestBody = input.getBody();
            logger.log("Original request body :"+requestBody);
            JsonObject userDetailsAsJson = JsonParser.parseString(requestBody).getAsJsonObject();
            String email = userDetailsAsJson.get("email").getAsString();
            String confirmationCode = userDetailsAsJson.get("code").getAsString();


            JsonObject confirmUserResult = cognitoUserService.confirmUserSignUp(appClientId,appClientSecret, email, confirmationCode);
            responseEvent.withStatusCode(200);
            responseEvent.withBody(new Gson().toJson(confirmUserResult, JsonObject.class));
        }catch (AwsServiceException exception){
            logger.log(exception.awsErrorDetails().errorMessage());
            responseEvent.withBody(exception.awsErrorDetails().errorMessage());
        }catch (Exception exception){
            logger.log(exception.getMessage());
            responseEvent.withBody(exception.getMessage());
            responseEvent.withStatusCode(500);

        }
        return responseEvent;
    }
}
