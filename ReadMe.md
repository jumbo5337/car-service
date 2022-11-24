**Car-service** 

Service for managing charge points used to charge electric vehicles.

Service has 5 endpoints.

**1) GET /api/v1/app-info HTTP/1.1 - Get application info**\
This method returns information about application and schema versions, doesn't require authentication:

Response example:
```json
{
    "code": 0,
    "appVersion": "0.0.1",
    "schemaVersion": "2",
    "ts": "2022-11-24T11:00:36.42518"
}
```
Response params: 
- *appVersion* - application version
- *schemaVersion* - schema version


**2) PUT /api/v1/admin/connector - Add new connector.**\
This method adds connector for existing charge point, requires admin authentication. 

Request example: 
```shell
curl --location --request PUT 'http://localhost:8080/api/v1/admin/connector?chargePoint=99' \
--header 'Authorization: Basic SOME_TOKEN=='
```
Request param: 
- *chargePoint* - integer value, id of charge point to add new connector


Response example: 
```json
{
    "code": 0,
    "message": "OK",
    "id": 99
}
```

Response params:
- *code* - Integer, Operation result code
- *message* - String, Operation result message
- *id* - Long, id of added connector (absent if operation result isn't successful)

**3) GET /api/v1/admin/sessions - Get sessions from database.**\
This method return sessions from database, requires admin authentication.

Request param:
- *from* - datetime, parameter specifies the earliest time border of transactions start timestamp (Optional)
- *to* - datetime, parameter specifies the latest time border of transactions end timestamp (Optional)

Response example: 
```json
{
  "code": 0,
  "message": "OK",
  "sessions": [
    {
      "id": "d97cae22-02e4-4edd-98e1-5208188bfb1b",
      "startTime": "2022-11-24T11:04:35.389143",
      "startMeter": 0.1,
      "endTime": "2022-11-24T11:14:45.582047",
      "endMeter": 0.1,
      "isCompleted": true,
      "isError": true,
      "rfidTag": {
        "id": 1,
        "name": "VASYA_RFID_1"
      },
      "connector": 1,
      "chargePoint": {
        "id": 1,
        "name": "VASYA_CP1"
      },
      "customer": {
        "id": 1,
        "name": "Vasya"
      },
      "vehicle": {
        "registrationPlate": "0001UZ",
        "name": "COBALT"
      }
    }
  ]
}
```

Response params:
- *code* - Integer, operation result code
- *message* - String, Operation result message
- *sessions* - list of charge sessions
- *sessions.id* - UUID, id of charge session
- *sessions.startTime* - Datetime, start time of charging session 
- *sessions.startMeter* - Double, start meter of charging session
- *sessions.endTime* - Datetime, end time of charging session (optional)
- *sessions.endMeter* - Double, end meter of charging session (optional)
- *sessions.isCompleted* - Boolean, marker of ongoing session
- *sessions.isError* - Boolean, marker of error session
- *sessions.rfidtag.id* - Long, id of RFID tag
- *sessions.rfidtag.name* - String, name of RFID tag
- *sessions.connector* - Long, id of connector
- *sessions.chargePoint.id* - Long, id of charge point
- *sessions.chargePoint.name* - String, name of charge point
- *sessions.customer.id* - Long, id of customer
- *sessions.customer.name* - String, name of customer
- *sessions.vehicle.registrationPlate* - String, plate number of charging car
- *sessions.vehicle.name* - String, name of charging car

**4) POST /api/v1/session/init - init charge session**\
   This method return sessions from database, requires customer authentication. RFID tag can have only one open session.

Request Example:
```shell
curl --location --request POST 'http://localhost:8080/api/v1/session/init' \
--header 'Authorization: Basic VmFzeWE6MTIzNA==' \
--header 'Content-Type: application/json' \
--data-raw '{
    "connector": 1,
    "rfidNumber": 1,
    "startMeter": 0.1
}'
```

Request param:
- *connector* - integer value, id of connector
- *rfidNumber* - integer value, id of rfid tag
- *startMeter* - double value, start meter of session
- *startTime* - datetime, start time of session (optional, if not specified current time is used)

Response example: 
```json
{
    "code": 0,
    "message": "OK",
    "id": "e154a858-3ac4-4170-a63c-6374117e8ba3"
}
```
- *code* - Integer, Operation result code
- *message* - String, Operation result message
- *id* - Long, id created session (absent if operation result isn't successful)

**5) POST /api/v1/session/{sessionId}/init - complete charge session**\
   This method return sessions from database, requires customer authentication. RFID tag can have only one open session.

Request Example:
```shell
curl --location --request POST 'http://localhost:8080/api/v1/session/e154a858-3ac4-4170-a63c-6374117e8ba3/complete' \
--header 'Authorization: Basic VmFzeWE6MTIzNA==' \
--header 'Content-Type: application/json' \
--data-raw '{
    "connector": 1,
    "rfidNumber": 1,
    "endMeter": 0.1
}'
```

Request param:
- *connector* - integer value, id of connector
- *rfidNumber* - integer value, id of rfid tag
- *endMeter* - double value, start meter of session
- *endTime* - datetime, start time of session (optional, if not specified current time is used)

Response example:
```json
{
    "code": 0,
    "message": "OK",
    "id": "e154a858-3ac4-4170-a63c-6374117e8ba3"
}
```
- *code* - Integer, Operation result code
- *message* - String, Operation result message
- *id* - Long, id closed session (absent if operation result isn't successful)


**Datetime formats**\
Application supports different date time formats:
- _2020-12-01T12:22:15.122_
- _2020-12-01 12:22:22.122_
- _2020-12-01T12:22:15_
- _2020-12-01 12:22:15_
- _2020-12-01T12:22_
- _2020-12-01 12:22_
- _2020-12-01_
- _2020/12/01_

**Example**
*example.sh* runs some demo, start of the scrip requires curl and jq