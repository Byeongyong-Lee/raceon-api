# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 목적

React Native 앱의 백엔드 API 서버. 다음 역할을 담당:

- **DB 조회/수정**: PostgreSQL 데이터를 REST API로 제공
- **로그인 관리**: 카카오/네이버/구글 소셜 로그인 + JWT 인증
- **마라톤 대회 크롤링**: roadrun.co.kr에서 매일 자동 수집

클라이언트는 React Native 앱 단독. API 응답은 JSON만 사용.

## Build & Run

```bash
./gradlew build
./gradlew bootRun
./gradlew test
./gradlew test --tests "com.raceon.api.ClassName.methodName"
./gradlew clean
```

## 기술 스택

- **Java 17**, **Spring Boot 4.0.6**, **Gradle**
- **패키징**: WAR (`ServletInitializer`로 외부 Tomcat 배포 지원)
- **DB**: PostgreSQL + Spring Data JPA (HikariCP)
- **보안**: Spring Security 7 (Stateless) + JWT (`jjwt` 0.12.6)
- **소셜 로그인**: 카카오 / 네이버 / 구글 (RestClient로 각 API 호출)
- **크롤링**: Jsoup 1.18.3 (EUC-KR 디코딩), `@Scheduled` 매일 오전 3시
- **Lombok**, **DevTools**

## 패키지 구조

```
com.raceon.api
├── domain/
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── dto/SocialLoginRequest, LoginResponse
│   │   ├── entity/User.java
│   │   ├── repository/UserRepository.java
│   │   └── service/AuthService.java              # UserDetailsService 구현
│   ├── race/
│   │   ├── controller/RaceController.java          # GET /api/races
│   │   ├── controller/RaceAdminController.java     # POST /api/admin/crawl
│   │   ├── dto/RaceResponse.java
│   │   ├── entity/Race.java
│   │   ├── repository/RaceRepository.java
│   │   ├── service/RaceService.java                # 조회 로직
│   │   └── service/RaceCrawlerService.java         # 크롤링 + @Scheduled
│   └── user/
│       ├── controller/UserController.java          # GET /api/users/me
│       ├── dto/UserResponse.java
│       └── service/UserService.java                # 유저 조회 로직
├── global/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── SchedulerConfig.java                  # @EnableScheduling
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAccessDeniedHandler.java
│   ├── exception/GlobalExceptionHandler.java
│   ├── jwt/JwtProvider.java, JwtAuthenticationFilter.java
│   └── response/ApiResponse.java
└── ApiApplication.java
```

새 도메인은 `domain/<도메인명>/` 하위에 같은 구조로 추가.

## 공통 응답 포맷

```json
{ "success": true,  "data": { ... }, "message": null }
{ "success": false, "data": null,    "message": "에러 메시지" }
```

컨트롤러 반환 타입은 항상 `ApiResponse<T>`. 예외는 `GlobalExceptionHandler`가 자동 변환.

| 예외 | HTTP |
|------|------|
| `BadCredentialsException` | 401 |
| `IllegalArgumentException` | 400 |
| `IllegalStateException` | 502 |
| 미인증 요청 | 401 |
| 권한 없음 | 403 |
| 그 외 | 500 |

## 인증 흐름

1. React Native → 소셜 SDK로 사용자 정보 직접 획득
2. `POST /api/auth/{kakao|naver|google}` + `{ "socialId": "...", "nickname": "...", "profileImage": "...", "gender": "...", "age": "...", "birthday": "...", "phone": "..." }`
3. 서버 → socialId로 DB 조회 → 없으면 신규 저장, 있으면 조회 → JWT 발급
4. 이후 요청: `Authorization: Bearer <token>` 헤더 필수

서버에서 소셜 API를 직접 호출하지 않음. 클라이언트가 소셜 SDK로 사용자 정보를 수집해 전달.

`/api/auth/**`, `/api/races` 인증 불필요. 나머지 전체 인증 필요. JWT subject = `userIdx`.

## 마라톤 대회 크롤링

- **소스**: `http://www.roadrun.co.kr/schedule/list.php?syear_key={year}`
- **인코딩**: EUC-KR → Jsoup으로 파싱
- **스케줄**: 매일 오전 3시 자동 실행 (`@Scheduled(cron = "0 0 3 * * *")`)
- **수동 실행**: `POST /api/admin/crawl?year=2026`
- **범위 크롤링**: `POST /api/admin/crawl/range?startYear=2000&endYear=2025`
- **조회**: `GET /api/races` (인증 불필요)
- **upsert 기준**: `sourceId` (roadrun.co.kr의 대회 고유번호)

## Race 테이블

실제 DB 컬럼명 주의 (camelCase → Hibernate가 snake_case로 변환하지 않음):

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| race_idx | raceIdx | BIGSERIAL PK (`@Column(name="race_idx")`) |
| source_id | sourceId | roadrun.co.kr 대회 고유번호 (VARCHAR, UNIQUE NOT NULL) |
| name | name | 대회명 |
| racedate | raceDate | 대회일 (`@Column(name="racedate")`) |
| location | location | 장소 |
| course | course | 코스 (예: 풀,하프,10km) |
| organizer | organizer | 주최기관 |
| phone | phone | 연락처 |
| homepage | homepage | 공식 홈페이지 |
| detailurl | detailUrl | roadrun.co.kr 상세 URL (`@Column(name="detailurl")`) |
| create_dt | createDt | 생성일시 |

## users 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| user_idx | userIdx | BIGSERIAL PK (`@Column(name="user_idx")`) |
| nickname | nickname | VARCHAR(50) |
| profile_image | profileImage | TEXT |
| kakao_id | kakaoId | VARCHAR(200) UNIQUE |
| naver_id | naverId | VARCHAR(200) UNIQUE |
| google_id | googleId | VARCHAR(200) UNIQUE |
| gender | gender | VARCHAR(1), M / F |
| age | age | VARCHAR(10) |
| birthday | birthday | VARCHAR(4), MMDD |
| phone | phone | VARCHAR(20) |
| fcm_token | fcmToken | TEXT, 푸시 알림용 |
| role | role | VARCHAR(10), 기본값 USER |
| create_dt | createDt | TIMESTAMP NOT NULL DEFAULT NOW() |
| update_dt | updateDt | TIMESTAMP NOT NULL DEFAULT NOW() |

## 설정 (`src/main/resources/application.yaml`)

- **서버 포트**: `18300`
- **DB**: Oracle Cloud VM PostgreSQL `168.107.51.69:5432/raceon`
- **JWT**: `jwt.secret` (Base64), `jwt.expiration` (ms, 기본 86400000 = 24h)
- `jpa.hibernate.ddl-auto: update` — 개발용, 운영 시 `validate` 또는 `none` 권장
- **주의**: 운영 배포 시 DB 자격증명·JWT secret 환경변수로 분리 필요

## 주의사항

- `users` 테이블의 PK는 `user_idx` — `@Id` 필드명은 `userIdx`, `@Column(name="user_idx")` 필수
- `race` 테이블의 PK는 `race_idx` — `@Id` 필드명은 `raceIdx`, `@Column(name="race_idx")` 필수
- `race` 테이블 컬럼 `racedate`, `detailurl`은 소문자 — 각각 `@Column(name="racedate")`, `@Column(name="detailurl")` 필수
- `AuthService`가 `UserDetailsService` 구현 — Spring Security가 자동으로 사용. JWT subject = `userIdx`
- JWT는 Stateless — 서버에서 토큰 강제 만료 불가
- 크롤링 대상 사이트가 EUC-KR 인코딩 — Jsoup `bodyAsBytes()` + 명시적 charset 파싱 필수
- `RaceCrawlerService.crawl()`은 `@Transactional` 필수 — 없으면 Hibernate 세션 문제로 INSERT 실패
