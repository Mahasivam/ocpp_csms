# Frontend Integration Guide

## Backend Configuration
- **Backend URL**: `http://localhost:8080`
- **Frontend URL**: `http://localhost:3000`  
- **CORS**: ✅ Properly configured with `allowedOriginPatterns` for React integration
- **Transaction API**: ✅ Fixed to return only active transactions (endTimestamp IS NULL)

## Available API Endpoints

### 1. Dashboard (Real-time Stats)
```
GET /api/dashboard/stats
```
**Response Example:**
```json
{
  "totalStations": 10,
  "onlineStations": 8,
  "offlineStations": 2,
  "activeTransactions": 5,
  "totalConnectors": 20,
  "availableConnectors": 15,
  "chargingConnectors": 3,
  "faultedConnectors": 2
}
```

### 2. Charging Stations
```
GET /api/charging-stations              // Get all stations
GET /api/charging-stations/{chargePointId}   // Get specific station
GET /api/charging-stations/{chargePointId}/connectors   // Get station connectors
GET /api/charging-stations/{chargePointId}/transactions // Get station transactions
```

### 3. Remote Commands (OCPP Commands to Charge Points)
```
POST /api/charging-stations/{chargePointId}/remote-start
  ?idTag=USER123&connectorId=1

POST /api/charging-stations/{chargePointId}/remote-stop
  ?transactionId=12345

POST /api/charging-stations/{chargePointId}/reset
  ?type=Soft   // or Hard

POST /api/charging-stations/{chargePointId}/unlock-connector
  ?connectorId=1
```

### 4. Transactions
```
GET /api/transactions                   // Get all transactions
GET /api/transactions/active           // Get active transactions
GET /api/transactions/{transactionId}  // Get specific transaction
```

### 5. Configuration Management
```
GET /api/configuration/{chargePointId}
POST /api/configuration/{chargePointId}
  Body: { "key": "HeartbeatInterval", "value": "300" }
```

### 6. ID Tags (User Management)
```
GET /api/id-tags                       // Get all authorized users
POST /api/id-tags                      // Add new user
PUT /api/id-tags/{idTag}               // Update user
DELETE /api/id-tags/{idTag}            // Remove user
```

### 7. Reservations
```
GET /api/reservations                  // Get all reservations
POST /api/reservations                 // Create reservation
DELETE /api/reservations/{reservationId} // Cancel reservation
```

### 8. Meter Values (Energy Data)
```
GET /api/meter-values/{chargePointId}
GET /api/meter-values/{chargePointId}?startTime=2024-01-01&endTime=2024-01-31
```

### 9. Smart Charging (Load Management)
```
GET /api/smart-charging/{chargePointId}/profiles
POST /api/smart-charging/{chargePointId}/set-profile
DELETE /api/smart-charging/{chargePointId}/clear-profile
```

### 10. Firmware Management
```
GET /api/firmware/{chargePointId}/status
POST /api/firmware/{chargePointId}/update
POST /api/firmware/{chargePointId}/diagnostics
```

## WebSocket Connection (Real-time Updates)
```javascript
// For monitoring OCPP messages (read-only)
const ws = new WebSocket('ws://localhost:8080/ocpp/{chargePointId}');
```

## React Integration Examples

### 1. Dashboard Component
```javascript
import { useState, useEffect } from 'react';

const Dashboard = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/dashboard/stats');
        const data = await response.json();
        setStats(data);
      } catch (error) {
        console.error('Error fetching stats:', error);
      }
    };

    fetchStats();
    const interval = setInterval(fetchStats, 5000); // Update every 5 seconds
    return () => clearInterval(interval);
  }, []);

  if (!stats) return <div>Loading...</div>;

  return (
    <div>
      <h2>OCPP CSMS Dashboard</h2>
      <div>Total Stations: {stats.totalStations}</div>
      <div>Online: {stats.onlineStations}</div>
      <div>Active Transactions: {stats.activeTransactions}</div>
    </div>
  );
};
```

### 2. Charging Station Control
```javascript
const ChargingStationControl = ({ chargePointId }) => {
  const remoteStart = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/charging-stations/${chargePointId}/remote-start?idTag=USER123&connectorId=1`,
        { method: 'POST' }
      );
      const result = await response.text();
      console.log('Remote start result:', result);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const remoteStop = async (transactionId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/charging-stations/${chargePointId}/remote-stop?transactionId=${transactionId}`,
        { method: 'POST' }
      );
      const result = await response.text();
      console.log('Remote stop result:', result);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div>
      <button onClick={remoteStart}>Start Charging</button>
      <button onClick={() => remoteStop(12345)}>Stop Charging</button>
    </div>
  );
};
```

### 3. Real-time Updates with Axios
```javascript
import axios from 'axios';

// Create axios instance with base URL
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
});

// Get charging stations
export const getChargingStations = () => api.get('/charging-stations');

// Remote commands
export const remoteStartTransaction = (chargePointId, idTag, connectorId) =>
  api.post(`/charging-stations/${chargePointId}/remote-start`, null, {
    params: { idTag, connectorId }
  });

export const remoteStopTransaction = (chargePointId, transactionId) =>
  api.post(`/charging-stations/${chargePointId}/remote-stop`, null, {
    params: { transactionId }
  });
```

## Common Integration Issues & Solutions

### 1. CORS Issues
- ✅ **Fixed**: Added comprehensive CORS configuration
- Backend now allows requests from `localhost:3000`

### 2. API Base URL
```javascript
// Use this base URL in your React app
const API_BASE_URL = 'http://localhost:8080/api';
```

### 3. WebSocket Connection
```javascript
// For real-time charging station monitoring
const connectWebSocket = (chargePointId) => {
  const ws = new WebSocket(`ws://localhost:8080/ocpp/${chargePointId}`);
  
  ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('OCPP Message:', message);
  };
  
  return ws;
};
```

## Next Steps for React Integration

1. **Install Required Packages**:
   ```bash
   npm install axios
   ```

2. **Create API Service**:
   - Create `/src/services/api.js` with axios configuration
   - Add all endpoint functions

3. **Add Environment Variables**:
   ```javascript
   // .env.local
   REACT_APP_API_BASE_URL=http://localhost:8080/api
   REACT_APP_WS_BASE_URL=ws://localhost:8080/ocpp
   ```

4. **Test Endpoints**:
   - Start with `/api/dashboard/stats`
   - Verify CORS is working
   - Test remote commands

The backend is now properly configured for React integration!