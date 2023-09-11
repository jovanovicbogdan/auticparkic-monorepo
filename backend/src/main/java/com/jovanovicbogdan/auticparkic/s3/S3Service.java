package com.jovanovicbogdan.auticparkic.s3;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

  private static final Logger log = LoggerFactory.getLogger(S3Service.class);
  private final S3Client s3Client;

  public S3Service(final S3Client s3Client) {
    this.s3Client = s3Client;
  }

  public void putObject(final String bucketName, final String key, byte[] file) {
    final PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();
    log.info("Attempting to upload file: {} to bucket: {}", key, bucketName);
    s3Client.putObject(objectRequest, RequestBody.fromBytes(file));
  }

  public byte[] getObject(final String bucketName, final String key) {
    final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();
    final ResponseInputStream<GetObjectResponse> res = s3Client.getObject(getObjectRequest);

    try {
      log.info("Successfully downloaded file: {} from bucket: {}", key, bucketName);
      return res.readAllBytes();
    } catch (IOException e) {
      log.error("Failed to download file: {} from bucket: {}", key, bucketName);
      throw new RuntimeException(e);
    }
  }
}
