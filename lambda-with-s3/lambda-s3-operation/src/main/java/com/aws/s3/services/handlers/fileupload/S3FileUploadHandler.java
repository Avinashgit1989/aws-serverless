package com.aws.s3.services.handlers.fileupload;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class S3FileUploadHandler implements RequestHandler<Map<String, String>, String> {

    private final AmazonS3 s3Client;
    public S3FileUploadHandler() {
        this.s3Client = AmazonS3ClientBuilder.standard().build();
    }
    @Override
    public String handleRequest(Map<String, String> inputRequest, Context context) {
        LambdaLogger logger = context.getLogger();
        String bucketName= inputRequest.get("bucketName");
        String keyName = inputRequest.get("keyName");
        String fileContent = inputRequest.get("fileContent");
        try {
            byte[] bytes = fileContent.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(bytes.length);
            PutObjectResult result = s3Client.putObject(bucketName,keyName,inputStream, metaData);
            logger.log("S3 result :: "+result);
            return "File uploaded successfully to S3";
        }catch (Exception e){
            logger.log("Error while uploading file on S3 :: "+ e.getMessage());
            return "Unable to upload file to S3";
        }
    }
}
