# Day-On-Server Architecture

## 모듈 구조

```
day-on-server/
├── day-on-api/                    # API 계층
├── day-on-core/                   # 도메인 계층
├── day-on-application/            # 애플리케이션 서비스 계층
├── day-on-provider/               # 외부 서비스 연동 계층
│   ├── day-on-provider-google/    # Google 서비스 연동
│   └── day-on-provider-claude/    # Claude AI 서비스 연동
├── day-on-persistence/            # 데이터 저장 계층
└── day-on-infrastructure/         # 인프라스트럭처 계층
```

## 각 계층별 상세 설명

### 1. day-on-api

**역할**: 외부 클라이언트와의 인터페이스를 담당하는 Primary Adapter

**포함되는 요소**:
- REST Controller
- Request/Response DTO
- Exception Handler
- API 문서화 (Swagger/OpenAPI)
- 입력 검증 (Validation)
- HTTP 관련 설정

**정의해야 할 항목들**:
```
api/
├── controller/        # REST 컨트롤러
├── dto/               # 요청/응답 DTO
├── exception/         # 전역 예외 처리
├── config/             # Web 설정
└── documentation/     # API 문서화
```

### 2. day-on-core

**역할**: 비즈니스 로직과 도메인 규칙을 담당하는 핵심 계층

**포함되는 요소**:
- Domain Model
- Domain UseCase
- Repository Interface (Port)
- External Service Interface (Port)
- Domain Event

**정의해야 할 항목들**:
```
core/
├── domain/
│   ├── model/        # 도메인 모델
│   ├── usecase/      # 도메인 서비스
│   └── event/        # 도메인 이벤트
├── port/
│   ├── in/           # 인바운드 포트
│   └── out/          # 아웃바운드 포트
└── exception/        # 도메인 예외
```

**의존성**: 외부 계층에 의존하지 않음 (Pure Domain)

### 3. day-on-application

**역할**: 애플리케이션 서비스와 유스케이스를 구현하는 계층

**포함되는 요소**:
- Use Case 구현
- Transaction 관리
- Port Interface 구현 (Inbound Port)
- DTO 변환
- 애플리케이션 수준의 예외 처리

**정의해야 할 항목들**:
```
application/
├── service/          # 유스케이스 구현
├── dto/              # 애플리케이션 DTO
├── mapper/           # DTO 변환기
└── config/            # 애플리케이션 설정
```

### 4. day-on-provider

**역할**: 외부 API 및 서비스와의 연동을 담당하는 Secondary Adapter

**포함되는 요소**:
- 외부 API 클라이언트 (Feign)
- 외부 서비스 Adapter
- 서킷 브레이커 (Circuit Breaker)
- 재시도 로직 (Retry)
- 외부 API 응답 DTO
- API 호출 관련 설정

**정의해야 할 항목들**:
```
provider/
├── google/
│   ├── client/       # Google API 클라이언트
│   ├── adapter/      # Google 서비스 어댑터
│   ├── dto/         # Google API DTO
│   └── config/      # Google API 설정
├── claude/
│   ├── client/       # Claude API 클라이언트
│   ├── adapter/      # Claude 서비스 어댑터
│   ├── dto/         # Claude API DTO
│   └── config/      # Claude API 설정
└── common/
    ├── exception/    # 외부 서비스 예외
    └── config/      # 공통 설정 (서킷브레이커 등)
```

### 5. day-on-persistence

**역할**: 데이터 저장소와의 연동을 담당하는 Secondary Adapter

**포함되는 요소**:
- JPA Entity
- Repository 구현체
- 데이터베이스 설정
- 쿼리 최적화
- 데이터베이스 관련 설정

**정의해야 할 항목들**:
```
persistence/
├── entity/           # JPA 엔티티
├── repository/       # Repository 구현체
├── config/            # 데이터베이스 설정
└── mapper/           # Entity-Domain 변환기
```

### 6. day-on-infrastructure

**역할**: 인프라스트럭처 기술과의 연동을 담당하는 계층

**포함되는 요소**:
- 캐시
- 메시징
- 모니터링 및 로깅
- 보안 관련 인프라

**정의해야 할 항목들**:
```
infrastructure/
├── cache/           # 캐시 관련 (Redis 등)
├── messaging/       # 메시징 관련 (RabbitMQ, Kafka 등)
├── monitoring/      # 모니터링 및 로깅
├── security/        # 보안 인프라 (JWT, OAuth 등)
├── config/           # 외부 설정 관리
└── scheduler/       # 스케줄링 관련
```

## 의존성 규칙

### Port와 Adapter 패턴

1. **Port (Interface)**: Core 계층에서 정의
2. **Adapter (구현체)**: 각 Secondary Adapter 계층에서 구현


### 트랜잭션 관리

- **위치**: Application, Persistence 계층
- **이유**: 유스케이스 단위의 트랜잭션 경계 설정

## 테스트 전략

### 단위 테스트
- **Core**: 도메인 로직 중심의 순수 단위 테스트
- **Application**: Port의 Mock을 활용한 테스트

### 통합 테스트
- **API**: MockMvc를 활용한 API 통합 테스트
- **Provider**: WireMock을 활용한 외부 API 테스트