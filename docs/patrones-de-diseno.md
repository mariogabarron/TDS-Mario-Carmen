# Patrones de diseño · Gestión de Gastos

## Estrategia · Evaluación de alertas

Cada `Alerta` tiene una estrategia de tipo `EstrategiaPeriodo`. Al crear la alerta, `PeriodoAlerta` proporciona la estrategia correspondiente a la periodicidad elegida. `PeriodoSemanal` toma la semana de lunes a domingo y `PeriodoMensual` toma el mes natural de la fecha del gasto.

`EvaluadorAlertas` pide a la estrategia las fechas de inicio y fin. Suma los gastos comprendidos entre ambas fechas y, si la alerta tiene una categoría, cuenta solo los de esa categoría. Con un límite mensual de 100 € en videojuegos, pasar de 95 € a 100 € deja el gasto dentro del presupuesto. Al pasar de 100 € a 101 € se genera un aviso. Mientras el total siga por encima del límite, los gastos siguientes no generan nuevos avisos por esa alerta en ese periodo.

## Repositorio · Gastos, categorías y alertas

`GestionGastos` utiliza los contratos `RepositorioGastos`, `RepositorioCategorias` y `RepositorioAlertas`. En el [registro de un gasto](diagrama-interaccion-hu01.md), el controlador solicita el alta al repositorio de gastos. Si el evaluador devuelve una notificación, el controlador encarga su escritura al repositorio de alertas dentro de la misma transacción.

Las implementaciones JDBC convierten las filas de SQLite en objetos del dominio. La configuración en memoria ofrece los mismos contratos y permite probar el controlador con datos independientes del fichero del usuario.

## Singleton · Gestión de ventanas

`SceneManager` centraliza la apertura de ventanas JavaFX. Su constructor es privado y `getInstancia()` da acceso a la misma instancia desde las vistas.

La configuración se establece en el arranque con `Configuracion.setInstancia`, que admite una sola asignación. Los controladores JavaFX consultan esa configuración para acceder al mismo servicio de gestión.
