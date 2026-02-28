# Trimly Backend — Spring Boot REST API

A production-ready backend for the **Trimly** barber booking platform.

---

## 🚀 Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Framework   | Spring Boot 3.2 (Java 17)         |
| Security    | Spring Security + JWT (JJWT 0.11) |
| Database    | MySQL 8.x + Spring Data JPA       |
| ORM         | Hibernate (auto-creates tables)   |
| Build Tool  | Maven                             |

---

## 📁 Project Structure

```
src/main/java/com/trimly/
├── TrimlyApplication.java          # Entry point
├── config/
│   ├── SecurityConfig.java         # CORS, JWT filter chain
│   └── DataSeeder.java             # Seeds DB on first run
├── controller/
│   ├── AuthController.java         # /api/auth/**
│   ├── ShopController.java         # /api/shops/**
│   ├── BookingController.java      # /api/bookings/**
│   ├── NotificationController.java # /api/notifications/**
│   └── AdminController.java        # /api/admin/**
├── dto/                            # Request/Response DTOs
├── entity/                         # JPA Entities
├── enums/                          # Role, ShopStatus, BookingStatus, NotificationType
├── exception/                      # Global error handling
├── repository/                     # Spring Data JPA repos
├── security/                       # JWT utility + filter
├── service/                        # Business logic
└── util/                           # SecurityUtils
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- MySQL 8.x running on localhost:3306
- Maven 3.8+

### 1. Configure Database

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trimly_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Or set environment variables (for production):
```bash
export DB_URL="jdbc:mysql://your-host:3306/trimly_db?createDatabaseIfNotExist=true&useSSL=true"
export DB_USERNAME="your_user"
export DB_PASSWORD="your_password"
export JWT_SECRET="your-256-bit-secret-key-here-at-least-32-chars"
export ALLOWED_ORIGINS="https://your-frontend.com"
```

### 2. Build & Run

```bash
# Development
mvn spring-boot:run

# Production build
mvn clean package -DskipTests
java -jar target/trimly-backend-1.0.0.jar --spring.profiles.active=prod
```

The server starts on **http://localhost:8080**

---

## 🌱 Initial Data (Auto-Seeded)

On first launch, the following demo data is inserted:

| Role    | Email                  | Password  | Notes                   |
|---------|------------------------|-----------|-------------------------|
| Admin   | admin@trimly.app       | admin123  | Platform administrator  |
| Barber  | rajan@blade.com        | 1234      | Blade & Co. (Active)    |
| Barber  | suresh@dapper.com      | 1234      | The Dapper Den (Active) |
| Barber  | irfan@royalcuts.com    | 1234      | Royal Cuts (Pending)    |
| Customer| name: Arjun Mehta      | —         | phone: 9876543210       |

---

## 🔌 API Reference

### Base URL
```
http://localhost:8080/api
```

### Authentication

All protected endpoints require:
```
Authorization: Bearer <JWT_TOKEN>
```

---

### 🔐 Auth Endpoints

| Method | URL                        | Body                                        | Description           |
|--------|----------------------------|---------------------------------------------|-----------------------|
| POST   | `/auth/admin/login`        | `{email, password}`                         | Admin login           |
| POST   | `/auth/barber/login`       | `{email, password}`                         | Barber login          |
| POST   | `/auth/barber/register`    | `{ownerName, email, password, shopName, location, phone}` | Register barber |
| POST   | `/auth/customer/login`     | `{name, phone}`                             | Customer login (no password) |

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "token": "eyJhbGci...",
    "role": "BARBER",
    "userId": 1,
    "name": "Rajan Sharma",
    "shopId": 1,
    "shopStatus": "ACTIVE"
  }
}
```

---

### 🏪 Shop Endpoints

#### Public (No Auth)
| Method | URL                              | Description                    |
|--------|----------------------------------|--------------------------------|
| GET    | `/shops/public`                  | List all active shops          |
| GET    | `/shops/public/{id}`             | Get shop details               |
| GET    | `/shops/{id}/services`           | Get shop's enabled services    |
| GET    | `/shops/{id}/slots?date=2024-01-01` | Get available slots for date |

#### Barber (Auth: BARBER)
| Method | URL                              | Description                    |
|--------|----------------------------------|--------------------------------|
| GET    | `/shops/my`                      | Get my shop                    |
| PUT    | `/shops/my`                      | Update shop settings           |
| POST   | `/shops/my/services`             | Add a service                  |
| PUT    | `/shops/my/services/{id}`        | Update a service               |
| DELETE | `/shops/my/services/{id}`        | Delete a service               |

#### Admin (Auth: ADMIN)
| Method  | URL                              | Description                    |
|---------|----------------------------------|--------------------------------|
| GET     | `/shops`                         | List all shops                 |
| PATCH   | `/shops/{id}/status?status=ACTIVE` | Approve/disable a shop       |

---

### 📅 Booking Endpoints

#### Customer (Auth: CUSTOMER)
| Method | URL                              | Body                     | Description         |
|--------|----------------------------------|--------------------------|---------------------|
| POST   | `/bookings`                      | CreateBookingRequest     | Create a booking    |
| GET    | `/bookings/my`                   | —                        | My bookings         |
| PATCH  | `/bookings/my/{id}/cancel`       | —                        | Cancel booking      |
| POST   | `/bookings/my/{id}/rate`         | `{rating, comment}`      | Rate booking        |

**CreateBookingRequest:**
```json
{
  "shopId": 1,
  "serviceIds": [1, 2],
  "slot": "10:00 AM",
  "slotId": "1000",
  "bookingDate": "2024-01-15",
  "customerName": "John Doe",
  "customerPhone": "9876543210"
}
```

#### Barber (Auth: BARBER)
| Method | URL                              | Body                     | Description           |
|--------|----------------------------------|--------------------------|-----------------------|
| GET    | `/bookings/shop`                 | —                        | All my shop bookings  |
| PATCH  | `/bookings/shop/{id}/status`     | `{status}`               | Accept/reject/complete|

Status values: `CONFIRMED`, `REJECTED`, `COMPLETED`, `CANCELLED`

#### Admin (Auth: ADMIN)
| Method | URL           | Description      |
|--------|---------------|------------------|
| GET    | `/bookings`   | All bookings     |

---

### 🔔 Notification Endpoints (Auth: Any)

| Method | URL                          | Description              |
|--------|------------------------------|--------------------------|
| GET    | `/notifications`             | My notifications         |
| GET    | `/notifications/unread-count`| Unread count             |
| PATCH  | `/notifications/{id}/read`   | Mark one as read         |
| POST   | `/notifications/mark-all-read` | Mark all as read        |

---

### 📊 Admin Endpoints (Auth: ADMIN)

| Method | URL             | Description         |
|--------|-----------------|---------------------|
| GET    | `/admin/stats`  | Platform statistics |

---

## 🏗️ Database Schema

Tables auto-created by Hibernate:

- **users** — All users (admin, barbers, customers)
- **shops** — Barber shop profiles
- **services** — Shop services (many per shop)
- **bookings** — Customer bookings
- **notifications** — In-app notifications

---

## 🔒 Security

- JWT tokens expire in 24 hours (configurable via `app.jwt.expiration-ms`)
- BCrypt password hashing (strength 10)
- CORS configured for Angular frontend at `localhost:4200`
- Role-based access: `ROLE_ADMIN`, `ROLE_BARBER`, `ROLE_CUSTOMER`
- Customers authenticate with name + phone (no password needed)

---

## 🌐 Environment Variables (Production)

| Variable          | Default                        | Description            |
|-------------------|--------------------------------|------------------------|
| `DB_URL`          | jdbc:mysql://localhost/trimly_db | MySQL connection URL  |
| `DB_USERNAME`     | root                           | DB username            |
| `DB_PASSWORD`     | root                           | DB password            |
| `JWT_SECRET`      | (hardcoded dev key)            | ≥ 32 char secret       |
| `JWT_EXPIRATION`  | 86400000 (24h)                 | Token expiry in ms     |
| `ALLOWED_ORIGINS` | localhost:4200,localhost:3000  | CORS origins           |
| `PORT`            | 8080                           | Server port            |

---

## 🚢 Deployment

### Docker (recommended)

```dockerfile
FROM eclipse-temurin:17-jre
COPY target/trimly-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
```

```bash
docker build -t trimly-backend .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host:3306/trimly_db?createDatabaseIfNotExist=true" \
  -e DB_USERNAME="user" \
  -e DB_PASSWORD="pass" \
  -e JWT_SECRET="your-super-secret-key-minimum-32-chars" \
  -e ALLOWED_ORIGINS="https://your-domain.com" \
  trimly-backend
```

### Heroku / Railway / Render

Set environment variables and deploy the JAR. The app will auto-create the database schema on first boot.

---

## ✅ Frontend Integration Notes

When building the Angular frontend:

1. **Base URL**: `http://localhost:8080/api`
2. **Auth header**: `Authorization: Bearer <token>`
3. **Customer flow**: POST `/auth/customer/login` with `{name, phone}` → get token → use for all booking actions
4. **Slot date format**: `YYYY-MM-DD` (ISO date string)
5. **Polling for notifications**: GET `/notifications/unread-count` every 15-30s
# trimly-backend
