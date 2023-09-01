package com.jovanovicbogdan.auticparkic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersTest extends AbstractTestcontainers {

  @Test
  void canStartPostgresContainer() {
    assertThat(postgreSQLContainer.isRunning()).isTrue();
    assertThat(postgreSQLContainer.isCreated()).isTrue();
  }

}
