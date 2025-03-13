# Product App

## Setup Instructions

### Prerequisites

- Ensure you have **Docker** installed and running.
- Ensure you have **Maven** installed.
- Ensure you have **Java 17+** installed.

### Build the Application

To build the application, run the following command:

```sh
mvn clean install
```

This will:

- Generate necessary classes (e.g., `ProductController`, `ProductService`).
- Run tests (Docker must be running for tests to pass).

If you want to skip tests, use:

```sh
mvn clean install -DskipTests
```

### Start Required Services (PostgreSQL & Redis)

Before starting the application, start the required services using Docker:

```sh
docker-compose up -d
```

This will start a PostgreSQL database and a Redis instance.

### Set Environment Variables

Before starting the application, set the required environment variables:

```sh
export SPRING_APPLICATION_JSON='{
   "spring.datasource.url":"jdbc:postgresql://localhost:5432/mydb",
   "spring.datasource.username":"myuser",
   "spring.datasource.password":"mypassword",
   "spring.data.redis.host":"localhost",
   "spring.data.redis.port":6379,
   "jwt.secret-key":"dlE3Oi7oCB1gPpxBYHoALcZCnt12XumT/xl1uS7VUs8="
}'
```

### Start the Application

Now you can start the Spring Boot application:

```sh
mvn spring-boot:run
```

## API Documentation

Once the application is running, you can access the API documentation at: [Swagger UI](http://localhost:8080/swagger-ui/index.html#/)

## Authentication

Before accessing protected endpoints, you need to authenticate.

### Login

Make a POST request to:

```
http://localhost:8080/auth/login
```

with the following JSON body:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

This will return a **Bearer token**, which you should use for subsequent requests.

## Product API

Once authenticated, you can interact with the `Product` API using the following endpoint:

```
http://localhost:8080/api/v1/products
```

### Supported Operations:

- **GET** - Retrieve a list of products
- **POST** - Create a new product
- **PUT** - Update an existing product
- **DELETE** - Delete a product

Refer to the Swagger documentation for full details on request and response formats.

