package com.jovanovicbogdan.auticparkic.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

  @Mock
  private S3Client s3Client;
  private S3Service underTest;

  @BeforeEach
  public void setUp() {
    underTest = new S3Service(s3Client);
  }

  @Test
  @Tag("unit")
  public void canPutObject() throws IOException {
    // given
    final String bucket = "vehicles";
    final String key = "vehicle1";
    final byte[] data = "some data".getBytes();

    // when
    underTest.putObject(bucket, key, data);

    // then
    final ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor =
        ArgumentCaptor.forClass(PutObjectRequest.class);

    final ArgumentCaptor<RequestBody> requestBodyArgumentCaptor =
        ArgumentCaptor.forClass(RequestBody.class);

    verify(s3Client).putObject(
        putObjectRequestArgumentCaptor.capture(),
        requestBodyArgumentCaptor.capture()
    );

    final PutObjectRequest putObjectRequestArgumentCaptorValue =
        putObjectRequestArgumentCaptor.getValue();

    assertThat(putObjectRequestArgumentCaptorValue.bucket()).isEqualTo(bucket);
    assertThat(putObjectRequestArgumentCaptorValue.key()).isEqualTo(key);

    final RequestBody requestBodyArgumentCaptorValue =
        requestBodyArgumentCaptor.getValue();

    assertThat(requestBodyArgumentCaptorValue.contentStreamProvider().newStream()
        .readAllBytes()).isEqualTo(
        RequestBody.fromBytes(data).contentStreamProvider().newStream().readAllBytes());
  }

  @Test
  @Tag("unit")
  public void canGetObject() throws IOException {
    // given
    final String bucket = "vehicles";
    final String key = "vehicle1";
    final byte[] data = "some data".getBytes();

    final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    final ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
    when(res.readAllBytes()).thenReturn(data);
    when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

    // when
    final byte[] result = underTest.getObject(bucket, key);

    // then
    assertThat(result).isEqualTo(data);
  }

  @Test
  @Tags({@Tag("unit"), @Tag("s3")})
  public void willThrowWhenGetObject() throws IOException {
    // given
    final String bucket = "vehicles";
    final String key = "vehicle1";

    final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    final ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
    when(res.readAllBytes()).thenThrow(new IOException("some error"));
    when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

    // when
    // then
    assertThatThrownBy(() -> underTest.getObject(bucket, key))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("java.io.IOException: some error")
        .hasRootCauseInstanceOf(IOException.class);
  }

}
