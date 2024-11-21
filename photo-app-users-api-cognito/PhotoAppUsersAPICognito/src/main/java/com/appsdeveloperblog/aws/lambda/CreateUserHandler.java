package com.appsdeveloperblog.aws.lambda;

import java.util.HashMap;
import java.util.Map;

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
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;

/**
 * Handler for requests to Lambda function.
 */
public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;
    private final String appClientId;
    private final String appClientSecret;

    public CreateUserHandler(CognitoUserService cognitoUserService,
                             String appClientId,
                             String appClientSecret) {
        this.cognitoUserService = cognitoUserService;
        this.appClientId = appClientId;
        this.appClientSecret = appClientSecret;
    }

    public CreateUserHandler(){
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientId = Utils.decryptKey("MY_COGNITO_POOL_APP_CLIENT_ID");
        this.appClientSecret = Utils.decryptKey("MY_COGNITO_POOL_APP_CLIENT_SECRET");

    }
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        LambdaLogger logger = context.getLogger();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        String requestBody = input.getBody();
        logger.log("Original request body :"+requestBody);

       try {
           JsonObject userDetailsAsJson = JsonParser.parseString(requestBody).getAsJsonObject();
           JsonObject createUserResult = cognitoUserService.createUser(userDetailsAsJson, appClientId, appClientSecret);
           response.withStatusCode(200);
           response.withBody(new Gson().toJson(createUserResult, JsonObject.class));
       }catch (AwsServiceException exception){
           logger.log(exception.awsErrorDetails().errorMessage());
           response.withStatusCode(500);
           response.withBody(exception.awsErrorDetails().errorMessage());

       }
        return response;

    }
}
