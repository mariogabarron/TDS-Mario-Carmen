# Historias de usuario · Gestión de Gastos

Tecnologías de Desarrollo de Software · Universidad de Murcia · Curso 2026/2027

## Gastos personales y categorías

### HU-01 — Registrar un gasto personal

Como usuario de la aplicación, quiero registrar un gasto indicando el importe, la fecha y la categoría para llevar un control de mis gastos.

**Criterios de aceptación**

- Dado que existe la categoría «Transporte», cuando registro un gasto de 15 € con fecha 24 de noviembre de 2026 en esa categoría, entonces el gasto queda guardado y aparece en la lista con el importe, la fecha y la categoría indicados.
- Dado un gasto ya guardado, cuando cierro y vuelvo a abrir la aplicación, entonces puedo seguir consultándolo.

### HU-02 — Editar un gasto personal

Como usuario de la aplicación, quiero modificar un gasto registrado para corregir sus datos.

**Criterios de aceptación**

- Dado un gasto de 15 € del 24 de noviembre en «Transporte», cuando cambio el importe a 18 €, la fecha al 25 de noviembre y la categoría a «Alimentación», entonces la lista muestra el mismo gasto con los datos actualizados.
- Dado un gasto modificado, cuando reinicio la aplicación, entonces se conservan los cambios.

### HU-03 — Borrar un gasto personal

Como usuario de la aplicación, quiero borrar un gasto registrado para retirar un apunte que no corresponde.

**Criterios de aceptación**

- Dada una lista con varios gastos, cuando borro uno de ellos, entonces la lista sigue mostrando los demás y el gasto eliminado deja de aparecer.
- Dado que he borrado el gasto, cuando reinicio la aplicación, entonces continúa eliminado.

### HU-04 — Gestionar gastos desde la línea de comandos

Como usuario de la aplicación, quiero registrar, modificar y borrar gastos desde la línea de comandos para gestionar mis apuntes desde la terminal.

**Criterios de aceptación**

- Dada una categoría existente, cuando registro desde la terminal un gasto con importe, fecha y categoría, entonces el gasto queda guardado y puedo consultarlo en la lista de la interfaz gráfica.
- Dado un gasto registrado, cuando lo modifico desde la terminal, entonces la lista muestra los datos actualizados.
- Dado un gasto registrado, cuando lo borro desde la terminal, entonces deja de aparecer en la lista de la interfaz gráfica.

### HU-05 — Usar y crear categorías

Como usuario de la aplicación, quiero elegir entre categorías predefinidas y crear categorías propias para clasificar mis gastos.

**Criterios de aceptación**

- Dada una instalación inicial, cuando consulto las categorías disponibles al registrar un gasto, entonces puedo elegir categorías predefinidas como «Alimentación», «Transporte» y «Entretenimiento».
- Dada una nueva categoría llamada «Material de estudio», cuando la creo y registro un gasto, entonces puedo seleccionar esa categoría. La categoría sigue disponible tras reiniciar la aplicación.

## Consulta, filtros y gráficos

### HU-06 — Consultar los gastos en una lista o tabla

Como usuario de la aplicación, quiero consultar mis gastos en una lista o tabla para revisar cada apunte.

**Criterio de aceptación**

- Dados varios gastos guardados, cuando abro la consulta sin filtros, entonces puedo ver todos los gastos registrados con su importe, su fecha y su categoría.

### HU-07 — Filtrar los gastos

Como usuario de la aplicación, quiero filtrar los gastos por meses, por un intervalo de fechas, por categorías o combinando filtros para revisar los apuntes que me interesan.

**Criterios de aceptación**

- Dados gastos en junio, julio y agosto, cuando selecciono julio y agosto en el filtro por meses, entonces aparecen los gastos de cualquiera de esos dos meses.
- Dados gastos del 5, 15 y 25 de julio, cuando filtro del 10 al 20 de julio, entonces aparece el gasto del día 15.
- Dados gastos de alimentación, transporte y entretenimiento, cuando selecciono alimentación y transporte en el filtro por categorías, entonces aparecen los gastos de cualquiera de esas dos categorías.
- Dados gastos de distintas categorías y fechas, cuando selecciono julio y agosto, la categoría «Transporte» y el intervalo del 10 de julio al 20 de agosto, entonces aparecen únicamente los gastos de transporte que cumplen tanto el filtro de meses como el intervalo.

### HU-08 — Ver la distribución de los gastos por categoría

Como usuario de la aplicación, quiero ver un gráfico de los gastos por categoría para identificar en qué gasto más.

**Criterios de aceptación**

- Dados dos gastos de alimentación de 25 € y 15 € y un gasto de transporte de 10 €, cuando consulto el gráfico de barras o circular, entonces los valores representados corresponden a 40 € en alimentación y 10 € en transporte.
- Dados esos mismos gastos, cuando filtro por «Transporte», entonces el gráfico representa únicamente los 10 € de transporte.

## Alertas y notificaciones

### HU-09 — Configurar un límite de gasto

Como usuario de la aplicación, quiero configurar alertas semanales o mensuales, generales o asociadas a una categoría, para controlar mi presupuesto.

**Criterios de aceptación**

- Cuando configuro una alerta semanal general de 500 €, entonces la alerta queda guardada con ese límite y se aplica al total de gastos de la semana.
- Dada la categoría «Videojuegos», cuando configuro una alerta mensual de 100 € para esa categoría, entonces la alerta queda guardada con ese límite y se aplica solo al gasto mensual de «Videojuegos».

### HU-10 — Recibir una notificación al superar un límite

Como usuario de la aplicación, quiero que se genere una notificación al superar un límite configurado para saber cuándo me he pasado del presupuesto.

**Criterios de aceptación**

- Dada una alerta semanal general de 500 € y un acumulado semanal de 490 €, cuando registro un gasto de 20 € en esa misma semana, entonces la aplicación genera una notificación que puedo leer.
- Dada una alerta mensual de 100 € para «Videojuegos» y un acumulado de 95 € en esa categoría, cuando registro 5 € en «Videojuegos», entonces no se genera una notificación por esa alerta, porque el total queda exactamente en 100 €. Si después registro 1 € más en esa categoría y en el mismo mes, entonces se genera una notificación.
- Dada esa misma alerta mensual, un gasto de otra categoría o de otro mes queda fuera del cálculo del total de «Videojuegos» del mes que se está comprobando.

### HU-11 — Consultar el historial de notificaciones

Como usuario de la aplicación, quiero consultar notificaciones pasadas para revisar cuándo superé los límites configurados.

**Criterio de aceptación**

- Dada una notificación generada anteriormente, cuando consulto el historial, incluso después de reiniciar la aplicación, entonces puedo volver a leerla.

## Referencia para el diagrama de interacción

La historia **HU-01 — Registrar un gasto personal** corresponde al flujo «Registrar un gasto personal» del [diagrama de interacción](diagrama-interaccion-hu01.md).
