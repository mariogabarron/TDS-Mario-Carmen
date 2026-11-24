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

## Cuentas compartidas

### HU-12 — Crear una cuenta compartida con reparto equitativo

Como usuario de la aplicación, quiero crear una cuenta compartida indicando los nombres de sus integrantes para repartir los gastos a partes iguales por defecto.

**Criterios de aceptación**

- Cuando creo una cuenta compartida con Raquel, Luis y María mediante sus nombres y sin indicar porcentajes, entonces la cuenta se guarda con esas tres personas, reparto equitativo y saldo inicial de 0 € para cada una.
- Dada una cuenta compartida ya creada, su lista de integrantes permanece fija. No se puede añadir, eliminar ni sustituir a una persona de esa cuenta.

### HU-13 — Crear una cuenta compartida con porcentajes personalizados

Como usuario de la aplicación, quiero indicar qué porcentaje del gasto asume cada integrante para reflejar un reparto acordado.

**Criterios de aceptación**

- Cuando creo una cuenta con un reparto del 40 % para Raquel, el 30 % para Luis y el 30 % para María, entonces la cuenta queda guardada con esos porcentajes y saldos iniciales de 0 €.
- Cuando intento crear una cuenta con porcentajes que suman el 90 % o el 110 %, entonces la cuenta no se crea, porque el reparto debe sumar el 100 %.

### HU-14 — Registrar un gasto en una cuenta compartida

Como usuario de la aplicación, quiero registrar un gasto indicando qué integrante lo ha pagado para que se actualice lo que debe o se le debe a cada persona.

**Criterios de aceptación**

- Dada una cuenta compartida, cuando registro un gasto en ella, entonces el pago queda asociado a uno de sus integrantes. Una persona ajena a la cuenta no puede figurar como pagadora.
- Dada una cuenta de reparto equitativo con Raquel, Luis y María y saldos iniciales de 0 €, cuando Raquel paga 30 €, entonces los saldos quedan en +20 €, −10 € y −10 €, respectivamente.
- Dados esos saldos, cuando Luis paga otros 9 €, entonces los saldos de Raquel, Luis y María quedan en +17 €, −7 € y −13 €, respectivamente.
- Dada una cuenta con porcentajes del 40 %, 30 % y 30 % para Raquel, Luis y María respectivamente y saldos iniciales de 0 €, cuando Raquel paga 30 €, entonces los saldos quedan en +18 €, −9 € y −9 €, respectivamente.

### HU-15 — Consultar los saldos de una cuenta compartida

Como usuario de la aplicación, quiero ver el saldo de cada integrante para saber quién debe dinero y a quién se le debe.

**Criterio de aceptación**

- Dada una cuenta con gastos registrados, cuando consulto sus saldos, incluso después de reiniciar la aplicación, entonces puedo ver el saldo de cada integrante. Un saldo positivo indica cuánto se le debe, uno negativo indica cuánto debe y un saldo cero indica que no tiene importe pendiente.

## Importación

### HU-16 — Importar gastos desde un fichero

Como usuario de la aplicación, quiero importar gastos desde un fichero de texto para incorporar apuntes de una fuente externa sin introducirlos manualmente.

**Criterios de aceptación**

- Dado un fichero bancario válido en un formato admitido, cuando lo importo, entonces sus gastos se incorporan a la aplicación y se pueden consultar en la lista.
- Dado el fichero de ejemplo de Aula Virtual, cuento sus registros de gasto (excluyendo cabeceras) y anoto ese número como N. Si antes hay M gastos, tras importarlo una vez deben figurar M + N. Compruebo que los importes y fechas de los apuntes incorporados coinciden con sus registros de origen y que los M anteriores se conservan.
- Dados gastos importados, cuando reinicio la aplicación, entonces siguen disponibles para su consulta.

Para repetir la prueba con otro formato admitido, se usan archivos de prueba equivalentes con los mismos movimientos y se comparan sus resultados partiendo del mismo estado inicial. La aplicación debe estar preparada para incorporar otros formatos de origen.

## Referencia para el diagrama de interacción

La historia **HU-01 — Registrar un gasto personal** corresponde al flujo «Registrar un gasto personal» del [diagrama de interacción](diagrama-interaccion-hu01.md).
