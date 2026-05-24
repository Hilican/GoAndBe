# Sprint 04 – Execution & Review

## 1. Resultados obtenidos

Comparación con Sprint Goal:
Se ha logrado conectar la aplicación con la API REST de hoteles para listar habitaciones y gestionar la creación, ha habido problemas con la cancelación de reservas, y mostrar el indicador visual (icono de casa/hospedaje) en la lista de viajes. Además, se implementó con éxito la persistencia en Room para reflejar el coste total y la lógica de agregar/eliminar imágenes de la galería en el almacenamiento interno. Los tests unitarios de validación del MVVM (éxito y fallo de disponibilidad) se corrigieron y pasan correctamente.

No se porque a ultima hora (literalmente), estoy teniendo problemas al hacer consultas a la API, a casi todo me esta dando error 500, no he podido grabar el video a causa de eso, no estoy seguro de si he tocado algo en algun momento o al refactorizar muchas carpetas (para poner a como se pide), me funcionaba el crear reserva, simplemente el eliminar reserva tenia que solucionarlo

---

## 2. Tareas completadas

| ID | Tarea | Completada | Comentarios                                                                                                                                                                                                   |
|----|------------|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| T1.1 | Learn/Study REST API provided | Sí | Se comprendieron los endpoints necesarios para disponibilidad y reservas.                                                                                                                                     |
| T1.2 | Implement Retrofit | Sí | Implementado junto con los mappers para transformar los DTOs de la API a modelos de dominio.                                                                                                                  |
| T1.3 | Implement a Repository to manage Retrofit | Sí | Creado e integrado bajo la nueva estructura del paquete `repo`.                                                                                                                                               |
| T1.4 | Implement MVVM using the new Repository | Sí | Se creo el repositorio para manejar las reservas usando Hilt.                                                                                                                                                 |
| T1.5 | Create Tests to check MVVM (Validation) | Sí | Funcionan, pero caldria aplicar en algunos cassos funciones en el test y en el viewmodel, ja que al cambiar como se usaban las fechas o similar en el viewmodel tengo que cambiarlo en los tests manualmente. |
| T1.6 | Create screens/Composables needed to achieve the goals | Sí | Se crearon los componentes necesarios (diálogos, tarjetas de hotel, etc.).                                                                                                                                    |
| T1.7 | Expand MVVM, screens... to fulfill all requirements | Sí | Se ajustó la principalmente tripViewModel para que muestre las reservas donde hace falta.                                                                                                                     |
| T2.1 | Implement add image button on Trips | Sí | Funciona correctamente guardando los archivos físicos en el almacenamiento interno del dispositivo.                                                                                                           |
| T2.2 | Display images of a Trip on Trip Details | Sí | Se muestran las imágenes en la sección de detalles leyendo las rutas guardadas en Room.                                                                                                                       |

---

## 3. Desviaciones

- **Reestructuración de Arquitectura:** Se tuvo que dedicar tiempo no planificado a refactorizar la estructura de carpetas (`view`, `viewmodel`, `repo`, `di`, `data`) para cumplir estrictamente con las directrices de la rúbrica de evaluación, ya que inicialmente estaban organizadas por capas de Clean Architecture pura.
- **Manejo de errores del Servidor (HTTP 500):** Durante la cancelación de reservas, la API remota devolvía errores genéricos internos.

---

## 4. Retrospectiva

### Qué funcionó bien
- **La galeria en los trips:**

### Qué no funcionó
- **Las reservas:** No se ha podido hacer que en el HotelViewModel se ejecuten correctamente la elminacion de las reservas.

---

## 5. Autoevaluación del equipo (0-10)
Nota:

[Inserta tu nota aquí, ej: 7]

Justificación:
A mi opinion he "perdido" mucho tiempo refactorizando los viewmodels, ja que antes no usaba el repositorio com debia de usarse, pero al final, sin contar el problema que he tenido con la API, diria que ha quedado bastante bien.

## Fallos que diria que penalizan, entre varios:
### Al confirmar una reserva, se vuelva a la pagina de inicio y se resete los valores de AvailableHotels, lo tenia hecho pero al hacer un git reset por los problemas de la API lo perdi
### Al buscar una reserva, deberia dejar como opcion crear un nuevo trip, y simplemente al dar a crear una reserva, en el viewModel veria que hay algun valor o algo que indique que se quiere la reserva en un nuevo trip y crear el nuevoTrip con la reserva ya adjuntada
### Al darle click hacia atras muy rapido es possible que se entre en una pantalla en blanco y no se pueda hacer mas que reiniciar la app (este si no se muy bien como lo solucionaria)