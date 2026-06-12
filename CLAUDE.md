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
- **이미지 처리**: Thumbnailator 0.4.20 (리사이즈)
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
│   ├── user/
│   │   ├── controller/UserController.java          # GET /api/users/me (JWT 토큰으로 조회)
│   │   ├── dto/UserResponse.java
│   │   └── service/UserService.java                # jwt_token으로 유저 조회
│   └── userrace/
│       ├── controller/UserRaceController.java      # POST/DELETE/GET/PATCH /api/user-races
│       ├── dto/UserRaceRegisterRequest.java
│       ├── dto/UserRaceRecordUpdateRequest.java
│       ├── dto/UserRaceResponse.java
│       ├── entity/UserRace.java
│       ├── repository/UserRaceRepository.java
│       └── service/UserRaceService.java
├── global/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── SchedulerConfig.java                  # @EnableScheduling
│   │   ├── WebMvcConfig.java                     # /upload/** 정적 파일 서빙
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAccessDeniedHandler.java
│   ├── exception/GlobalExceptionHandler.java
│   ├── jwt/JwtProvider.java, JwtAuthenticationFilter.java
│   ├── response/ApiResponse.java
│   └── upload/FileUploadService.java             # 이미지 업로드 + 리사이즈
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
| jwt_token | jwtToken | TEXT, 로그인 시 발급된 JWT 저장 (재로그인 시 갱신) |
| fcm_token | fcmToken | TEXT, 푸시 알림용 |
| role | role | VARCHAR(10), 기본값 USER |
| create_dt | createDt | TIMESTAMP NOT NULL DEFAULT NOW() |
| update_dt | updateDt | TIMESTAMP NOT NULL DEFAULT NOW() |

## user_race 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| user_race_idx | userRaceIdx | BIGSERIAL PK (`@Column(name="user_race_idx")`) |
| user_idx | user | BIGINT FK → users(user_idx), `@ManyToOne` |
| race_idx | race | BIGINT FK → race(race_idx), `@ManyToOne` |
| course | course | VARCHAR(20), 참가 코스 (풀/하프/10km 등) |
| bib_number | bibNumber | VARCHAR(20), 배번호 (`@Column(name="bib_number")`) |
| record_time | recordTime | VARCHAR(10), 완주 기록 HH:MM:SS (`@Column(name="record_time")`) |
| pace | pace | VARCHAR(10), 평균 페이스 MM:SS/km |
| ranking | ranking | INT, 순위 |
| finish_yn | finishYn | VARCHAR(1), 완주 여부 Y/N (`@Column(name="finish_yn")`) |
| memo | memo | TEXT, 한줄 소감 |
| record_image_path | recordImagePath | TEXT, 기록증 이미지 경로 (`@Column(name="record_image_path")`) |
| del_at | delAt | VARCHAR(1) NOT NULL DEFAULT 'N', 취소 시 Y (`@Column(name="del_at")`) |
| create_dt | createDt | TIMESTAMP NOT NULL DEFAULT NOW() |
| update_dt | updateDt | TIMESTAMP NOT NULL DEFAULT NOW() |

### user_race API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| POST | `/api/user-races` | 필요 | 대회 등록 |
| DELETE | `/api/user-races/{userRaceIdx}` | 필요 | 대회 취소 (del_at='Y') |
| GET | `/api/user-races/me` | 필요 | 내 등록 대회 목록 (del_at='N') |
| PATCH | `/api/user-races/{userRaceIdx}/record` | 필요 | 기록 업데이트 |
| POST | `/api/user-races/{userRaceIdx}/record-image` | 필요 | 기록증 이미지 업로드 |
| GET | `/upload/recode/{userIdx}/{filename}` | 불필요 | 이미지 파일 서빙 |

- 동일 대회 중복 등록 방지: `del_at='N'`인 레코드 존재 시 400
- 취소는 soft delete — 히스토리 유지, 재등록 시 새 row 삽입
- `Authentication.getName()`으로 JWT subject(`userIdx`) 추출

## 파일 업로드

- **업로드 API**: `POST /api/user-races/{userRaceIdx}/record-image` (multipart/form-data, 파라미터명 `file`)
- **허용 형식**: jpg, png, webp / 최대 20MB
- **리사이즈**: 최대 1080×1920 (모바일 FHD 기준), 비율 유지, 품질 0.8
- **저장 경로**: `{upload.base-path}/recode/{userIdx}/{uuid}.확장자`
- **서빙 URL**: `/upload/recode/{userIdx}/{filename}` (인증 불필요)
- **설정**: `application.yaml`의 `upload.base-path` (기본 `./upload`, 운영 시 절대경로 권장)
- `FileUploadService` — 업로드·리사이즈 담당, `WebMvcConfig` — `/upload/**` 정적 리소스 핸들러 등록

## 설정 (`src/main/resources/application.yaml`)

- **서버 포트**: `18300`
- **DB**: Oracle Cloud VM PostgreSQL `168.107.51.69:5432/raceon`
- **JWT**: `jwt.secret` (Base64), `jwt.expiration` (ms, 기본 86400000 = 24h)
- **멀티파트**: `max-file-size: 20MB`, `max-request-size: 20MB`
- **업로드 경로**: `upload.base-path: ./upload`
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
