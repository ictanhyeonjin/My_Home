# My Home

FE + BE project for a housing subscription information service.

## Structure

```text
myHome/
  myHome-be/  # Spring Boot backend
  myHome-fe/  # Vue 3 + Vite frontend
```

## Backend

프로젝트 루트의 `.env`에 공공데이터포털 서비스 키를 설정합니다.

```dotenv
SERVICE_KEY=발급받은_일반_인증키_Encoding
```

`.env`는 Git에서 제외됩니다. 기존 `LH_SERVICE_KEY` 이름도 계속 지원합니다.

```powershell
cd .\myHome-be
.\run-be.cmd
```

- API: `http://localhost:8080`
- Hello API: `http://localhost:8080/api/hello`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- LH 공급 상세정보: `GET /api/lh/notices/{PAN_ID}/supply-info`

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
