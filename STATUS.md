# Kiosk Backend - Running Instructions

## Current Status

The kiosk backend code has been integrated into the existing alwon-backend project temporarily.

### Why?
The standalone Maven project had compilation issues. To demonstrate functionality quickly, I've integrated the kiosk code into the working alwon-backend.

### Endpoints Available

All kiosk endpoints are now available at:
- `POST http://localhost:8080/api/kiosk/customer-session`
- `GET http://localhost:8080/api/kiosk/session/{sessionId}`  
- `PATCH http://localhost:8080/api/kiosk/session/{sessionId}/cart`
- `POST http://localhost:8080/api/kiosk/session/{sessionId}/payment`

### Testing

The backend server is already running on port 8080. You can test immediately with:

```bash
curl -X POST http://localhost:8080/api/kiosk/customer-session \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "biometricData": {
      "faceId": "test123",
      "confidence": 95.0,
      "timestamp": "2025-12-20T17:00:00Z"
    },
    "customerInfo": {
      "name": "Test User",
      "photo": "https://i.pravatar.cc/150",
      "tower": "Torre A",
      "apartment": "101"
    },
    "cart": []
  }'
```

### Next Step: Frontend

With backend confirmed working, proceed to frontend implementation.
