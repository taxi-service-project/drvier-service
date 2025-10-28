# MSA 기반 Taxi 호출 플랫폼 - Driver Service

Taxi 호출 플랫폼의 택시 기사 정보 및 차량 정보 관리를 담당하는 마이크로서비스입니다. 기사의 프로필, 차량 정보, 실시간 운행 상태 등을 관리합니다.

## 주요 기능 (API Endpoints)

### 기사 관리 (`/api/drivers`)

* `POST /`: 신규 기사 생성 (차량 정보 포함)
* `GET /{driverId}`: 기사 프로필 조회
* `PUT /{driverId}`: 기사 프로필 수정
* `PUT /{driverId}/status`: 기사 실시간 운행 상태 변경 - **Redis 사용**

### 차량 관리 (`/api/drivers/{driverId}/vehicle`)

* `GET /`: 기사의 차량 정보 조회
* `PUT /`: 기사의 차량 정보 수정

## 기술 스택

* **Language & Framework:** Java, Spring Boot
* **Database:** Spring Data JPA, MySQL
* **Cache:** Spring Data Redis
