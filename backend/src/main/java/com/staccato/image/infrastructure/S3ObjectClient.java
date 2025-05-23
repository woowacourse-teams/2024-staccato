package com.staccato.image.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3ObjectClient {
    private final S3Client s3Client;
    private final String bucketName;
    private final String endPoint;
    private final String cloudFrontEndPoint;

    public S3ObjectClient(
            @Value("${cloud.aws.s3.bucket}") String bucketName,
            @Value("${cloud.aws.s3.endpoint}") String endPoint,
            @Value("${cloud.aws.cloudfront.endpoint}") String cloudFrontEndPoint,
            @Value("${cloud.aws.access-key}") String accessKey,
            @Value("${cloud.aws.secret-access-key}") String secretAccessKey
    ) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.AP_NORTHEAST_2)
                .build();
        this.bucketName = bucketName;
        this.endPoint = endPoint;
        this.cloudFrontEndPoint = cloudFrontEndPoint;
    }

    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }

    public String getUrl(String keyName) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        String url = s3Client.utilities().getUrl(request).toString();

        return url.replace(endPoint, cloudFrontEndPoint);
    }
}
