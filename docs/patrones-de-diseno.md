# Patrones de diseño · Gestión de Gastos

En este apartado explicamos cómo encajan en el diseño de Gestión de Gastos, qué responsabilidad asignamos a cada participante y cómo se puede comprobar el resultado.

## Estrategia · Evaluación de alertas

Una alerta combina un límite, una periodicidad y, opcionalmente, una categoría. En nuestro diseño, la alerta delega en una estrategia la determinación del periodo que corresponde a la fecha evaluada. La estrategia semanal selecciona la semana y la mensual selecciona el mes. Con ese periodo, la lógica común filtra por categoría si procede, suma los importes y comprueba si el total supera el límite. Así evitamos definir cuatro clases distintas para todas las combinaciones de periodicidad y categoría.

Por ejemplo, con un límite mensual de 100 € en videojuegos y 95 € acumulados, un gasto de 5 € deja el total justo en el límite. Otro euro en la misma categoría y mes lo eleva a 101 € y da lugar a una notificación. La misma comparación sirve para una alerta semanal: lo que cambia al sustituir la estrategia es el conjunto de fechas evaluado.

## Adaptador · Lectura de movimientos externos

El importador necesita recibir movimientos con datos que la aplicación entienda. Cada formato bancario puede representar de forma distinta la fecha y el importe o colocar esos campos en posiciones diferentes. Proponemos una interfaz común de lectura y un adaptador por formato. Cada adaptador utiliza el lector correspondiente y transforma su salida en movimientos que la capa de aplicación puede procesar.

La consecuencia se aprecia al incorporar un segundo formato. Si dos ficheros representan el mismo movimiento, sus adaptadores deben producir el mismo importe y la misma fecha. El código que incorpora los gastos puede procesar ambos resultados de la misma manera.

## Método Factoría · Creación del adaptador

El proceso de importación sigue los mismos pasos: obtener el adaptador, leer el fichero e incorporar los movimientos. Situamos en el importador base un **método factoría** que devuelve un adaptador a través de la interfaz común. Cada importador concreto redefine ese método para construir el adaptador de su formato. Después, el proceso compartido invoca la lectura sin depender de la clase concreta creada.

Los dos patrones colaboran en momentos distintos del mismo recorrido. El Método Factoría resuelve **qué objeto se crea** para el formato seleccionado. El Adaptador resuelve **cómo se interpretan los datos** una vez creado ese objeto.

## Repositorio · Persistencia mediante JDBC

En el [registro de un gasto personal](diagrama-interaccion-hu01.md), la capa de aplicación valida la categoría y solicita al repositorio que guarde el gasto. El repositorio ofrece operaciones expresadas en términos del dominio, como guardar o recuperar gastos. Su implementación JDBC se ocupa de ejecutar las consultas y reconstruir los objetos a partir de las filas recuperadas. Esta separación permite que el formulario y la terminal utilicen la misma operación de registro. Si la escritura falla, la capa de aplicación recibe el fallo y la interfaz puede comunicárselo al usuario. El criterio observable es que un alta confirmada pueda consultarse después de cerrar y volver a abrir el programa.

## Singleton · Acceso a una instancia compartida

El enunciado exige Singleton cuando una clase necesita una única instancia de acceso global. Para ese papel proponemos el servicio de aplicación `GestionGastos`, que coordina el registro desde las distintas entradas. El diseño prevé restringir su construcción y ofrecer un punto de acceso que devuelva siempre la misma instancia dentro de una ejecución. Puede comprobarse solicitándola dos veces y verificando que ambas referencias identifican el mismo objeto.

El alcance de esta decisión importa en nuestra aplicación. Si la interfaz gráfica y la terminal arrancan en procesos distintos, cada proceso tiene su propia instancia de gestión. Lo que permite recuperar los gastos desde ambas ejecuciones es el repositorio persistente.
