package com.jovanovicbogdan.auticparkic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

//  @Bean
//  CommandLineRunner commandLineRunner(final S3Service service, final S3Buckets buckets) {
//    return args -> {
//      service.putObject(buckets.getVehicles(), "test.txt", "Hello World!".getBytes());
//      System.out.println(new String(service.getObject(buckets.getVehicles(), "test.txt")));
//    };
//  }
}
