# Sprint 04 – Planning Document

## 1. Sprint Goal
Connect the application with a hotel listing page that displays room details and imagery. Users must be able to create and cancel hotel reservations linked to specific Trips. The system should provide a visual indicator on the Trip overview to show if a reservation is attached. Additionally, users should be able to upload and view custom images within the Trip Details section.

---

## 2. Sprint Backlog

I plan to do all T1.x with the bare minium to understand how does it work (doing a small implementation)
After that just grow the code

| ID | Tarea  | Estimación (h) | Prioridad |
|----|-------|----------------|-----------|
| T1.1 | Learn/Study REST API provided | 1 | Alta |
| T1.2 | Implement Retrofit | 1.5 | Alta |
| T1.3 | Implement a Repository to manage Retrofit | 1.5 | Alta |
| T1.4 | Implement MVVM using the new Repository | 1.5 | Alta |
| T1.5 | Create Tests to check MVVM (Validation) | 1 | Alta |
| T1.6 | Create screens/Composables needed to achieve the goals | 2 | Alta |
| T1.7 | Expand MVVM, screens... to fulfill all requeriments * | 5 | Medium |
| T2.1 | Implement add image button on Trips | 2 | Alta |
| T2.2 | Display images of a Trip on Trip Details | 2 | Medium |
*Modify other classes if needed
---

## 3. Definition of Done (DoD)

### General Requirements
- [ ] Code is formatted according REST API.
- [ ] App compiles without errors.

### Functional Requirements
- [ ] Feature works as described in the task.
- [ ] Error cases (like no internet or page down) are handled.
- [ ] Logcat shows clear debug messages for success/failure.
- [ ] All tests pass successfully

### Documentation & Maintenance
- [ ] README.md is updated with new features or setup steps.
- [ ] Domain models are correctly placed in the `domain` package.
- [ ] All implementations tested at least on the virtual phone.

---

## 4. Riesgos identificados

- Nula experiencia con Retrofit
- Desconocimiento de la REST API
- Desconocimiento de como tratar con de la galeria del movil

---

⚠ Este documento no puede modificarse después del 30% del sprint.
Fecha límite modificación: 11/05/2026
