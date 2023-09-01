package com.jovanovicbogdan.auticparkic.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
public class S3Buckets {

  private String vehicles;

  public String getVehicles() {
    return vehicles;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }
}
