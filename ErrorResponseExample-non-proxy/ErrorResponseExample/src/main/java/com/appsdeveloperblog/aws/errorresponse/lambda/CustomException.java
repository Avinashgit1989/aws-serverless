package com.appsdeveloperblog.aws.errorresponse.lambda;

public class CustomException extends RuntimeException{
    public CustomException(String errorMessage, Throwable cause){
        super(errorMessage, cause);
    }
}
