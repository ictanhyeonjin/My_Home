# My Home

FE + BE project for a housing subscription information service.

## Structure

```text
myHome/
  myHome-be/  # Spring Boot backend
  myHome-fe/  # Vue 3 + Vite frontend
```

## Backend

```powershell
cd .\myHome-be
.\run-be.cmd
```

- API: `http://localhost:8080`
- Hello API: `http://localhost:8080/api/hello`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Frontend

```powershell
cd .\myHome-fe
.\run-fe.cmd
```

- FE: `http://localhost:5173`
- `/api` requests are proxied to `http://localhost:8080`.

## Database

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/myhome
    username: root
    password: root1234!
    driver-class-name: org.mariadb.jdbc.Driver
```
