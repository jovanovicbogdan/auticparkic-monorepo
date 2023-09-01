# Java Spring Boot Project

- Application for managing rides asynchronously and the costs per each drive based on elapsed time

### Build the Application

```
./gradlew build
```

### Run the JAR Task

```
./gradlew jar
```

### Verify that the Application JAR is Valid

```
jar tf build/libs/autic-parkic-1.0.0.jar
```

### Manifest File

- Set manifest info

```gradle
tasks.jar {
	manifest {
		attributes(mapOf("Implementation-Title" to project.name,
						 "Implementation-Version" to project.version))
	}
}
```

- Unpack the manifest file from the jar

```
jar xf build/libs/autic-parkic-1.0.0.jar META-INF/MANIFEST.MF
```

### Run JAR

```
java -jar build/libs/autic-parkic-1.0.0.jar
```

### Build an OCI Image of the Application

```
./gradlew bootBuildImage
```

#### TODO Backend

- [ ] Add Swagger
- [ ] Add logging - for controllers use INFO, for services use INFO, and for repositories use DEBUG level

#### TODO Frontend

- [ ] Add notifications for errors

#### Docker Format

```shell
export FORMAT=ID\t{{.ID}}\nNAME\t{{.Names}}\nIMAGE\t{{.Image}}\nPORTS\t{{.Ports}}\nCOMMAND\t{{.Command}}\nCREATED\t{{.CreatedAt}}\nSTATUS\t{{.Status}}\n
```
