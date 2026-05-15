# Go And Be - Planificador de Viajes

## POR REVISAR
Este es el archivo de documentación (`README.md`) para la aplicación **Go And Be**, una solución moderna de gestión de viajes diseñada para Android.

---

## 🎨 Capa de Presentación (UI) y Sistema de Navegación

La interfaz de usuario de **Go And Be** está desarrollada de manera nativa y 100% declarativa utilizando **Jetpack Compose** y componentes de **Material Design 3**. Las pantallas son "componentes puros" que carecen de lógica interna pesada; se limitan a observar el estado expuesto por los ViewModels y a renderizar los cambios reactivamente.

### 🧭 1. Arquitectura de Navegación (Jetpack Compose Navigation)
Para enlazar las pantallas sin acoplar el código de los composables, se centraliza la navegación mediante un grafo estructurado:
* **`_Routes.kt`:** Define de forma estática o mediante estructuras fuertemente tipadas las rutas de la aplicación, asegurando la consistencia en los identificadores de destino y simplificando el paso de argumentos entre pantallas.
* **`_NavGraph.kt`:** Es el núcleo del enrutamiento. Contiene el contenedor `NavHost` encargado de interceptar las transiciones del usuario, inyectar los ViewModels correspondientes a cada pantalla mediante Hilt y resolver los lambdas de navegación de forma desacoplada.
* **`_CommonFuncitons.kt`:** Alberga componentes visuales reutilizables por toda la aplicación, aportando una consistencia de diseño uniforme y reduciendo la duplicidad de código.

### 📱 2. Desglose de Pantallas (Screens)

#### 🔐 Flujo de Acceso y Gestión de Usuarios
* **`LoginScreen.kt`:** Formulario de acceso directo que solicita correo electrónico y contraseña. Comunica estados visuales de carga y errores mediante una barra de estado o diálogos en caso de credenciales incorrectas.
* **`SignInScreen.kt`:** Pantalla de registro integral. Incluye campos avanzados con validación inmediata en tiempo de escritura. Recoge información compleja como la fecha de nacimiento (a través de un selector de fecha personalizado) y una dirección postal completa estructurada en campos individuales, la cual se procesa como un modelo embebido.
* **`ForgottenPasswordScreen.kt`:** Interfaz limpia dedicada a la recuperación de cuentas, permitiendo introducir el correo electrónico para disparar la solicitud de reestablecimiento de contraseña.
* **`UserInfoScreen.kt`:** Sección de perfil donde el usuario autenticado puede visualizar sus datos personales y realizar modificaciones sobre su configuración, sincronizando los cambios con la base de datos local de Room.

#### 🗺️ Flujo Principal y Gestión de Viajes
* **`MainPageScreen.kt`:** El panel de control principal de la app. Utiliza una arquitectura de **Doble Menú Lateral (`ModalNavigationDrawer`)**: un lateral izquierdo dedicado a opciones globales (Preferencias, Términos y Sobre Nosotros) y un lateral derecho para opciones exclusivas de la cuenta del usuario. Cuenta con un diseño minimalista que incluye una `BottomAppBar` optimizada con un botón centralizado (`Icons.Default.DateRange`) para dirigir de forma rápida y cómoda al usuario hacia sus itinerarios.
* **`TripListScreen.kt`:** El núcleo operativo de la aplicación. Muestra de forma reactiva la colección de viajes del usuario. Desde aquí se gestionan las actividades (`ItineraryItem`) asociadas a cada viaje, permitiendo al usuario añadir, editar o eliminar eventos cotidianos (visitas, comidas, transportes) mientras la pantalla actualiza en tiempo real los costes totales calculados por el ViewModel.

#### 📄 Pantallas de Soporte e Información
* **`PreferencesScreen.kt`:** Espacio destinado a las preferencias del sistema o personalización de la experiencia del usuario dentro de la app.
* **`TermsAndConditionsScreen.kt`:** Vista de lectura dedicada a los aspectos legales, términos de servicio y políticas de privacidad de la plataforma.
* **`AboutUsScreen.kt`:** Pantalla informativa que detalla el propósito de la aplicación **Go And Be** y la información sobre sus desarrolladores.

## 🧠 Capa de Arquitectura y Lógica de Negocio (ViewModels)

La aplicación utiliza la arquitectura oficial **MVVM (Model-View-ViewModel)**. Los ViewModels se encargan de gestionar el estado de la interfaz de usuario de manera segura ante cambios de configuración (como la rotación de pantalla) y de procesar las interacciones del usuario, aislando por completo las vistas (`Screens`) de la lógica de datos.

Se implementa el patrón **UDF (Flujo de Datos Unidireccional)**, donde la UI solo emite eventos hacia el ViewModel y este expone un único estado inmutable a través de un `StateFlow`.

### 🔐 1. AuthViewModel
Este componente centraliza el estado de autenticación y la gestión del perfil del usuario actual. Inyecta `IAuthRepository` mediante Hilt.

* **Patrón de Estado Unificado (`UserUiState`):** En lugar de manejar múltiples variables sueltas, encapsula todo el estado de la pantalla en un único DTO reactivo `UserUiState`:
  ```kotlin
  data class UserUiState(
      val user: User? = null,
      val isLoading: Boolean = true,
      val isSaving: Boolean = false,
      val errorMessage: String? = null,
      val isAuthenticated: Boolean = false,
      val isPasswordResetSent: Boolean = false
  )

## 🗄️ Capa de Repositorios (Patrón Repository)

La aplicación implementa el **Patrón Repository** para abstraer los orígenes de datos (Room y Firebase) del resto de la aplicación. Los ViewModels nunca acceden directamente a las bases de datos ni al cliente de autenticación; en su lugar, delegan en esta capa, que actúa como la **única fuente de verdad** (*Single Source of Truth*).

### 👥 1. AuthRepository
Este repositorio gestiona de forma unificada la identidad remota del usuario y su perfil local de Room. Implementa la interfaz `IAuthRepository` e inyecta `UserDao` para acceder a Room junto a `FirebaseAuth` para acceder a googleFirebase.

* **Gestión de Sesión Reactiva (`getLogState`):** Utiliza un `callbackFlow` para escuchar en tiempo real los cambios de estado de Firebase Auth (Login/Logout), emitiendo el identificador único (`uid`) o `null` como un flujo reactivo (`Flow<String?>`).
* **Sincronización Transaccional Híbrida (`signUp`):** *Esta es una de las lógicas más robustas del proyecto.* Al registrar un nuevo usuario, el repositorio ejecuta una operación en dos pasos con sistema de seguridad integrado (*Rollback*):
    1. Intenta crear la cuenta en la nube mediante Firebase.
    2. Si tiene éxito, genera la entidad `User` (convirtiendo el DTO `UserRegistrationRequest` mediante una función de extensión) e intenta persistirla localmente en Room.
    3. **Mecanismo de Rollback:** Si Room falla al insertar el registro (devuelve `-1L` o lanza una excepción por falta de almacenamiento), el repositorio intercepta el error y activa de inmediato `deleteUserFirebase()` para eliminar la cuenta huérfana de la nube. Esto garantiza que jamás existan inconsistencias de datos entre Firebase y la base de datos local.
* **Operaciones Locales y Recuperación:** Proporciona métodos suspendidos para el CRUD completo del usuario en Room, inicio de sesión síncrono y la solicitud remota para el restablecimiento de contraseña vía email (`sendPasswordResetEmail`).

### ✈️ 2. TripRepository
Este repositorio maneja exclusivamente el dominio de los viajes y sus respectivos itinerarios locales, sirviendo de puente directo con `TripDao` e implementando la interfaz `ITripRepository`.

* **Flujos de Datos Complejos (`getTripsForUser`):** Expone un flujo continuo (`Flow<List<TripWithItinerary>>`) de los viajes asignados a un usuario. Room notifica automáticamente al flujo en cuanto ocurre cualquier cambio en las tablas, permitiendo una interfaz reactiva sin peticiones manuales de refresco.
* **Operaciones CRUD Agrupadas:** Centraliza tanto la gestión de los viajes (`Trip`) como de sus elementos secundarios:
    * **Viajes:** Permite añadir (`addTrip`), editar (`editTrip`), eliminar (`deleteTrip`) u obtener detalles por ID.
    * **Actividades (`ItineraryItem`):** Proporciona los accesos directos para la inserción, actualización y eliminación de actividades vinculadas, permitiendo al ViewModel coordinar los cálculos de costes de los itinerarios de manera centralizada.


## 💾 Gestión de Datos y Autenticación (¿Dónde se guardan las cosas?)

El sistema de almacenamiento y seguridad de la aplicación está estructurado en dos capas independientes: una capa remota encargada exclusivamente de la identidad del usuario, y una capa de base de datos local para la persistencia del contenido operativo y de negocio de forma rápida y con soporte offline completo.

### 🔐 1. Autenticación Remota (Firebase Auth)
Para garantizar un acceso seguro y un control de accesos estandarizado, la aplicación delega la identidad en **Firebase Authentication**.
* **Método de Acceso:** Se utiliza de forma exclusiva el proveedor de **Correo Electrónico y Contraseña**. Es un sistema de inicio de sesión limpio y directo.
* **Recuperación de Credenciales:** El sistema cuenta con soporte para la **recuperación de contraseña**. Los usuarios pueden solicitar directamente desde la interfaz el envío de un correo electrónico automatizado para reestablecer sus credenciales de forma segura a través de Firebase.

### 🏠 2. Persistencia Local (Room Database)
A excepción de las credenciales de acceso, **todos los datos de la aplicación se guardan localmente en el dispositivo** utilizando la librería oficial **Room** sobre SQLite. Esto garantiza una velocidad de carga instantánea y permite el uso de la aplicación sin conexión a internet.

El diseño de la base de datos local cubre las siguientes funciones:
* **CRUD Completo de Usuarios:** El sistema permite **Crear** (SignIn), **Leer** (Login/Perfil), **Actualizar** (Ajustes de usuario) y **Eliminar** cuentas locales de forma completa.
* **Información Personalizada del Usuario:** Cada perfil almacena datos específicos (UUID de Firebase, email, nombre de usuario, fecha de nacimiento, teléfono, preferencias de correo) utilizando técnicas avanzadas de Room como `@Embedded` para estructurar objetos complejos (por ejemplo, la clase `Address`).
* **Estructura Relacional (Viajes Asignados):** Los datos están completamente vinculados de forma relacional mediante llaves foráneas (`ForeignKeys`). Cada viaje creado está estrictamente asignado al `userId` del usuario en sesión, garantizando que al cambiar de cuenta o iniciar sesión, el usuario vea única y exclusivamente su lista de viajes personalizados junto con sus respectivos itinerarios y actividades.

## 🧪 Pruebas Unitarias y Verificación Visual (Previews)

El proyecto prioriza la estabilidad de la lógica de negocio y la agilidad en el desarrollo de la interfaz de usuario mediante dos pilares fundamentales: los tests automatizados y las vistas previas de Jetpack Compose.

### 🧩 1. Pruebas Unitarias (En Proceso)
La suite de pruebas unitarias se encuentra actualmente en fase de expansión activa (**en proceso**). El objetivo principal es blindar el comportamiento del sistema y asegurar que los componentes clave respondan exactamente como se espera ante cualquier escenario.

Actualmente, el proyecto cuenta con cobertura de pruebas unitarias en sus componentes más críticos:
* **`AuthViewModelTest`:** Verifica el correcto funcionamiento del flujo unidireccional de datos (UDF). Asegura el disparo inmediato de errores ante validaciones locales (campos vacíos, formatos de dirección) y la consistencia en los estados de carga.
* **`TripListViewModelTest`:** Comprueba la reactividad de los flujos asíncronos mediante las herramientas de `kotlinx-coroutines-test` (`runTest`, `backgroundScope`). Valida con precisión matemática los cálculos de costes acumulados de los viajes al añadir, editar o eliminar actividades.

> 💡 **Arquitectura Testeable:** Gracias a la eliminación completa de dependencias rígidas de Firebase dentro de la lógica del ViewModel, los tests se ejecutan de forma nativa en la JVM en cuestión de milisegundos, utilizando **MockK** para falsear de forma limpia el comportamiento de la capa de datos.

### 📱 2. Vistas Previas en Tiempo Real (Compose Previews)
Para garantizar un desarrollo ágil y una interfaz de usuario pulida sin necesidad de compilar la aplicación en un emulador o dispositivo físico en cada cambio, **todas las pantallas (`Screens`) del proyecto implementan funciones `@Preview`**.

* **Renderizado de Estados:** Las previews están diseñadas para simular los diferentes estados de la UI (por ejemplo: vista en estado de carga, pantallas con datos simulados utilizando objetos de prueba o flujos de error), permitiendo validar el comportamiento visual al instante.
* **Consistencia de Diseño:** Facilita el ajuste rápido de márgenes, tipografías y componentes de Material 3, asegurando que la interfaz sea consistente y responsiva antes de integrarse en el grafo de navegación real.

## 🛠️ Stack Tecnológico y Gestión de Dependencias

Este proyecto está construido bajo los estándares más modernos del ecosistema nativo de Android, garantizando un rendimiento óptimo, código mantenible y una arquitectura escalable.

### 📦 Tecnologías Principales
* **Kotlin:** Lenguaje de programación principal, aprovechando al máximo su expresividad, seguridad frente a nulos (`Null Safety`) y funciones de extensión.
* **Jetpack Compose & Material 3:** Motor gráfico moderno para la creación de interfaces de usuario 100% declarativas y reactivas.
* **Hilt (Dagger):** Framework oficial para la inyección de dependencias, reduciendo el acoplamiento y facilitando las pruebas unitarias.
* **Room Database:** Capa de abstracción sobre SQLite para la persistencia de datos local de forma fluida y segura.
* **Firebase Authentication:** Servicio en la nube para la gestión segura de identidades (registro, login y recuperación).
* **Corrutinas y Flow (StateFlow/SharedFlow):** Gestión nativa de la asincronía y pipelines de datos reactivos en tiempo real.
* **MockK & RunTest:** Herramientas avanzadas para pruebas unitarias de ViewModels y Repositorios sin dependencias de la plataforma Android.

---

### ⚙️ Control Centralizado de Versiones (`libs.versions.toml`)

El proyecto implementa **Gradle Version Catalogs** para la administración de todo su ecosistema de librerías y plugins.

> 💡 **Mantenimiento Ágil:** Todas las dependencias, SDKs y plugins de la aplicación se gestionan exclusivamente desde el archivo **`libs.versions.toml`**. Esto actúa como una *única fuente de verdad*, permitiendo que cualquier actualización de versión o adición de una nueva librería se propague por todo el proyecto de forma global, rápida y segura modificando una sola línea de código.