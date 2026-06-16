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
- **동적 쿼리**: QueryDSL 5.1.0 (jakarta)
- **실시간 통신**: Spring WebSocket + STOMP (채팅, 위치공유)
- **Lombok**, **DevTools**

## 패키지 구조

```
com.raceon.api
├── domain/
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── dto/SocialLoginRequest.java
│   │   ├── dto/LoginResponse.java                  # accessToken + refreshToken 반환
│   │   ├── dto/TokenRefreshRequest.java
│   │   ├── dto/TokenRefreshResponse.java
│   │   ├── entity/User.java
│   │   ├── repository/UserRepository.java
│   │   ├── repository/UserRepositoryCustom.java    # QueryDSL 커스텀 인터페이스
│   │   ├── repository/UserRepositoryImpl.java      # QueryDSL 구현체
│   │   ├── repository/UserSearchCondition.java     # 검색 조건 DTO
│   │   └── service/AuthService.java              # UserDetailsService 구현
│   ├── race/
│   │   ├── controller/RaceController.java          # GET /api/races
│   │   ├── controller/RaceAdminController.java     # POST /api/admin/crawl
│   │   ├── dto/RaceResponse.java
│   │   ├── entity/Race.java
│   │   ├── repository/RaceRepository.java
│   │   ├── repository/RaceRepositoryCustom.java    # QueryDSL 커스텀 인터페이스
│   │   ├── repository/RaceRepositoryImpl.java      # QueryDSL 구현체
│   │   ├── repository/RaceSearchCondition.java     # 검색 조건 DTO
│   │   ├── service/RaceService.java                # 조회 로직
│   │   └── service/RaceCrawlerService.java         # 크롤링 + @Scheduled
│   ├── area/
│   │   ├── controller/AreaController.java          # GET /api/areas
│   │   ├── dto/AreaResponse.java
│   │   ├── entity/Area.java
│   │   ├── repository/AreaRepository.java
│   │   └── service/AreaService.java
│   ├── user/
│   │   ├── controller/UserController.java          # GET /api/users/me
│   │   ├── dto/UserResponse.java
│   │   └── service/UserService.java                # userIdx로 유저 조회
│   ├── userrace/
│   │   ├── controller/UserRaceController.java      # POST/DELETE/GET/PATCH /api/user-races
│   │   ├── dto/UserRaceRegisterRequest.java
│   │   ├── dto/UserRaceRecordUpdateRequest.java
│   │   ├── dto/UserRaceResponse.java
│   │   ├── entity/UserRace.java
│   │   ├── repository/UserRaceRepository.java
│   │   ├── repository/UserRaceRepositoryCustom.java
│   │   ├── repository/UserRaceRepositoryImpl.java
│   │   ├── repository/UserRaceSearchCondition.java
│   │   └── service/UserRaceService.java
│   └── group/
│       ├── controller/GroupController.java              # 모임 CRUD
│       ├── controller/GroupMemberController.java        # 멤버 관리, 권한
│       ├── controller/GroupJoinApplicationController.java # 가입 신청/승인
│       ├── controller/GroupBoardController.java         # 게시판
│       ├── controller/GroupChatController.java          # 채팅 이력 조회
│       ├── controller/GroupRaceController.java          # 대회 연동
│       ├── controller/GroupMeetupController.java        # 약속
│       ├── dto/                                         # 요청/응답 DTO
│       ├── entity/Group.java
│       ├── entity/GroupMember.java
│       ├── entity/GroupManagerPermission.java
│       ├── entity/GroupJoinApplication.java
│       ├── entity/GroupBoard.java
│       ├── entity/GroupBoardComment.java
│       ├── entity/GroupChat.java
│       ├── entity/GroupRace.java
│       ├── entity/GroupMeetup.java
│       ├── entity/GroupMeetupParticipant.java
│       ├── enums/GroupRole.java                         # OWNER, MANAGER, MEMBER
│       ├── enums/ApplicationStatus.java                 # PENDING, APPROVED, REJECTED
│       ├── enums/ChatMessageType.java                   # TEXT, IMAGE, SYSTEM
│       ├── enums/MeetupStatus.java                      # ATTEND, ABSENT, PENDING
│       ├── repository/                                  # JPA 리포지토리
│       └── service/                                     # 비즈니스 로직
├── global/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── SchedulerConfig.java                  # @EnableScheduling
│   │   ├── WebMvcConfig.java                     # /upload/** 정적 파일 서빙
│   │   ├── QuerydslConfig.java                   # JPAQueryFactory 빈 등록
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAccessDeniedHandler.java
│   ├── exception/GlobalExceptionHandler.java
│   ├── jwt/JwtProvider.java, JwtAuthenticationFilter.java
│   ├── response/ApiResponse.java
│   ├── upload/FileUploadService.java             # 이미지 업로드 + 리사이즈
│   └── websocket/
│       ├── WebSocketConfig.java                  # STOMP 브로커 설정
│       ├── WebSocketAuthInterceptor.java          # CONNECT 시 JWT 인증
│       ├── GroupWebSocketController.java          # 채팅·위치 메시지 핸들러
│       ├── ChatMessage.java / ChatResponse.java
│       └── LocationMessage.java / LocationBroadcast.java
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
3. 서버 → socialId로 DB 조회 → 없으면 신규 저장, 있으면 조회 → Access Token + Refresh Token 발급
4. 이후 요청: `Authorization: Bearer <accessToken>` 헤더 필수
5. Access Token 만료 시: `POST /api/auth/refresh` + `{ "refreshToken": "..." }` → 새 Access Token 발급

서버에서 소셜 API를 직접 호출하지 않음. 클라이언트가 소셜 SDK로 사용자 정보를 수집해 전달.

`/api/auth/**`, `/api/races`, `/api/areas` 인증 불필요. 나머지 전체 인증 필요. JWT subject = `userIdx`.

### 인증 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| POST | `/api/auth/kakao` | 불필요 | 카카오 로그인 |
| POST | `/api/auth/naver` | 불필요 | 네이버 로그인 |
| POST | `/api/auth/google` | 불필요 | 구글 로그인 |
| POST | `/api/auth/refresh` | 불필요 | Access Token 재발급 |

로그인 응답 (`LoginResponse`):
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "userId": 1,
  "nickname": "홍길동",
  "profileImage": "https://...",
  "role": "USER"
}
```

토큰 갱신 요청/응답:
```json
// POST /api/auth/refresh 요청
{ "refreshToken": "eyJ..." }

// 응답
{ "accessToken": "eyJ..." }
```

### 클라이언트 토큰 처리 흐름

1. 로그인 시 `accessToken`, `refreshToken` 모두 저장
2. API 호출 시 `Authorization: Bearer <accessToken>` 헤더 사용
3. 401 응답 수신 → `POST /api/auth/refresh`로 `accessToken` 재발급
4. 재발급도 실패(401) → 로그인 화면으로 이동

### 토큰 구조

| 토큰 | 만료 | 저장 위치 | 용도 |
|------|------|-----------|------|
| Access Token | 1시간 | 클라이언트 메모리 | API 인증 (`Authorization` 헤더) |
| Refresh Token | 7일 | 클라이언트 + DB (`users.refresh_token`) | Access Token 재발급 |

- 토큰에 `type` 클레임 포함 — Refresh Token을 Access Token으로 사용 불가
- Refresh Token은 DB 저장값과 일치해야 재발급 허용 (탈취 방지)
- 재로그인 시 Refresh Token 갱신 → 기존 Refresh Token 무효화

## area 테이블

행정안전부 법정동코드 기준 행정구역 데이터. 초기 데이터(시도 17개 + 시군구 전체)는 DB에 직접 삽입.

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| area_idx | areaIdx | BIGSERIAL PK |
| area_code | areaCode | 행정구역코드 (시도 2자리 / 시군구 5자리 / 읍면동 8자리), UNIQUE |
| area_name | areaName | 행정구역명 (예: 종로구, 수원시 장안구) |
| area_level | areaLevel | 1=시도, 2=시군구, 3=읍면동 |
| parent_code | parentCode | 상위 행정구역 코드 (시도는 NULL) |
| full_name | fullName | 전체 경로명 (예: 경기도 수원시 장안구) |
| create_dt | createDt | 생성일시 |

### area API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api/areas` | 불필요 | 행정구역 목록 조회 |

**쿼리 파라미터**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `level` | Integer (선택) | 1=시도, 2=시군구, 3=읍면동 |
| `parentCode` | String (선택) | 상위 행정구역 코드 |

```
GET /api/areas?level=1               → 시도 17개
GET /api/areas?level=2&parentCode=11 → 서울 시군구 25개
GET /api/areas?level=3&parentCode=11010 → 종로구 읍면동 전체
GET /api/areas                       → 전체 조회
```

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
| refresh_token | refreshToken | TEXT, Refresh Token 저장 (`@Column(name="refresh_token")`) |
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

## QueryDSL

복잡한 동적 쿼리는 QueryDSL을 사용. 조건 DTO를 넘기면 `null` 필드는 WHERE절에서 자동 제외.

### 구조 패턴

- `XxxRepositoryCustom` — 커스텀 메서드 인터페이스
- `XxxRepositoryImpl` — QueryDSL 구현체 (`JPAQueryFactory` 주입)
- `XxxSearchCondition` — 검색 조건 DTO (`@Builder`)
- `XxxRepository` — `JpaRepository<X, ID>` + `XxxRepositoryCustom` 동시 상속 (메서드 선언 없는 빈 인터페이스지만 Spring Data JPA 주입 진입점으로 필수)

### 사용 예시

```java
// 서비스에서 조건 DTO 빌드해서 넘김
repository.search(XxxSearchCondition.builder()
        .userIdx(userIdx)
        .delAt("N")
        .build());

// Impl 내부 — null이면 해당 조건 무시
private BooleanExpression delAtEq(String delAt) {
    return delAt != null ? entity.delAt.eq(delAt) : null;
}
```

### Q클래스 생성

`./gradlew compileJava` 실행 시 APT가 자동 생성 → `build/generated/sources/annotationProcessor/java/main/`

새 엔티티 추가 시 `compileJava` 재실행 필요.

## 파일 업로드

- **업로드 API**: `POST /api/user-races/{userRaceIdx}/record-image` (multipart/form-data, 파라미터명 `file`)
- **허용 형식**: jpg, jpeg, png, webp / 최대 10MB
- **리사이즈**: 최대 1080×1920 (모바일 FHD 기준), 비율 유지, **100KB 미만 보장**
  - 1단계: 품질 0.8→0.1 순으로 감소 시도
  - 2단계: 품질 0.1 고정 후 해상도를 0.9배씩 축소
  - webp는 Thumbnailator 출력 미지원 → jpeg로 변환 저장
- **저장 경로**: `{upload.base-path}/recode/{userIdx}/{uuid}.확장자`
- **서빙 URL**: `/upload/recode/{userIdx}/{filename}` (인증 불필요)
- **설정**: `application.yaml`의 `upload.base-path` (기본 `./upload`, 운영 시 절대경로 권장)
- `FileUploadService` — 업로드·리사이즈 담당, `WebMvcConfig` — `/upload/**` 정적 리소스 핸들러 등록

## 설정 (`src/main/resources/application.yaml`)

- **서버 포트**: `18300`
- **DB**: Oracle Cloud VM PostgreSQL `168.107.51.69:5432/raceon`
- **JWT**: `jwt.secret` (Base64), `jwt.access-expiration: 3600000` (1시간), `jwt.refresh-expiration: 604800000` (7일)
- **멀티파트**: `max-file-size: 10MB`, `max-request-size: 10MB`
- **업로드 경로**: `upload.base-path: ./upload`
- `jpa.hibernate.ddl-auto: update` — 개발용, 운영 시 `validate` 또는 `none` 권장
- **SQL 로그**: `show-sql` 비활성화 — `log4j2-spring.xml`의 `org.hibernate.SQL` (DEBUG) 단일 출력, `format_sql: true`로 포맷팅
- **주의**: 운영 배포 시 DB 자격증명·JWT secret 환경변수로 분리 필요

## 모임(Group) 기능

### 권한 체계

```
OWNER (모임장)
  └─ MANAGER (운영진) — 모임장이 세부 권한 선택 부여
       ├─ can_manage_board    : 게시판 관리 (공지 설정, 글/댓글 삭제)
       ├─ can_manage_members  : 멤버 관리 (강퇴, 신청 승인/거절)
       ├─ can_manage_race     : 대회 연동 관리
       └─ can_manage_meetup   : 약속 생성/수정/삭제
  └─ MEMBER (일반 멤버)
```

### 가입 흐름

신청 → 모임장/운영진(can_manage_members) 승인 → group_member 자동 추가

### groups 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| group_idx | groupIdx | BIGSERIAL PK |
| name | name | VARCHAR(100) NOT NULL |
| description | description | TEXT |
| group_members | groupMembers | INTEGER, 그룹 인원 제한 (null = 무제한) |
| manager_members | managerMembers | INTEGER, 운영진 인원 제한 |
| area_code | areaCode | VARCHAR(20), 지역 코드 |
| tag1 ~ tag5 | tag1 ~ tag5 | VARCHAR(50), 태그 (최대 5개) |
| profile_image | profileImage | TEXT |
| owner_idx | ownerIdx | BIGINT NOT NULL |
| del_at | delAt | VARCHAR(1) DEFAULT 'N' |
| create_dt / update_dt | | TIMESTAMP |

### group_member 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| group_member_idx | groupMemberIdx | BIGSERIAL PK |
| group_idx / user_idx | | BIGINT FK (UNIQUE) |
| role | role | OWNER / MANAGER / MEMBER |
| del_at | delAt | VARCHAR(1) DEFAULT 'N' |

### group_manager_permission 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| permission_idx | permissionIdx | BIGSERIAL PK |
| group_idx / user_idx | | BIGINT FK (UNIQUE) |
| can_manage_board | canManageBoard | Y/N |
| can_manage_members | canManageMembers | Y/N |
| can_manage_race | canManageRace | Y/N |
| can_manage_meetup | canManageMeetup | Y/N |

### group_join_application 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| application_idx | applicationIdx | BIGSERIAL PK |
| group_idx / user_idx | | BIGINT FK (UNIQUE) |
| message | message | TEXT, 신청 메시지 |
| status | status | PENDING / APPROVED / REJECTED |
| processed_by | processedBy | BIGINT, 처리한 user_idx |

### group_board / group_board_comment 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| board_idx | boardIdx | BIGSERIAL PK |
| group_idx / author_idx | | BIGINT FK |
| title / content | | VARCHAR(200) / TEXT |
| is_notice | isNotice | Y/N, 공지 여부 |
| del_at | delAt | Y/N |

### group_chat 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| chat_idx | chatIdx | BIGSERIAL PK |
| group_idx / sender_idx | | BIGINT FK |
| message_type | messageType | TEXT / IMAGE / SYSTEM |
| content | content | TEXT |
| create_dt | createDt | TIMESTAMP |

### group_race 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| group_race_idx | groupRaceIdx | BIGSERIAL PK |
| group_idx / race_idx | | BIGINT FK (UNIQUE) |
| linked_by | linkedBy | BIGINT, 연동한 user_idx |
| location_share_start | locationShareStart | TIMESTAMP, 위치공유 시작 |
| location_share_end | locationShareEnd | TIMESTAMP, 위치공유 종료 |

### group_meetup / group_meetup_participant 테이블

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| meetup_idx | meetupIdx | BIGSERIAL PK |
| group_idx / created_by | | BIGINT FK |
| title / description | | VARCHAR(100) / TEXT |
| meetup_dt | meetupDt | TIMESTAMP, 약속 일시 |
| location | location | VARCHAR(200) |
| del_at | delAt | Y/N |

| DB 컬럼 | Java 필드 | 설명 |
|---------|-----------|------|
| participant_idx | participantIdx | BIGSERIAL PK |
| meetup_idx / user_idx | | BIGINT FK (UNIQUE) |
| status | status | ATTEND / ABSENT / PENDING |

### 모임 API

| Method | URL | 권한 | 설명 |
|--------|-----|------|------|
| POST | `/api/groups` | 인증 | 모임 생성 |
| GET | `/api/groups/me` | 인증 | 내 모임 목록 |
| GET | `/api/groups/{groupIdx}` | 멤버 | 모임 상세 |
| PATCH | `/api/groups/{groupIdx}` | 모임장 | 모임 수정 |
| DELETE | `/api/groups/{groupIdx}` | 모임장 | 모임 삭제 |
| POST | `/api/groups/{groupIdx}/applications` | 인증 | 가입 신청 |
| GET | `/api/groups/{groupIdx}/applications/me` | 인증 | 내 신청 상태 |
| GET | `/api/groups/{groupIdx}/applications?status=` | 모임장/운영진 | 신청 목록 |
| POST | `/api/groups/{groupIdx}/applications/{idx}/approve` | 모임장/운영진 | 승인 |
| POST | `/api/groups/{groupIdx}/applications/{idx}/reject` | 모임장/운영진 | 거절 |
| GET | `/api/groups/{groupIdx}/members` | 멤버 | 멤버 목록 |
| DELETE | `/api/groups/{groupIdx}/members/me` | 멤버 | 탈퇴 |
| DELETE | `/api/groups/{groupIdx}/members/{userIdx}` | 모임장/운영진 | 강퇴 |
| PATCH | `/api/groups/{groupIdx}/members/{userIdx}/role` | 모임장 | 역할 변경 |
| PUT | `/api/groups/{groupIdx}/members/{userIdx}/permissions` | 모임장 | 운영진 권한 설정 |
| GET/POST/PATCH/DELETE | `/api/groups/{groupIdx}/boards/**` | 멤버/운영진 | 게시판 CRUD |
| GET | `/api/groups/{groupIdx}/chat/messages` | 멤버 | 채팅 이력 (커서 페이징) |
| GET/POST/DELETE | `/api/groups/{groupIdx}/races/**` | 멤버/운영진 | 대회 연동 |
| PATCH | `/api/groups/{groupIdx}/races/{idx}/location-share` | 운영진 | 위치공유 시간 설정 |
| GET/POST/PATCH/DELETE | `/api/groups/{groupIdx}/meetups/**` | 멤버/운영진 | 약속 관리 |
| POST | `/api/groups/{groupIdx}/meetups/{idx}/respond` | 멤버 | 참가 여부 응답 |

- 약속 생성: 오늘~+10일 이내만 가능, 지난 약속은 목록에서 자동 제외
- 모임장은 탈퇴 불가 (모임 삭제로 대체)
- `group_members` null이면 인원 제한 없음

### WebSocket (STOMP)

연결: `/ws` (SockJS), STOMP CONNECT 헤더 `Authorization: Bearer {accessToken}` 필수

| 방향 | Destination | 설명 |
|------|-------------|------|
| 발행 | `/pub/groups/{groupIdx}/chat` | 채팅 전송 (`content`, `messageType`) |
| 구독 | `/sub/groups/{groupIdx}/chat` | 채팅 수신 |
| 발행 | `/pub/groups/{groupIdx}/location/{groupRaceIdx}` | 위치 전송 (`latitude`, `longitude`, `accuracy`, `timestamp`) |
| 구독 | `/sub/groups/{groupIdx}/location/{groupRaceIdx}` | 위치 수신 (브로드캐스트) |

- 위치공유는 `location_share_start ~ location_share_end` 범위 내에서만 브로드캐스트
- 위치 데이터는 DB 저장 없이 실시간 브로드캐스트만 수행

## 주의사항

- `users` 테이블의 PK는 `user_idx` — `@Id` 필드명은 `userIdx`, `@Column(name="user_idx")` 필수
- `race` 테이블의 PK는 `race_idx` — `@Id` 필드명은 `raceIdx`, `@Column(name="race_idx")` 필수
- `race` 테이블 컬럼 `racedate`, `detailurl`은 소문자 — 각각 `@Column(name="racedate")`, `@Column(name="detailurl")` 필수
- `AuthService`가 `UserDetailsService` 구현 — Spring Security가 자동으로 사용. JWT subject = `userIdx`
- JWT는 Stateless — 서버에서 토큰 강제 만료 불가
- 크롤링 대상 사이트가 EUC-KR 인코딩 — Jsoup `bodyAsBytes()` + 명시적 charset 파싱 필수
- `RaceCrawlerService.crawl()`은 `@Transactional` 필수 — 없으면 Hibernate 세션 문제로 INSERT 실패
