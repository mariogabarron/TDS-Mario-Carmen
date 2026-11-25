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


## Estado del proyecto

- [x] Registro, modificación y eliminación de gastos.
- [x] Gestión de categorías.
- [x] Consulta y filtrado combinado de gastos.
- [x] Representaciones gráficas.
- [x] Interfaz gráfica y consola interactiva.
- [x] Arquitectura MVC y configuración centralizada.
- [x] Persistencia mediante JDBC y patrón Repositorio.
- [x] Alertas y notificaciones.
- [ ] Cuentas compartidas y reparto de gastos.
- [ ] Importación de gastos desde ficheros.
- [ ] Implementación de los patrones de diseño restantes.
- [ ] Manual de usuario.
