# Twitter-like Social Media Application

A RESTful API for a Twitter-like social media platform built with Spring Boot, featuring user management, posts, comments, likes, and following functionality.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Local Development](#local-development)
  - [Docker Deployment](#docker-deployment)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Configuration](#configuration)
- [Monitoring and Observability](#monitoring-and-observability)
- [Architecture Decisions](#architecture-decisions)

## Features

- **User Management**
  - User registration and authentication
  - JWT-based authentication
  - User profile management
  - User following/followers system

- **Posts**
  - Create, read, update, and delete posts
  - Pagination support
  - View posts from followed users
  - View user's own posts

- **Comments**
  - Add comments to posts
  - View comments for a post
  - Update and delete own comments

- **Likes**
  - Like and unlike posts
  - View like count for posts

- **Caching**
  - Redis-based caching for improved performance
  - Cache invalidation strategies

- **Monitoring**
  - Health checks via Spring Boot Actuator
  - Prometheus metrics
  - Distributed tracing with Zipkin

## Technology Stack

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 21, Groovy 5.0.2
- **Build Tool**: Gradle 9.2.1
- **Database**: MongoDB 7.0
- **Cache**: Redis 7.2
- **Security**: Spring Security with JWT
- **API Documentation**: SpringDoc OpenAPI 3.0
- **Testing**: Spock Framework, Testcontainers
- **Mapping**: MapStruct 1.5.5
- **Resilience**: Resilience4j (Circuit Breaker)
- **Monitoring**: Micrometer, Prometheus, Zipkin

## Prerequisites

- Java 21 or higher
- Gradle 9.2.1 (or use the included Gradle Wrapper)
- Docker and Docker Compose (for containerized deployment)
- MongoDB 7.0 (if running locally without Docker)
- Redis 7.2 (if running locally without Docker)

## Getting Started

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd twitter
   ```

2. **Start MongoDB and Redis**
   
   Option A: Using Docker Compose (recommended)
   ```bash
   docker-compose up -d mongodb redis
   ```
   
   Option B: Install and run locally
   - MongoDB: Ensure MongoDB is running on `localhost:27017`
   - Redis: Ensure Redis is running on `localhost:6379`

3. **Configure the application**
   
   Create a `.env` file in the project root (optional, defaults are provided):
   ```bash
   cp .env.example .env
   # Edit .env with your configuration values
   ```
   
   Or set environment variables directly:
   - MongoDB: `MONGO_HOST`, `MONGO_PORT`, `MONGO_USERNAME`, `MONGO_PASSWORD`, `MONGO_DATABASE`
   - Redis: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
   - JWT: `JWT_SECRET` (required for production)
   - Server: `SERVER_PORT`

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run the application**
   ```bash
   ./gradlew bootRun
   ```
   
   Or using the JAR:
   ```bash
   java -jar build/libs/twitter-0.0.1-SNAPSHOT.jar
   ```

6. **Access the application**
   - API Base URL: `http://localhost:8080`
   - API Documentation: `http://localhost:8080/swagger-ui.html`
   - Health Check: `http://localhost:8080/actuator/health`
   - Metrics: `http://localhost:8080/actuator/metrics`
   - Prometheus: `http://localhost:8080/actuator/prometheus`

### Docker Deployment

1. **Build and start all services**
   ```bash
   docker-compose up -d
   ```

2. **Check service status**
   ```bash
   docker-compose ps
   ```

3. **View logs**
   ```bash
   docker-compose logs -f app
   ```

4. **Stop services**
   ```bash
   docker-compose down
   ```

5. **Stop and remove volumes**
   ```bash
   docker-compose down -v
   ```

The application will be available at `http://localhost:8080` with MongoDB and Redis automatically configured.

## API Documentation

Interactive API documentation is available via Swagger UI once the application is running:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Authentication

Most endpoints require JWT authentication. To authenticate:

1. Register a new user:
   ```bash
   POST /api/v1/auth/registration
   {
     "username": "johndoe",
     "email": "john@example.com",
     "password": "password123"
   }
   ```

2. Login to get a JWT token:
   ```bash
   POST /api/v1/auth/login
   {
     "email": "john@example.com",
     "password": "password123"
   }
   ```

3. Use the token in subsequent requests:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

### Main Endpoints

- **Authentication**: `/api/v1/auth/*`
- **Users**: `/api/v1/users/*`
- **Posts**: `/api/v1/posts/*`
- **Comments**: `/api/v1/comments/*`
- **Likes**: `/api/v1/likes/*`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── koval/proxyseller/twitter/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── exception/       # Exception handlers
│   │       ├── mapper/          # MapStruct mappers
│   │       ├── model/           # Domain models
│   │       ├── monitoring/      # Monitoring configuration
│   │       ├── repository/      # MongoDB repositories
│   │       ├── security/        # Security configuration
│   │       └── service/         # Business logic
│   └── resources/
│       └── application.yml      # Application configuration
├── test/
│   └── groovy/                  # Unit tests (Spock)
└── integration-test/
    └── groovy/                  # Integration tests (Testcontainers)
```

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Integration Tests
```bash
./gradlew integrationTest
```

### Run All Tests
```bash
./gradlew check
```

### Test Coverage

Test reports are generated in `build/reports/tests/test/index.html` after running tests.

**Test Stack:**
- **Spock Framework**: BDD-style testing with Groovy
- **Testcontainers**: Integration tests with real MongoDB containers
- **Spring Security Test**: Security testing utilities
- **MockMvc**: Web layer testing

## Configuration

### Application Properties

Key configuration options in `application.yml`:

- **MongoDB**: Connection settings, database name
- **Redis**: Host, port, connection pool settings
- **JWT**: Secret key, token expiration time
- **Cache**: Cache names, TTL settings
- **Actuator**: Exposed endpoints, metrics configuration

### Environment Variables

All configuration values can be overridden via environment variables. Create a `.env` file or set them in your environment:

**MongoDB Configuration:**
- `MONGO_HOST`: MongoDB host (default: `localhost`)
- `MONGO_PORT`: MongoDB port (default: `27017`)
- `MONGO_USERNAME`: MongoDB username (default: `username`)
- `MONGO_PASSWORD`: MongoDB password (default: `password`)
- `MONGO_DATABASE`: MongoDB database name (default: `twitter`)
- `MONGO_AUTH_DATABASE`: MongoDB authentication database (default: `admin`)

**Redis Configuration:**
- `REDIS_HOST`: Redis host (default: `localhost`)
- `REDIS_PORT`: Redis port (default: `6379`)
- `REDIS_PASSWORD`: Redis password (default: empty)
- `REDIS_TIMEOUT`: Redis connection timeout (default: `2000ms`)
- `REDIS_POOL_MAX_ACTIVE`: Redis pool max active connections (default: `8`)
- `REDIS_POOL_MAX_IDLE`: Redis pool max idle connections (default: `8`)
- `REDIS_POOL_MIN_IDLE`: Redis pool min idle connections (default: `0`)

**Server Configuration:**
- `SERVER_PORT`: Application server port (default: `8080`)

**JWT Configuration:**
- `JWT_SECRET`: JWT secret key (⚠️ **REQUIRED** for production - must be at least 256 bits)
- `JWT_EXPIRATION`: JWT token expiration in milliseconds (default: `600000` = 10 minutes)

**Cache Configuration:**
- `CACHE_TTL`: Cache time-to-live in milliseconds (default: `3600000` = 1 hour)
- `CACHE_NAMES`: Comma-separated cache names (default: `posts,users,comments,likes,userPosts,followingPosts`)
- `CACHE_DEFAULT_TTL`: Default cache TTL in minutes (default: `600`)
- `CACHE_POSTS_TTL`: Posts cache TTL in minutes (default: `10`)
- `CACHE_USERS_TTL`: Users cache TTL in minutes (default: `15`)
- `CACHE_COMMENTS_TTL`: Comments cache TTL in minutes (default: `5`)
- `CACHE_LIKES_TTL`: Likes cache TTL in minutes (default: `5`)
- `CACHE_USER_POSTS_TTL`: User posts cache TTL in minutes (default: `10`)
- `CACHE_FOLLOWING_POSTS_TTL`: Following posts cache TTL in minutes (default: `5`)

**Circuit Breaker Configuration:**
- `CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE`: Sliding window size (default: `10`)
- `CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD`: Failure rate threshold percentage (default: `50.0`)
- `CIRCUIT_BREAKER_WAIT_DURATION`: Wait duration in open state in seconds (default: `30`)
- `CIRCUIT_BREAKER_PERMITTED_CALLS_HALF_OPEN`: Permitted calls in half-open state (default: `3`)
- `CIRCUIT_BREAKER_SLOW_CALL_RATE_THRESHOLD`: Slow call rate threshold percentage (default: `50.0`)
- `CIRCUIT_BREAKER_SLOW_CALL_DURATION`: Slow call duration threshold in seconds (default: `2`)

**Security Configuration:**
- `BCRYPT_STRENGTH`: BCrypt password encoder strength (default: `12`)
- `CORS_ALLOWED_ORIGINS`: Comma-separated allowed CORS origins (default: `http://localhost:3000,http://localhost:8080,http://127.0.0.1:3000,http://127.0.0.1:8080`)
- `CORS_ALLOWED_METHODS`: Comma-separated allowed HTTP methods (default: `GET,POST,PUT,PATCH,DELETE,OPTIONS`)
- `CORS_ALLOWED_HEADERS`: Comma-separated allowed headers (default: `*`)
- `CORS_EXPOSED_HEADERS`: Comma-separated exposed headers (default: `X-Request-ID,X-Response-Time,X-Memory-Used,X-RateLimit-Limit-Minute,X-RateLimit-Remaining-Minute,X-RateLimit-Limit-Hour,X-RateLimit-Remaining-Hour`)
- `CORS_ALLOW_CREDENTIALS`: Allow credentials in CORS (default: `true`)
- `CORS_MAX_AGE`: CORS max age in seconds (default: `3600`)
- `SECURITY_PUBLIC_ENDPOINTS`: Comma-separated public endpoints that don't require authentication

**Rate Limiting Configuration:**
- `RATE_LIMIT_MAX_REQUESTS_PER_MINUTE`: Maximum requests per minute per client (default: `100`)
- `RATE_LIMIT_MAX_REQUESTS_PER_HOUR`: Maximum requests per hour per client (default: `1000`)
- `RATE_LIMIT_EXCLUDED_PATHS`: Comma-separated paths excluded from rate limiting (default: `/actuator/health,/actuator/info`)

**Performance Monitoring Configuration:**
- `PERFORMANCE_SLOW_REQUEST_THRESHOLD`: Slow request threshold in milliseconds (default: `1000`)
- `PERFORMANCE_VERY_SLOW_REQUEST_THRESHOLD`: Very slow request threshold in milliseconds (default: `5000`)

**OpenAPI Configuration:**
- `OPENAPI_TITLE`: API title (default: `Twitter API`)
- `OPENAPI_VERSION`: API version (default: `1.0.0`)
- `OPENAPI_DESCRIPTION`: API description
- `OPENAPI_CONTACT_NAME`: Contact name (default: `API Support`)
- `OPENAPI_CONTACT_EMAIL`: Contact email (default: `support@twitter.com`)
- `OPENAPI_LICENSE_NAME`: License name (default: `Apache 2.0`)
- `OPENAPI_LICENSE_URL`: License URL

**Spring Profile:**
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: `default`, use `docker` for Docker deployment)

> **Note**: For production, always set `JWT_SECRET` to a secure, randomly generated value. Never commit secrets to version control.

## Monitoring and Observability

### Health Checks

- **Liveness**: `GET /actuator/health/liveness`
- **Readiness**: `GET /actuator/health/readiness`
- **Full Health**: `GET /actuator/health`

### Metrics

- **Prometheus**: `GET /actuator/prometheus`
- **All Metrics**: `GET /actuator/metrics`
- **Specific Metric**: `GET /actuator/metrics/{metricName}`

### Distributed Tracing

The application is configured for distributed tracing with Zipkin. Ensure Zipkin is running to collect trace data.

### Circuit Breaker

Resilience4j circuit breaker is integrated for fault tolerance. Configuration can be found in the monitoring package.

## Architecture Decisions

1. **Groovy for Business Logic**: Leverages Groovy's dynamic features while maintaining type safety where needed
2. **MongoDB**: Chosen for flexible schema and horizontal scalability
3. **Redis Caching**: Improves performance for frequently accessed data
4. **JWT Authentication**: Stateless authentication suitable for microservices
5. **MapStruct**: Compile-time mapping for better performance
6. **Testcontainers**: Integration tests with real database instances
8. **Spring Boot Actuator**: Production-ready monitoring and management
9. **Circuit Breaker**: Resilience patterns for external dependencies
10. **API-First Design**: OpenAPI/Swagger documentation for API contracts

## License

This project is a test assignment.

## Author

Developed as a test assignment demonstrating:
- RESTful API design
- Spring Boot best practices
- Security implementation
- Testing strategies
- Docker containerization
- Monitoring and observability

