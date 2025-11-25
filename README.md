# 🚙 Driver Service

> **기사 및 차량 정보를 관리하고, 출근/퇴근 상태를 실시간으로 동기화하는 마이크로서비스입니다.**

## 🛠 Tech Stack
| Category | Technology                  |
| :--- |:----------------------------|
| **Language** | **Java 17**                 |
| **Framework** | Spring Boot (MVC)           |
| **Database** | MySQL (JPA), Redis (String) |

## 📡 API Specification

### Driver & Vehicle Management
| Method | URI | Auth | Description |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/drivers` | 🔐 | 기사 가입 및 차량 정보 등록 |
| `GET` | `/api/drivers/{driverId}` | 🔐 | 기사 프로필 조회 |
| `PUT` | `/api/drivers/{driverId}` | 🔐 | 기사 프로필 수정 |
| `GET` | `/api/drivers/{driverId}/vehicle` | 🔐 | 차량 정보 조회 |
| `PUT` | `/api/drivers/{driverId}/vehicle` | 🔐 | 차량 정보 수정 |
| `PUT` | `/api/drivers/{driverId}/status` | 🔐 | 출근/퇴근 상태 변경 (Redis 동기화) |

### Internal API (Backend Only)
| Method | URI | Auth | Description |
| :--- | :--- | :---: | :--- |
| `GET` | `/internal/api/drivers/{driverId}` | ❌ | **(내부용)** 기사 및 차량 정보 조회 |

> **Note:** `Internal API`는 게이트웨이를 거치지 않는 서비스 간 직접 호출용으로, JWT 검증 없이 **내부망의 신뢰성**을 기반으로 동작합니다.

## 🚀 Key Improvements (핵심 기술적 개선)

### 1. JPA 연관관계 최적화
* **Cascade:** 기사(`Driver`)와 차량(`Vehicle`)의 1:1 관계에서 **`CascadeType.ALL`**과 **`orphanRemoval`**을 적용하여, 기사 엔티티의 생명주기만 관리하면 차량 데이터도 정합성 있게 관리되도록 설계했습니다.
* **Fetch Join:** 목록 조회 시 발생할 수 있는 **N+1 문제**를 방지하기 위해 `Fetch Join` 쿼리를 적용하여 조회 성능을 최적화했습니다.
