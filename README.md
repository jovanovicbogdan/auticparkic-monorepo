# 🚗 Autić Parkić Application

This application handles real-time ride tracking, allowing users to stream elapsed time for rides and manage vehicle-related operations.

🔗 [Live Application](http://auticparkic-api-test.eba-jtrhurmp.eu-central-1.elasticbeanstalk.com/)
📝 [Swagger API Documentation](http://auticparkic-api-test.eba-jtrhurmp.eu-central-1.elasticbeanstalk.com:10000/swagger-ui/index.html)

## 🌟 Features
- 🕐 Real-time ride tracking using WebSockets
- 🚀 Efficient ride time calculation leveraging system memory caching
- 🚗 Comprehensive vehicle management - from creation to image upload
- 📜 High-quality documentation with OpenAPI specification

## 💾 Backend Technologies & Dependencies
- **Java Spring Boot**: A robust backend framework chosen for its mature ecosystem, efficient performance, and vast enterprise features.
- **PostgreSQL**: Our go-to relational database, chosen for its reliable locking mechanisms.
- **AWS S3**: For secure and scalable file storage.

### Major Dependencies:
- `spring-boot-starter-web`: For creating web applications.
- `spring-boot-starter-data-jdbc`: To connect with relational databases.
- `spring-boot-starter-websocket`: For handling WebSocket connections.
- `springdoc-openapi-starter-webmvc-ui`: For OpenAPI documentation.
- `testcontainers`: For integration testing with real containers.

## 🔄 CI/CD
- **GitHub Actions**: A powerful CI/CD tool ensuring consistent builds and deployments.

## 🖥️ Frontend Technologies
- **React.js with TypeScript**: Chosen for the complexity of the UI required on the client-side, aiding in maintaining a dynamic state.

## ⏳ Real-Time Streaming Elapsed Time

Given the application's requirement to stream elapsed time (by publishing to a message broker) every second, we'll cache temporary `started_at`, `resumed_at`, and `paused_at` values in system memory (RAM) to bypass constant database reads. As the active rides won't exceed 20 at any given moment, leveraging extensive distributed caching solutions like Redis or memcached isn't necessary.
