# Backend Integration Guide

## Issue Identified ✅

Your backend is responding with `[4,"test123","NotSupported","Action not supported",{}]` for CSMS commands, which means it only handles **Charge Point → CSMS** messages but not **CSMS → Charge Point** commands.

## Required Backend Changes

Your backend needs to handle these CSMS → Charge Point commands:

### 1. RemoteStartTransaction
**Frontend sends:**
```json
[2,"ui_1756589484075","RemoteStartTransaction",{"idTag":"04E91C5A123456","connectorId":1}]
```

**Backend should respond:**
```json
[3,"ui_1756589484075",{"status":"Accepted"}]
```

### 2. RemoteStopTransaction
**Frontend sends:**
```json
[2,"ui_1756589484076","RemoteStopTransaction",{"transactionId":12345}]
```

**Backend should respond:**
```json
[3,"ui_1756589484076",{"status":"Accepted"}]
```

### 3. Reset
**Frontend sends:**
```json
[2,"ui_1756589484077","Reset",{"type":"Soft"}]
```

**Backend should respond:**
```json
[3,"ui_1756589484077",{"status":"Accepted"}]
```

### 4. UnlockConnector
**Frontend sends:**
```json
[2,"ui_1756589484078","UnlockConnector",{"connectorId":1}]
```

**Backend should respond:**
```json
[3,"ui_1756589484078",{"status":"Accepted"}]
```

## Backend Implementation Template

Add these handlers to your WebSocket message processor:

```java
// In your OcppWebSocketHandler or similar class
private void handleIncomingMessage(String chargePointId, String message) {
    try {
        JsonArray parsedMessage = JsonParser.parseString(message).getAsJsonArray();
        int messageType = parsedMessage.get(0).getAsInt();
        String messageId = parsedMessage.get(1).getAsString();
        String action = parsedMessage.get(2).getAsString();
        JsonObject payload = parsedMessage.get(3).getAsJsonObject();

        if (messageType == 2) { // CALL
            switch (action) {
                // Existing CP → CSMS handlers
                case "BootNotification":
                    handleBootNotification(chargePointId, messageId, payload);
                    break;
                case "StatusNotification":
                    handleStatusNotification(chargePointId, messageId, payload);
                    break;
                case "Heartbeat":
                    handleHeartbeat(chargePointId, messageId, payload);
                    break;
                case "Authorize":
                    handleAuthorize(chargePointId, messageId, payload);
                    break;
                case "StartTransaction":
                    handleStartTransaction(chargePointId, messageId, payload);
                    break;
                case "StopTransaction":
                    handleStopTransaction(chargePointId, messageId, payload);
                    break;
                case "MeterValues":
                    handleMeterValues(chargePointId, messageId, payload);
                    break;

                // NEW: CSMS → CP handlers (ADD THESE)
                case "RemoteStartTransaction":
                    handleRemoteStartTransaction(chargePointId, messageId, payload);
                    break;
                case "RemoteStopTransaction":
                    handleRemoteStopTransaction(chargePointId, messageId, payload);
                    break;
                case "Reset":
                    handleReset(chargePointId, messageId, payload);
                    break;
                case "UnlockConnector":
                    handleUnlockConnector(chargePointId, messageId, payload);
                    break;
                    
                default:
                    sendErrorResponse(chargePointId, messageId, "NotSupported", "Action not supported");
            }
        }
    } catch (Exception e) {
        log.error("Error processing message", e);
    }
}

// NEW HANDLER METHODS (ADD THESE)
private void handleRemoteStartTransaction(String chargePointId, String messageId, JsonObject payload) {
    try {
        String idTag = payload.get("idTag").getAsString();
        int connectorId = payload.get("connectorId").getAsInt();
        
        // Your business logic here
        log.info("Remote start requested for {} on connector {}", idTag, connectorId);
        
        // For now, just accept all requests
        JsonObject response = new JsonObject();
        response.addProperty("status", "Accepted");
        sendResponse(chargePointId, messageId, response);
        
    } catch (Exception e) {
        sendErrorResponse(chargePointId, messageId, "InternalError", "Failed to process RemoteStartTransaction");
    }
}

private void handleRemoteStopTransaction(String chargePointId, String messageId, JsonObject payload) {
    try {
        int transactionId = payload.get("transactionId").getAsInt();
        
        log.info("Remote stop requested for transaction {}", transactionId);
        
        JsonObject response = new JsonObject();
        response.addProperty("status", "Accepted");
        sendResponse(chargePointId, messageId, response);
        
    } catch (Exception e) {
        sendErrorResponse(chargePointId, messageId, "InternalError", "Failed to process RemoteStopTransaction");
    }
}

private void handleReset(String chargePointId, String messageId, JsonObject payload) {
    try {
        String type = payload.get("type").getAsString(); // "Hard" or "Soft"
        
        log.info("Reset ({}) requested for {}", type, chargePointId);
        
        JsonObject response = new JsonObject();
        response.addProperty("status", "Accepted");
        sendResponse(chargePointId, messageId, response);
        
    } catch (Exception e) {
        sendErrorResponse(chargePointId, messageId, "InternalError", "Failed to process Reset");
    }
}

private void handleUnlockConnector(String chargePointId, String messageId, JsonObject payload) {
    try {
        int connectorId = payload.get("connectorId").getAsInt();
        
        log.info("Unlock connector {} requested for {}", connectorId, chargePointId);
        
        JsonObject response = new JsonObject();
        response.addProperty("status", "Accepted");
        sendResponse(chargePointId, messageId, response);
        
    } catch (Exception e) {
        sendErrorResponse(chargePointId, messageId, "InternalError", "Failed to process UnlockConnector");
    }
}

private void sendResponse(String chargePointId, String messageId, JsonObject payload) {
    JsonArray response = new JsonArray();
    response.add(3); // CALLRESULT
    response.add(messageId);
    response.add(payload);
    
    // Send via your WebSocket session
    sendMessage(chargePointId, response.toString());
}

private void sendErrorResponse(String chargePointId, String messageId, String errorCode, String errorDescription) {
    JsonArray response = new JsonArray();
    response.add(4); // CALLERROR
    response.add(messageId);
    response.add(errorCode);
    response.add(errorDescription);
    response.add(new JsonObject()); // details
    
    sendMessage(chargePointId, response.toString());
}
```

## Testing After Backend Changes

Once you implement the handlers, test using the WebSocket Debugger:

1. Connect to CP001
2. Click "Remote Start" button
3. You should see: `[3,"ui_...",{"status":"Accepted"}]` instead of the error

## Current Status

✅ **Frontend integration complete**
❌ **Backend missing CSMS command handlers** ← Fix this
✅ **WebSocket connection working**
✅ **CP→CSMS messages working**

## Quick Test

After implementing, try this command in the debugger:
```json
[2,"test123","RemoteStartTransaction",{"idTag":"04E91C5A123456","connectorId":1}]
```

Should respond with:
```json
[3,"test123",{"status":"Accepted"}]
```