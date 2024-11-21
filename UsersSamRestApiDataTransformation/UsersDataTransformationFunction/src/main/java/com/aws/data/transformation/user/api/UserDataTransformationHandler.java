package com.aws.data.transformation.user.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class UserDataTransformationHandler implements RequestHandler<Map<String, String>, Map<String,String>> {

    public Map<String,String> handleRequest(final Map<String, String> input, final Context context) {
        LambdaLogger logger = context.getLogger();
        Map<String,String> response = new HashMap<>();
        logger.log("Start executing handler method :: UserDataTransformationHandler");
            String firstName= input.get("firstName");
            String lastName= input.get("lastName");
            String email= input.get("email");
            String password= input.get("password");
            String repeatPassword= input.get("repeatPassword");

            logger.log("\n firstName : "+firstName);
            logger.log("\n lastName : "+lastName);
            logger.log("\n email : "+email);
            logger.log("\n password : "+password);
            logger.log("\n repeatPassword : "+repeatPassword);

            response.put("userId", UUID.randomUUID().toString());
            response.put("firstName", firstName);
            response.put("lastName", lastName);
            response.put("email", email);
        logger.log("End executing handler method :: UserDataTransformationHandler");

        return response;
    }

}

