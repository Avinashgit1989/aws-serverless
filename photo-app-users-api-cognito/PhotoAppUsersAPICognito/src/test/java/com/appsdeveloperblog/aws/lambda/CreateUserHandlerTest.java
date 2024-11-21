package com.appsdeveloperblog.aws.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.appsdeveloperblog.aws.lambda.constant.Constant;
import com.appsdeveloperblog.aws.lambda.service.CognitoUserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateUserHandlerTest {

    @Mock
    CognitoUserService cognitoUserService;
    @Mock
    APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent;
    @Mock
    Context context;
    @Mock
    LambdaLogger lambdaLogger;
    @InjectMocks
    CreateUserHandler createUserHandler;
    @BeforeEach
    public void runBeforeEachTestMethod(){
        System.out.println("Executing @BeforeEach method");
        when(context.getLogger()).thenReturn(lambdaLogger);
    }
    @AfterEach
    public void runAfterEachTestMethod(){
        System.out.println("Executing @AfterEach method");
    }
    @BeforeAll
    public static void runBeforeAllTestMethod(){
        System.out.println("Executing @BeforeAll method");
    }
    @AfterAll
    public static void runAfterAllTestMethod(){
        System.out.println("Executing @AfterAll method");
    }
  @Test
public void testHandleRequest_whenValidDetailsProvided_returnSuccessfulResponse(){
      //Arrange or Given
      JsonObject userDetails = new JsonObject();
      userDetails.addProperty("firstName", "Avinash");
      userDetails.addProperty("lastName", "Tiwari");
      userDetails.addProperty("email", "aviansh@gmail.com");
      userDetails.addProperty("password", "Avinash123");

      String userDetailsJsonString = new Gson().toJson(userDetails, JsonObject.class);

        when(apiGatewayProxyRequestEvent.getBody()).thenReturn(userDetailsJsonString);
        //when(context.getLogger()).thenReturn(lambdaLogger);
      JsonObject creatUserResult = new JsonObject();
      creatUserResult.addProperty(Constant.IS_SUCCESSFUL, true);
      creatUserResult.addProperty(Constant.STATUS_CODE, 200);
      creatUserResult.addProperty(Constant.COGNITO_USER_ID, UUID.randomUUID().toString());
      creatUserResult.addProperty(Constant.IS_CONFIRMED, false);
        when(cognitoUserService.createUser(any(),any(),any())).thenReturn(creatUserResult);
      //Act or when
       APIGatewayProxyResponseEvent responseEvent=  createUserHandler.handleRequest(apiGatewayProxyRequestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject resonseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();

       //Assert or Then
      verify(lambdaLogger, times(1)).log(anyString());
      assertTrue(resonseBodyJson.get(Constant.IS_SUCCESSFUL).getAsBoolean());
      assertEquals(200, resonseBodyJson.get(Constant.STATUS_CODE).getAsInt());
      assertNotNull(resonseBodyJson.get(Constant.COGNITO_USER_ID).getAsString());
      assertEquals(200, responseEvent.getStatusCode());
      assertFalse(creatUserResult.get(Constant.IS_CONFIRMED).getAsBoolean());
      verify(cognitoUserService, times(1)).createUser(any(),any(),any());
    }

    @Test
    public void testHandleRequest_whenEmptyRequestBodyProvided_returnErrorMessage(){
        //Arrange
        when(apiGatewayProxyRequestEvent.getBody()).thenReturn("{}");

        //Act
        APIGatewayProxyResponseEvent responseEvent=  createUserHandler.handleRequest(apiGatewayProxyRequestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject resonseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();
        //Assert
        assertEquals(500, responseEvent.getStatusCode());
        assertNotNull(resonseBodyJson.get("message"), "Missing the 'message' property in JSON response");
        assertFalse(resonseBodyJson.get("message").getAsString().isEmpty(),"Error message should not be empty");
    }

   /* @Test
    public void testHandleRequest_whenAwsServiceExceptionTakePlace_returnErrorMessage(){
        //Arrange
        when(apiGatewayProxyRequestEvent.getBody()).thenReturn("{}");
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder()
                .errorCode("")
                .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                .errorMessage("Aws exception take place").build();
        when(cognitoUserService.createUser(any(),any(),any())).thenThrow(
                AwsServiceException.builder()
                        .statusCode(500)
                        .awsErrorDetails(awsErrorDetails)
                        .build()
        );

        //Act
        APIGatewayProxyResponseEvent responseEvent=  createUserHandler.handleRequest(apiGatewayProxyRequestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject resonseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();
        //Assert
        assertEquals(awsErrorDetails.sdkHttpResponse().statusCode(), responseEvent.getStatusCode());
        assertNotNull(resonseBodyJson.get("message"), "Missing the 'message' property in JSON response");
        assertEquals(awsErrorDetails.errorMessage(), resonseBodyJson.get("message").getAsString());
    }*/
}
