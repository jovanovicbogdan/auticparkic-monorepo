# 🚗 Autić Parkić Application

The application must ensure real-time sync with multiple client instances, displaying consistent elapsed time for rides in RUNNING status across all clients. This demands a real-time communication mechanism between the server and all connected clients to maintain a consistent state.

## 🔗 [Live Application](https://auticparkic.bogdanjovanovic.dev/) | 📝 [Swagger API Documentation](https://auticparkic-api.bogdanjovanovic.dev/)

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3
- WebSockets for real-time communication
- PostgreSQL 15.4
- Flyway for seamless database migrations
- Docker for containerization and deployment
- AWS for cloud-based infrastructure
- React with TypeScript for frontend development
- Package by Feature for project structure

## Application Workflow

Users can register a vehicle, which has a distinct name in the database and an associated image 
stored on AWS S3. Once a vehicle is registered, rides can be initiated. It's important 
to note that a vehicle can only be engaged in a single ride at a given time. Rides transition 
through several distinct statuses: CREATED, RUNNING, PAUSED, STOPPED, and FINISHED. Initially, 
a ride is in the CREATED state. Once RUNNING, it can either be PAUSED or STOPPED. When a ride is 
PAUSED, it can be resumed by returning to the RUNNING state. Conversely, a STOPPED ride offers 
two choices: it can either restart or advance to its FINISHED state. Opting to restart concludes 
the current ride and launches a new one using the same vehicle. Once designated as FINISHED, a 
ride's status is fixed and unalterable. A key function is the real-time tabulation and distribution 
of the elapsed ride time once it's RUNNING. This time calculation disregards any PAUSED intervals. 
To facilitate this, the database stores arrays of started_at and paused_at timestamps. The computed 
elapsed time is then shared in real-time to all connected clients using the STOMP WebSocket 
protocol. Considering the application's need to relay elapsed time updates every second, ongoing 
rides are temporarily cached in system memory (RAM) using Collections.synchronizedList. This 
strategy helps avoid recurrent database queries. Given the cap of 20 active rides at any 
instance, there's no compelling reason to employ comprehensive distributed caching systems like 
Redis or memcached.

## License

[MIT License](https://choosealicense.com/licenses/mit/)
