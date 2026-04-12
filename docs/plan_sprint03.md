# Sprint 03 – Planning Document

## 1. Sprint Goal
Implement DataBase for the trips.
Implement authentification system.
Make sure all works via testing

---

## 2. Sprint Backlog

| ID | Tarea  | Estimación (h) | Prioridad |
|----|-------|----------------|-----------|
| T1.1 | Implement simple SQLite database (Room) | 3 | Alta |
| T1.2 | Implement simple ViewModel to interact with the database | 2 | Media |
| T1.3 | Complete ViewModel logic for Trips | 2 | Media |
| T1.4 | Modify Composables to interact with ViewModel/Database | 2 | Media |
| T2.1 | Implement User Registration (Firebase Auth) | 2 | Alta |
| T2.2 | Implement LogIn/LogOut actions (Firebase Auth) | 2 | Media |
| T2.3 | Implement Password Recovery flow | 2 | Media |

---

## 3. Definition of Done (DoD)

### General Requirements
- [ ] Code is formatted according to the project style guide.
- [ ] App compiles without errors or warnings.

### Functional Requirements
- [ ] Feature works as described in the task.
- [ ] Error cases (like no internet or wrong password) are handled.
- [ ] Logcat shows clear debug messages for success/failure.
- [ ] All DAO unit tests pass successfully

### Documentation & Maintenance
- [ ] README.md is updated with new features or setup steps.
- [ ] Domain models are correctly placed in the `domain` package.
- [ ] All implementations tested at least on the virtual phone.

---

## 4. Riesgos identificados

- Nula experiencia con SQLlite, Room, FireBase
- Falta experiencia con tests

---

⚠ Este documento no puede modificarse después del 30% del sprint.
Fecha límite modificación: DD/MM/YYYY