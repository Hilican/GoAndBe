# Architectural Design Document

## 1. Architectural Pattern: MVVM
For this Traveler App, we have chosen the **MVVM (Model-View-ViewModel)** architecture. This pattern provides a clean separation of concerns, making the app easier to test, maintain, and scale.
* **Model:** Represents our data layer (the classes defined in our domain model, such as `Trip`, `User`, and `ItineraryItem`) and handles data retrieval (via local Room database or remote APIs).
* **View:** The UI controllers (Activities/Fragments or Jetpack Compose screens). They only display data and route user actions.
* **ViewModel:** Acts as the bridge. It holds the UI logic, processes user intents from the View, and retrieves data from the Model, surviving configuration changes like screen rotations.

## 2. Domain Model Decisions
During the design phase, we expanded the core data model with the following structural decisions:
* **Centralized Trip Management:** The `Trip` class acts as the main container, directly managing `ItineraryItem` and `Image` objects. This allows operations like `modifyItineraryItem()` and `deleteImage()` to be handled safely within the context of a specific trip.
* **Authentication Separation:** Instead of bloating the `User` class with login logic, we created a dedicated `Authentication` service class. This keeps the `User` entity strictly for data holding while `Authentication` handles security operations like `register()` and `login()`.
* **Database Optimization:** All entity identifiers (IDs) were designed as `int` types to seamlessly integrate with Android's Room database auto-incrementing primary keys.
* **AI & Preferences Split:** User settings are stored in `Preferences` (the inputs), while the system's generated travel tips are isolated in an `AIRecommendations` class (the outputs), preventing data overlap.

## 3. Anticipated Technology Stack
* **Language:** Kotlin
* **Local Data Storage:** Room (SQLite abstraction)
* **UI Toolkit:** Android Jetpack Compose