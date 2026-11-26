# Arquitectura y decisiones de diseño

| Responsabilidad | Clases y comportamiento |
|---|---|
| Presentación | JavaFX recoge los datos del formulario y muestra la tabla y los gráficos. La consola presenta un menú interactivo. Ambas entradas llaman a las operaciones de gestión. |
| Controlador | `GestionGastos` coordina el alta, la edición, el borrado, las consultas y la configuración de alertas. |
| Dominio | `Gasto` valida el importe, la fecha y la categoría. `FiltroGastos` aplica los criterios de consulta. `Alerta` contiene el límite y el ámbito. `EvaluadorAlertas` calcula el gasto acumulado en las fechas que determina la estrategia de la alerta. |
| Persistencia | Los contratos `RepositorioGastos`, `RepositorioCategorias` y `RepositorioAlertas` exponen operaciones con objetos de dominio. Sus implementaciones SQLite ejecutan SQL mediante JDBC. `BaseDatos` prepara el esquema y las conexiones. |

## Registro e identidad

`GestionGastos.registrarGasto` comprueba que la categoría esté registrada, construye el gasto y llama a `RepositorioGastos.agregar`. El gasto y la categoría tienen identificadores UUID que se conservan al leerlos de SQLite. La igualdad de categoría depende del identificador. Así, la categoría seleccionada en un filtro coincide con la que se recupera en una consulta posterior aunque sean dos objetos Java distintos.

## Consultas y gráficos

Los meses seleccionados se combinan entre sí y las categorías seleccionadas también. Para entrar en el resultado, el gasto debe cumplir simultáneamente el criterio de mes, el de categoría y el intervalo de fechas. Los extremos indicados son inclusivos. La tabla y las gráficas representan los gastos de la consulta. El importe acumulado por categoría se calcula con `BigDecimal`.

## Alertas y transacciones

Al registrar un gasto, `GestionGastos` guarda el apunte y entrega a `EvaluadorAlertas` los gastos registrados y cada alerta configurada. El evaluador devuelve una notificación cuando el total pasa de estar dentro del límite a superarlo. Al editar, el controlador recupera el gasto anterior para que el evaluador tenga en cuenta los cambios de importe, fecha o categoría.

`GestorTransacciones` agrupa la escritura del gasto y la de sus avisos. En SQLite, los repositorios utilizan la misma conexión durante la operación. Si falla una escritura, se revierte la transacción. En `importarGastos`, la transacción comprende toda la lista de gastos y los avisos que se generan al incorporarlos.

## Configuración de las interfaces

JavaFX registra una instancia de `ConfiguracionSQLite` al arrancar. Los controladores de las vistas obtienen el servicio mediante `Configuracion.getInstancia().getGestionGastos()`. La consola obtiene el suyo de `ConfiguracionSQLite`. Ambas interfaces abren el mismo fichero en la carpeta personal del usuario, incluso cuando se ejecutan desde directorios distintos.

Las pruebas pueden utilizar `ConfiguracionMemoria`, que proporciona a `GestionGastos` los repositorios en memoria. Las reglas de filtrado y evaluación de alertas son las mismas en ambas configuraciones.
