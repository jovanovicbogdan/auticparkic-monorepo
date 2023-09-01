package com.jovanovicbogdan.auticparkic.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    servers = {
        @Server(url = "http://localhost:10000", description = "Local server"),
        @Server(url = "https://auticparkic.herokuapp.com", description = "Heroku server")
    },
    info = @Info(
    contact = @Contact(
        name = "Bogdan Jovanovic",
        email = "bogdan.jovanovic@hotmail.com"
    ),
    description = "This is an application for managing electric car rides.",
    title = "AuticParkic API",
    version = "1.0.0"
))

public class OpenApiConfig { }
