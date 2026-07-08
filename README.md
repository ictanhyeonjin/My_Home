# My Home

청약 공공 API 기반 청약 정보 조회 서비스를 위한 FE + BE 프로젝트입니다.

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
