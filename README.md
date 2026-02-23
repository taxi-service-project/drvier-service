# 🚙 Driver Service

> **기사 및 차량 정보를 관리하는 마이크로서비스입니다.**

## 🛠 Tech Stack
| Category | Technology                  |
| :--- |:----------------------------|
| **Language** | **Java 17**                 |
| **Framework** | Spring Boot (MVC)           |
| **Database** | MySQL (Spring Data JPA), Redis (String) |

## 📡 API Specification

### Driver & Vehicle Management
| Method | URI | Auth | Description |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/drivers` | 🔐 | 기사 가입 (user-service 연동) |
| `GET` | `/api/drivers/{driverId}` | 🔐 | 기사 프로필 조회 |
| `PUT` | `/api/drivers/{driverId}` | 🔐 | 기사 프로필 수정 |
| `GET` | `/api/drivers/{driverId}/vehicle` | 🔐 | 차량 정보 조회 |
| `PUT` | `/api/drivers/{driverId}/vehicle` | 🔐 | 차량 정보 수정 |
| `PUT` | `/api/drivers/{driverId}/status` | 🔐 | 출근/퇴근 상태 변경 (Redis 동기화) |

### Internal API (Microservice Communication)
| Method | URI | Auth | Description |
| :--- | :--- | :---: | :--- |
| `GET` | `/internal/api/drivers/{driverId}` | ❌ | **[내부망]** 기사 및 차량 통합 정보 조회 |




----------

## 아키텍쳐
<img width="2324" height="1686" alt="Image" src="https://github.com/user-attachments/assets/81a25ff9-ee02-4996-80d3-f9217c3b7750" />
