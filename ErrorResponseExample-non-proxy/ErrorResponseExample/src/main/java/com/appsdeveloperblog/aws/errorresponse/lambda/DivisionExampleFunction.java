package com.appsdeveloperblog.aws.errorresponse.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class DivisionExampleFunction implements RequestHandler<Map<String, String>, Map<String,Integer>> {

    public Map<String,Integer> handleRequest(final Map<String,String> input, final Context context) {
        Map<String,Integer> response = new HashMap<>();
        try {
            int dividend = Integer.parseInt(input.get("dividend"));
            int divisor = Integer.parseInt(input.get("divisor"));
            int result = (int) dividend / divisor;

            response.put("dividend", dividend);
            response.put("divisor", divisor);
            response.put("result", result);
        }catch(Exception e){
            throw new CustomException("Exception: "+ e.getMessage(), e.getCause());
        }
        return response;

    }

}
