# Patrones de diseño · Gestión de Gastos

El enunciado fija los patrones que debemos aplicar. Los gastos y las alertas utilizan repositorios JDBC. La periodicidad de cada alerta determina la estrategia de cálculo del periodo.

## Estrategia · Evaluación de alertas

Una alerta combina un límite, una periodicidad y, opcionalmente, una categoría. La alerta almacena un valor de `PeriodoAlerta`. Durante la evaluación, `AlertasSQLite` invoca los métodos `inicio(fecha)` y `fin(fecha)` de esa estrategia. Las constantes SEMANAL y MENSUAL implementan esos métodos de forma distinta. La primera selecciona de lunes a domingo y la segunda, del primer al último día del mes. Con ese periodo, la lógica común filtra por categoría si procede, suma los importes y comprueba si el total supera el límite. Así evitamos definir cuatro clases distintas para todas las combinaciones de periodicidad y categoría.

Por ejemplo, con un límite mensual de 100 € en videojuegos y 95 € acumulados, un gasto de 5 € deja el total justo en el límite. Otro euro en la misma categoría y mes lo eleva a 101 € y da lugar a una notificación. La misma comparación sirve para una alerta semanal: lo que cambia al sustituir la estrategia es el conjunto de fechas evaluado.

## Adaptador · Diseño de la lectura de movimientos externos

El importador necesita recibir movimientos con datos que la aplicación entienda. Cada formato bancario puede representar de forma distinta la fecha y el importe o colocar esos campos en posiciones diferentes. Proponemos una interfaz común de lectura y un adaptador por formato. Cada adaptador utiliza el lector correspondiente y transforma su salida en movimientos que la capa de aplicación puede procesar.

La consecuencia se aprecia al incorporar un segundo formato. Si dos ficheros representan el mismo movimiento, sus adaptadores deben producir el mismo importe y la misma fecha. El código que incorpora los gastos puede procesar ambos resultados de la misma manera.

## Método Factoría · Diseño de la creación del adaptador

El proceso de importación sigue los mismos pasos: obtener el adaptador, leer el fichero e incorporar los movimientos. Situamos en el importador base un **método factoría** que devuelve un adaptador a través de la interfaz común. Cada importador concreto redefine ese método para construir el adaptador de su formato. Después, el proceso compartido invoca la lectura sin depender de la clase concreta creada.

Los dos patrones colaboran en momentos distintos del mismo recorrido. El Método Factoría resuelve **qué objeto se crea** para el formato seleccionado. El Adaptador resuelve **cómo se interpretan los datos** una vez creado ese objeto.

## Repositorio · Persistencia mediante JDBC

En el [registro de un gasto personal](diagrama-interaccion-hu01.md), la capa de aplicación valida la categoría y solicita al repositorio que guarde el gasto. El repositorio ofrece operaciones expresadas en términos del dominio, como guardar o recuperar gastos. Su implementación JDBC se ocupa de ejecutar las consultas y reconstruir los objetos a partir de las filas recuperadas.

Esta separación permite que el formulario y la terminal utilicen la misma operación de registro. Si la escritura falla, la capa de aplicación recibe el fallo y la interfaz puede comunicárselo al usuario. El criterio observable es que un alta confirmada pueda consultarse después de cerrar y volver a abrir el programa.

## Acceso compartido a la configuración

Durante el arranque, JavaFX registra una instancia de `ConfiguracionSQLite` mediante `Configuracion.setInstancia`. Los controladores acceden a ella con `Configuracion.getInstancia()`. La configuración mantiene una referencia al mismo servicio de gestión durante esa ejecución. El registro admite una nueva asignación y `GestionGastos` tiene constructores públicos para crear servicios independientes, como los que usan la consola y las pruebas.

El patrón Singleton exige controlar la creación para garantizar una única instancia. En esta versión, la configuración proporciona el punto de acceso global y la unicidad depende del código de arranque.
