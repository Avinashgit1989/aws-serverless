package com.aws.s3.services.handlers.fileRead;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class S3FileReadHandler implements RequestHandler<Map<String, String>, String> {

    private final AmazonS3 s3Client;
    public S3FileReadHandler() {
        this.s3Client = AmazonS3ClientBuilder.standard().build();
    }


    @Override
    public String handleRequest(Map<String, String> inputRequest, Context context) {

        LambdaLogger logger = context.getLogger();
        String bucketName= inputRequest.get("bucketName");
        String keyName = inputRequest.get("keyName");

        try {
            S3Object s3Object = s3Client.getObject(bucketName, keyName);
            InputStream objectData = s3Object.getObjectContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(objectData));
               String line;
               StringBuilder builder = new StringBuilder();
               while ((line= bufferedReader.readLine())!= null){
                   builder.append(line).append("\n");
               }

            logger.log("successfully read data from S3 file :: "+builder.toString());
            return builder.toString();
        }catch (Exception e){
            logger.log("Error while reading file on S3 :: "+ e.getMessage());
            return "Unable to read file to S3";
        }
    }
}
