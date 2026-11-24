# Arquitectura de la aplicación y decisiones de diseño

Gestión de Gastos es una aplicación de escritorio que permite trabajar desde una ventana JavaFX y desde la línea de comandos. Para organizarla, partimos de una operación que se puede hacer por ambas vías: registrar un gasto. Cambia la forma de introducir el importe, la fecha y la categoría, pero las reglas que debe cumplir el gasto son las mismas.

## Cómo se reparten las responsabilidades

| Parte | Responsabilidad en el diseño |
| --- | --- |
| Presentación | El formulario JavaFX recoge los datos y muestra el resultado. En la terminal, esa misma tarea consiste en interpretar los argumentos del comando y comunicar el resultado por texto. |
| Aplicación | `GestionGastos` coordina la operación. Para registrar un gasto, comprueba que la categoría esté registrada, crea el gasto y solicita su guardado. Ambas interfaces pueden llamar a la misma operación. |
| Dominio | `Gasto` reúne el importe, la fecha y la categoría, y valida sus propios datos. `FiltroGastos` determina si un gasto cumple los criterios de una consulta. |
| Persistencia | El repositorio se encarga de guardar y recuperar los datos mediante JDBC. Así, el código que registra el gasto puede pedir que se guarde sin conocer las tablas ni escribir SQL. |

El flujo documentado inicialmente guarda los gastos en memoria. El [diagrama de interacción de la HU-01](diagrama-interaccion-hu01.md) recoge el diseño previsto con persistencia: la interfaz muestra el alta como completada cuando el repositorio confirma la escritura. Si falla el guardado, el usuario puede saber que ese apunte sigue pendiente.

## Decisiones de diseño

**Compartir el registro entre las dos interfaces.** Situamos la operación en la capa de aplicación para que tanto el formulario como la terminal deleguen en ella. Si una categoría deja de ser válida, ambas entradas deben rechazar el mismo gasto. Podemos comprobarlo invocando la operación sin abrir una ventana.

**Delimitar cuándo se confirma el alta.** En el diseño con persistencia, la operación de registro depende de un repositorio con una operación de guardado. La implementación JDBC gestiona la escritura y comunica cualquier fallo a la capa de aplicación. Mostrar el gasto como confirmado solo después del guardado evita que el usuario vea un apunte que desaparecería al reiniciar.

**Repartir las validaciones según quién conoce los datos.** Para saber si una categoría está registrada hace falta consultar las categorías disponibles, de ahí que la comprobación corresponda a `GestionGastos`. En cambio, `Gasto` dispone de sus propios datos para validarlos tanto al crearse como al modificarse. Así evitamos mantener una versión de las reglas para el alta y otra para la edición.

**Dar el mismo significado a los filtros en todas las vistas.** Concentramos los criterios en `FiltroGastos`. Por ejemplo, al seleccionar «julio», «agosto» y «Transporte», un gasto entra si pertenece a cualquiera de esos dos meses y, además, es de transporte. El diseño prevé usar esa misma selección para la tabla y para el gráfico. De este modo, el total representado se puede contrastar con los apuntes visibles.

**Distinguir una alerta del aviso que genera.** En el modelo, una alerta describe una regla, como superar 100 € al mes en videojuegos. Una notificación recoge una ocasión concreta en que se superó el límite. Esta distinción permite consultar en el historial qué ocurrió en un mes anterior aunque el usuario haya cambiado después su presupuesto.

El [modelo de dominio](diagramas-gestion-gastos.md) recoge las multiplicidades y restricciones que aplican estas decisiones. Los nombres de las clases de persistencia y su conexión definitiva deberán contrastarse con la versión entregada del código.
