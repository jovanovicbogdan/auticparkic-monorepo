# Java Spring Boot Project

- Application for managing rides asynchronously and the costs per each drive based on elapsed time

### TODO Backend

- [x] Add Swagger
- [x] Add logging - for controllers use INFO, for services use INFO, and for repositories use DEBUG level

### TODO Frontend

- [ ] Add notifications for errors

#### Docker Format

```shell
export FORMAT=ID\t{{.ID}}\nNAME\t{{.Names}}\nIMAGE\t{{.Image}}\nPORTS\t{{.Ports}}\nCOMMAND\t{{.Command}}\nCREATED\t{{.CreatedAt}}\nSTATUS\t{{.Status}}\n
```
