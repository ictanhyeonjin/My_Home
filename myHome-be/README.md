# myHome

청약 공공 API 기반 정보 조회 서비스를 위한 Spring Boot 백엔드 프로젝트입니다.

## Stack

- Open JDK 17
- Spring Boot 3.3.2
- Maven
- MariaDB / MyBatis
- Log4j2

## Run

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\run-be.cmd
```

또는 직접 실행:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd spring-boot:run
```

기본 프로필은 `local`이며 서버 포트는 `8080`입니다.
프론트엔드는 sibling 폴더인 `../myHome-fe`에서 Vue로 실행합니다.

## Database

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/myhome
    username: root
    password: root1234!
    driver-class-name: org.mariadb.jdbc.Driver
```

## Paths

- Health API: `http://localhost:8080/api/health`
- Hello API: `http://localhost:8080/api/hello`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
