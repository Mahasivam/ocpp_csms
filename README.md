# OCPP 1.6J CSMS - Complete Implementation Guide

## 🚀 Project Overview

This is a production-ready Charging Station Management System (CSMS) implementing the OCPP 1.6J protocol. The system provides a comprehensive solution for managing electric vehicle charging infrastructure with real-time monitoring, remote control capabilities, and a modern web interface.

## 📋 Prerequisites

### Required Software

- **Java 17** (OpenJDK or Oracle JDK)
- **Node.js 16+** with npm
- **PostgreSQL 12+**
- **Maven 3.6+**
- **Git**

### Optional (for development)

- **Docker & Docker Compose**
- **IntelliJ IDEA** or **VS Code**
- **Postman** for API testing
- **OCPP Charge Point Simulator**

## 🛠 Installation & Setup

### Step 1: Clone and Setup Project Structure

```bash
# Create project directory
mkdir ocpp-csms
cd ocpp-csms

# Create backend directory
mkdir backend
cd backend

# Initialize Spring Boot project structure
mkdir -p src/main/java/com/csms
mkdir -p src/main/resources
mkdir -p src/test/java/com/csms
mkdir -p src/test/resources

# Create frontend directory
cd ../
mkdir frontend

# Create scripts directory
mkdir scripts
```

### Step 2: Database Setup

```bash
# Connect to PostgreSQL
sudo -u postgres psql

# Create user and database
CREATE USER ocpp_user WITH PASSWORD 'ocpp_password';
CREATE DATABASE ocpp_csms OWNER ocpp_user;
GRANT ALL PRIVILEGES ON DATABASE ocpp_csms TO ocpp_user;

# Exit and connect to new database
\q
psql -h localhost -U ocpp_user -d ocpp_csms

# Run the provided SQL schema file
\i database_schema.sql
```

### Step 3: Backend Setup

1. **Copy all Java files** from the artifacts into the `backend/src/main/java/com/csms/` directory following the package structure.

2. **Copy the pom.xml** to the backend root directory.

3. **Copy application.yml** to `backend/src/main/resources/`.

4. **Build and run the backend:**

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Step 4: Frontend Setup

1. **Initialize React application:**

```bash
cd frontend
npx create-react-app . --template typescript
```

2. **Install additional dependencies:**

```bash
npm install axios react-router-dom recharts lucide-react
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

3. **Copy all React files** from the artifacts.

4. **Start the development server:**

```bash
npm start
```

## 🔧 Configuration

### Environment Variables

Create `.env` file in backend directory:

```bash
DB_USERNAME=ocpp_user
DB_PASSWORD=ocpp_password
DB_URL=jdbc:postgresql://localhost:5432/ocpp_csms
OCPP_HEARTBEAT_TIMEOUT=600
OCPP_WEBSOCKET_PATH=/ocpp
```

### Application Properties

The system uses the following key configurations:

- **WebSocket Endpoint:** `ws://localhost:8080/ocpp/{chargePointId}`
- **API Base URL:** `http://localhost:8080/api`
- **Frontend URL:** `http://localhost:3000`
- **Database:** PostgreSQL with UUID primary keys

## 🧪 Testing Your OCPP Implementation

### Manual Testing with WebSocket Client

You can test the OCPP implementation using any WebSocket client:

#### 1. Boot Notification Test

```json
[2,"msg001","BootNotification",{
  "chargePointVendor":"TestVendor",
  "chargePointModel":"TestModel",
  "chargePointSerialNumber":"CP001SN",
  "firmwareVersion":"1.0.0"
}]
```

**Expected Response:**
```json
[3,"msg001",{
  "status":"Accepted",
  "currentTime":"2024-01-15T10:30:00.000Z",
  "interval":300
}]
```

#### 2. Status Notification Test

```json
[2,"msg002","StatusNotification",{
  "connectorId":1,
  "status":"Available",
  "errorCode":"NoError",
  "timestamp":"2024-01-15T10:30:00.000Z"
}]
```

#### 3. Start Transaction Test

```json
[2,"msg003","StartTransaction",{
  "connectorId":1,
  "idTag":"04E91C5A123456",
  "meterStart":1000,
  "timestamp":"2024-01-15T10:30:00.000Z"
}]
```

#### 4. Heartbeat Test

```json
[2,"msg004","Heartbeat",{}]
```

### Using OCPP Charge Point Simulator

1. **Download and configure an OCPP 1.6J simulator**
   - Steve (SteVe) - Open source OCPP simulator
   - OCPP Test Tool
   - Custom simulator

2. **Configure simulator connection:**
   - URL: `ws://localhost:8080/ocpp/CP001`
   - Protocol: OCPP 1.6J
   - Charge Point ID: `CP001`

3. **Test complete flow:**
   - Send BootNotification
   - Send StatusNotification for connectors
   - Send periodic Heartbeat
   - Test authorization with Authorize message
   - Start and stop transactions
   - Send meter values

## 📊 OCPP 1.6J Features Implemented

### Core Profile ✅

- **BootNotification** - Charging station registration
- **Authorize** - ID tag authorization
- **StartTransaction** - Begin charging session
- **StopTransaction** - End charging session
- **Heartbeat** - Keep-alive messages
- **MeterValues** - Energy consumption data
- **StatusNotification** - Connector status updates
- **DataTransfer** - Vendor-specific data exchange

### Remote Control Profile ✅

- **RemoteStartTransaction** - Start charging remotely
- **RemoteStopTransaction** - Stop charging remotely
- **UnlockConnector** - Unlock connector remotely
- **Reset** - Reset charging station
- **GetConfiguration** - Retrieve configuration
- **ChangeConfiguration** - Update configuration
- **ClearCache** - Clear authorization cache

### Reservation Profile ✅

- **ReserveNow** - Reserve connector
- **CancelReservation** - Cancel reservation

### Additional Features

- Real-time WebSocket communication
- Transaction management
- ID tag management
- Configuration management
- Heartbeat monitoring
- Comprehensive logging

## 🎯 API Endpoints Reference

### Charging Stations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/charging-stations` | List all stations |
| `GET` | `/api/charging-stations/{id}` | Get specific station |
| `GET` | `/api/charging-stations/{id}/connectors` | Get connectors |
| `GET` | `/api/charging-stations/{id}/transactions` | Get transactions |
| `POST` | `/api/charging-stations/{id}/remote-start` | Remote start |
| `POST` | `/api/charging-stations/{id}/remote-stop` | Remote stop |
| `POST` | `/api/charging-stations/{id}/reset` | Reset station |

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/transactions` | List all transactions |
| `GET` | `/api/transactions/{id}` | Get specific transaction |
| `GET` | `/api/transactions/active` | Get active transactions |

### ID Tags

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/id-tags` | List all ID tags |
| `POST` | `/api/id-tags` | Create new ID tag |
| `PUT` | `/api/id-tags/{id}` | Update ID tag |
| `DELETE` | `/api/id-tags/{id}` | Delete ID tag |

## 🌐 WebSocket Events

### Client → Server (Charge Point → CSMS)

- `BootNotification`
- `StatusNotification` 
- `Authorize`
- `StartTransaction`
- `StopTransaction`
- `Heartbeat`
- `MeterValues`
- `DataTransfer`

### Server → Client (CSMS → Charge Point)

- `RemoteStartTransaction`
- `RemoteStopTransaction`
- `Reset`
- `UnlockConnector`
- `GetConfiguration`
- `ChangeConfiguration`
- `ClearCache`
- `ReserveNow`
- `CancelReservation`

## 🚦 Getting Started

1. **Clone the repository**
2. **Follow the installation steps** in order
3. **Start the backend** server first
4. **Start the frontend** development server
5. **Test with a WebSocket client** or OCPP simulator
6. **Access the web interface** at `http://localhost:3000`

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support and questions, please open an issue in the GitHub repository.

---

**Built with ❤️ for the EV charging community**
