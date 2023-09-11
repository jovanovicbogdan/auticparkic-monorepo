package com.jovanovicbogdan.auticparkic.common;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class Constants {

  public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
      .append(DateTimeFormatter.ISO_LOCAL_DATE)
      .appendLiteral(' ')
      .append(DateTimeFormatter.ISO_LOCAL_TIME)
      .toFormatter();
  public static final String WEBSOCKET_ENDPOINT = "/ws";
  public static final String WEBSOCKET_APPLICATION_DESTINATION_PREFIX = "/app";
  public static final String WEBSOCKET_BROKER = "/topic";
  public static final String WEBSOCKET_BROKER_PUBLIC = "/topic/public";

}
