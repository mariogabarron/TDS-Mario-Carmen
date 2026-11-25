# Arquitectura y decisiones de diseño

| Responsabilidad | Clases y comportamiento |
|---|---|
| Presentación | JavaFX recoge los datos del formulario y muestra la tabla y los gráficos. La consola presenta un menú interactivo. Ambas entradas llaman a las operaciones de gestión. |
| Aplicación | `GestionGastos` coordina el alta, la edición, el borrado, las consultas y la configuración de alertas. |
| Dominio | `Gasto` valida el importe, la fecha y la categoría. `FiltroGastos` aplica los criterios de consulta. `Alerta` contiene el límite y el ámbito. `PeriodoAlerta` determina las fechas de evaluación. |
| Persistencia | Los contratos `RepositorioGastos`, `RepositorioCategorias` y `RepositorioAlertas` exponen operaciones con objetos de dominio. Sus implementaciones SQLite ejecutan SQL mediante JDBC. `BaseDatos` prepara el esquema y las conexiones. |

## Registro e identidad

`GestionGastos.registrarGasto` comprueba que la categoría esté registrada, construye el gasto y llama a `RepositorioGastos.agregar`. El gasto y la categoría tienen identificadores UUID que se conservan al leerlos de SQLite. La igualdad de categoría depende del identificador. Así, la categoría seleccionada en un filtro coincide con la que se recupera en una consulta posterior aunque sean dos objetos Java distintos.

## Consultas y gráficos

Los meses seleccionados se combinan entre sí y las categorías seleccionadas también. Para entrar en el resultado, el gasto debe cumplir simultáneamente el criterio de mes, el de categoría y el intervalo de fechas. Los extremos indicados son inclusivos. La tabla y las gráficas representan los gastos de la consulta. El importe acumulado por categoría se calcula con `BigDecimal`.

## Alertas y transacciones

El registro o la edición de un gasto y las notificaciones que genera se confirman en la misma transacción. Después de escribir el gasto, `GastosSQLite` llama a `AlertasSQLite.evaluar` con la misma conexión. Al editar, se recuperan primero los datos anteriores para determinar si se acaba de cruzar el límite. La notificación conserva las fechas del periodo, la categoría o ámbito general, el límite y el total alcanzado.

`GestionGastos.importarGastos` admite una lista de gastos ya interpretados. La implementación SQLite procesa el lote en una transacción y revierte tanto los gastos como sus avisos si falla una escritura. La lectura de formatos bancarios corresponde a la HU-16.

## Configuración de las interfaces

JavaFX registra la configuración de la ejecución y sus controladores obtienen el servicio de gestión mediante `Configuracion.getInstancia().getGestionGastos()`. La consola crea su propio servicio. Las dos interfaces resuelven la ubicación de la base a partir de la carpeta personal del usuario, por lo que pueden recuperar los mismos datos aunque arranquen desde directorios distintos.
