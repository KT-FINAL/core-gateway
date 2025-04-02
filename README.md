# Gateway Server

## Overview
Gateway Server는 마이크로서비스 아키텍처의 진입점으로, Spring Cloud Gateway를 기반으로 구축된 API Gateway 서비스입니다. 
이 서비스는 클라이언트의 요청을 적절한 마이크로서비스로 라우팅하고, 인증/인가, 로깅, 모니터링 등의 공통 기능을 제공합니다.

## Features
- Spring Cloud Gateway 기반 라우팅
- JWT 기반 인증/인가
- Kubernetes 통합
- WebFlux 기반 비동기 처리
- OpenFeign을 통한 서비스 간 통신
- Docker 지원

## Tech Stack
- Java 17
- Spring Boot 3.4.4
- Spring Cloud 2024.0.1
- Spring Cloud Gateway
- Spring Security
- JWT (JSON Web Token)
- Kubernetes Client
- WebFlux
- OpenFeign
- Lombok

## Prerequisites
- JDK 17 이상
- Gradle 8.x
- Docker (선택사항)
- Kubernetes (선택사항)

## Getting Started

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew bootRun
```

### Docker Build & Run
```bash
docker build -t gateway-server .
docker run -p 8080:8080 gateway-server
```

## Configuration
프로젝트의 주요 설정은 `application.yml` 또는 `application.properties`에서 관리됩니다.
환경 변수는 `.env` 파일을 통해 관리됩니다.

## Project Structure
```
gateway-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── build.gradle
├── Dockerfile
└── README.md
```

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details. 
