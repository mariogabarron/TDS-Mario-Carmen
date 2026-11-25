<h1 align="center">Gestión de Gastos</h1>

<p align="center">
  Tecnología de Desarrollo Software<br>
  Universidad de Murcia · Curso 2026/2027
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-333333?style=flat-square" alt="Java">
  <img src="https://img.shields.io/badge/JavaFX-21-333333?style=flat-square" alt="JavaFX">
  <img src="https://img.shields.io/badge/Maven-333333?style=flat-square" alt="Maven">
</p>

---

## Integrantes

| Nombre | Correo electrónico | Subgrupo |
| :--- | :--- | :---: |
| Mario Gabarrón Espín | m.gabarronespin@um.es | 1.3 |
| Carmen Lorente Cámara | carmen.lorentec@um.es | 1.3 |

## Descripción

Aplicación de gestión de gastos personales y compartidos,
orientada al registro, organización y análisis de gastos mediante
consultas, filtros, representaciones gráficas y alertas de consumo. Sigue una arquitectura Modelo-Vista-Controlador, separando la presentación,
la lógica de aplicación y el modelo de dominio. Se utiliza JavaFX
para la interfaz gráfica y Maven para la gestión de dependencias.

## Ejecución

**Requisitos:** JDK 21 y Maven.

### Interfaz gráfica

```bash
mvn clean javafx:run
```

### Interfaz de consola

```bash
mvn compile
java -cp target/classes es.um.tds.gastos.consola.ConsolaGastos
```

## Documentación

| Documento | Contenido |
| :--- | :--- |
| [Modelo de dominio](docs/diagramas-gestion-gastos.md) | Diagramas de clases y relaciones del dominio. |
| [Historias de usuario](docs/historias-de-usuario.md) | Requisitos funcionales y criterios de aceptación. |
| [Diagrama de interacción](docs/diagrama-interaccion-hu01.md) | Interacción entre objetos durante el registro de un gasto. |
| [Arquitectura y diseño](docs/arquitectura-y-diseno.md) | Organización del sistema y justificación de las decisiones de diseño. |
| [Patrones de diseño](docs/patrones-de-diseno.md) | Aplicación y justificación de los patrones utilizados. |
| [Manual de usuario](docs/manual-de-usuario.md) | Explicación del manejo al usuario sobre la aplicación. |


## Lo que queda por hacer

### Cierre de la implementación actual

- [ ] Mostrar una notificación inmediata al superar el límite de una alerta.
- [ ] Ampliar las pruebas unitarias de gastos, categorías, filtros y validaciones.
- [ ] Añadir pruebas de búsqueda por identificador y persistencia.
- [ ] Reforzar las pruebas de rollback y consistencia transaccional.
- [ ] Verificar el funcionamiento de JavaFX y la consola.

### Funcionalidades pendientes

- [ ] Implementar cuentas compartidas.
- [ ] Implementar reparto equitativo y porcentual mediante el patrón Estrategia.
- [ ] Implementar el cálculo y la consulta de saldos.
- [ ] Implementar la importación de ficheros mediante los patrones Adaptador y Método Factoría.
- [ ] Añadir pruebas unitarias y de integración de las nuevas funcionalidades.

### Mejoras opcionales

- [ ] Implementar una vista de calendario.
- [ ] Mejorar el diseño visual y la usabilidad de JavaFX.
- [ ] Sincronizar las categorías de los filtros ante modificaciones y eliminaciones.
- [ ] Diferenciar los errores de persistencia de los errores de actualización de la vista.

### Documentación y entrega

- [ ] Actualizar la documentación (`docs`, README, diagramas y manual de usuario).
- [ ] Ejecutar todas las pruebas y verificar el funcionamiento desde una instalación limpia.
