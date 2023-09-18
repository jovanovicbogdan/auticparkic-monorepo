# React.js and Java Spring Boot Project

- Application for managing rides asynchronously and the costs per each drive based on elapsed time

## TODO Backend

- [x] Add Swagger
- [x] Add logging - for controllers use INFO, for services use INFO, and for repositories use DEBUG level
- [ ] Upon scheduling a task to send rides data, do not read every time from a database but rather from a cache (it can be simple HashMap as temporary solution)
- [ ] Convert REST API endpoint `/api/v1/vehicles/available` to WebSocket
- [ ] Convert REST API endpoint `/api/v1/vehicles/{vehicleId}` to WebSocket
- [ ] Handle case where deleting a vehicle in use 
- [ ] Handle deleting vehicles written in ride database table (db constraints)
- [ ] Add deleteObject functionality for s3 service

## TODO Frontend

- [ ] Add delete functionality for vehicles in /dashboard page
- [ ] Add when the ride has started and when it has ended
- [ ] Add notifications for errors
- [ ] Add loader/spinner

## AWS Beanstalk

- We need `psql` to connect to the database, so create docker container with `psql` installed

```shell
docker run --rm -it postgres:alpine bash
```

- To create database in RDS first connect to the database using `psql`

```shell
psql -U postgres -d postgres -h awseb-e-hpqaxacpv6-stack-awsebrdsdatabase-dafkdvlt0qvd.cncflnnvajsx.eu-central-1.rds.amazonaws.com
```

## Build Frontend Docker Image

```shell
docker build --no-cache --build-arg="API_BASE_URL=http://auticparkic-api-test.eba-jtrhurmp.eu-central-1.elasticbeanstalk.com:10000/api" --build-arg="WS_BASE_URL=ws://auticparkic-api-test.eba-jtrhurmp.eu-central-1.elasticbeanstalk.com:10000/ws" -t bogdanjovanovic/auticparkic-frontend .
```

- Push to Docker Hub

```shell
docker push bogdanjovanovic/auticparkic-frontend:latest
```

### Docker Format

```shell
export FORMAT=ID\t{{.ID}}\nNAME\t{{.Names}}\nIMAGE\t{{.Image}}\nPORTS\t{{.Ports}}\nCOMMAND\t{{.Command}}\nCREATED\t{{.CreatedAt}}\nSTATUS\t{{.Status}}\n
```
