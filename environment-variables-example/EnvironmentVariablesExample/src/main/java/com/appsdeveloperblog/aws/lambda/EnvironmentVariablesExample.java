package com.appsdeveloperblog.aws.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.Base64;

/**
 * Handler for requests to Lambda function.
 */
public class EnvironmentVariablesExample implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final String myVariable = decryptKey("My_VARIABLE");
    private final String myCognitoUserPoolId = decryptKey("MY_COGNITO_USER_POOL_ID");
    private final String myCognitoAppSecret = decryptKey("My_COGNITO_APP_SECRET");
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        LambdaLogger logger = context.getLogger();
        /*String myVariable = System.getenv("My_VARIABLE");
        String myCognitoUserPoolId = System.getenv("MY_COGNITO_USER_POOL_ID");
        String myCognitoAppSecret = System.getenv("My_COGNITO_APP_SECRET");*/
        logger.log("\n My_VARIABLE: "+myVariable);
        logger.log("\n MY_COGNITO_USER_POOL_ID: "+myCognitoUserPoolId);
        logger.log("\n My_COGNITO_APP_SECRET: "+myCognitoAppSecret);


        return response
                .withBody("{}")
                .withStatusCode(200);

    }
    private String decryptKey(String variable) {
        System.out.println("Decrypting key");
        byte[] encryptedKey = Base64.decode(System.getenv(variable));
        Map<String, String> encryptionContext = new HashMap<>();
        encryptionContext.put("LambdaFunctionName",
                System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey))
                .withEncryptionContext(encryptionContext);

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }
}
