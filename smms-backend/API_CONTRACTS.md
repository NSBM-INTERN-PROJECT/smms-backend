# SMMS Report Service — Required API Contracts

The report service owns no database. It aggregates these endpoints through OpenFeign.
The owners of the other services must confirm or implement these contracts before the
dashboard can return live data.

## User service

`GET /api/users/statistics`

```json
{
  "totalStudents": 120,
  "totalMentors": 15
}
```

## Allocation service

`GET /api/allocations/statistics`

```json
{
  "allocatedStudents": 110,
  "unallocatedStudents": 10
}
```

## Meeting service

`GET /api/meetings/statistics`

```json
{
  "totalMeetings": 105,
  "completedMeetings": 85,
  "pendingMeetings": 20,
  "attendanceRate": 87.5
}
```

## Session service

`GET /api/sessions/statistics`

```json
{
  "atRiskStudents": 8,
  "openEscalations": 3
}
```

The incoming `Authorization: Bearer <JWT>` header is forwarded to every downstream
service. Each downstream service remains responsible for its own authorization checks.
