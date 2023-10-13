## How to Run Locally
- Clone the repo
- Run `docker-compose up -d` to start local PostgreSQL database on port 5432 
- Create `autic_parkic` database
- Run `./gradlew bootRun` in `backend` directory to start the backend on port 10000
- Run `npm install` in `frontend` directory to install dependencies
- Run `npm run start:dev` in `frontend` directory to start the frontend on port 3000

## AWS Beanstalk Environment Configuration

- Connect to EC2 instance via SSH. Assuming key pair is already created, run:
```shell
ssh -i /path/to/keypair.pem ec2-user@EC2_INSTANCE_PUBLIC_IP
```

- We need `psql` to connect to the database, so create ephemeral docker container:

```shell
docker run --rm -it postgres:alpine bash
```

- Connect to RDS database via `psql`:

```shell
psql -U postgres -d postgres -h awseb-e-hpqaxacpv6-stack-awsebrdsdatabase-dafkdvlt0qvd.cncflnnvajsx.eu-central-1.rds.amazonaws.com
```

## Build Frontend Docker Image (for AWS Beanstalk)

- **Note**: Frontend won't be deployed to AWS Beanstalk it's used only for testing the integration between frontend and backend

```shell
docker build --no-cache --build-arg="API_BASE_URL=http://auticparkic-api-test.eba-jtrhurmp.eu-central-1.elasticbeanstalk.com/api" --build-arg="WS_BASE_URL=ws://auticparkic-api-test.eba-jtrhurmp.eu-central-1.elasticbeanstalk.com/ws" -t bogdanjovanovic/auticparkic-frontend .
```

- Push to Docker Hub

```shell
docker push bogdanjovanovic/auticparkic-frontend:latest
```

## TODO Backend

- [x] ~~Add Swagger UI~~
- [x] ~~Add logging - for controllers use INFO, for services use INFO, and for repositories use DEBUG level~~
- [x] ~~Upon scheduling a task to send rides data, do not read every time from a database but rather from a cache (it can be simple List as temporary solution, mind the memory!)~~
- [ ] ~~Convert REST API endpoint `/api/v1/vehicles/available` to WebSocket~~ - Won't Do
- [ ] ~~Convert REST API endpoint `/api/v1/vehicles/{vehicleId}` to WebSocket (check if possible since this REST endpoint will be used in the dashboard)~~ - Won't Do
- [ ] Handle case when deleting a vehicle in use
- [ ] Handle deleting vehicles written in ride database table (db constraints)
- [ ] Add deleteObject functionality for s3 service
- [x] ~~Turn off mocking S3 in test environment~~
- [x] ~~Fix ride price calculator (it's not working properly when incorporating hours in calculation)~~

## TODO Frontend

- [ ] Add delete functionality for vehicles in /dashboard page once it's ready on the backend
- [x] ~~Turn off React Strict Mode~~ - Won't Do (AbortController is used to cancel fetch requests on unmount)
- [x] ~~Fix Dashboard page, not loading vehicle status badge~~
- [x] ~~Handle 404 page in React Router~~
- [x] ~~Add when the ride has started and when it has ended~~
- [x] ~~Add notifications/toasts for errors~~
- [x] ~~Add loader/spinner~~
