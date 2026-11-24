# Diagrama de interacción · HU-01 — Registrar un gasto personal

Como usuario, quiero registrar un gasto con su importe, fecha y categoría para llevar un control de mis gastos.

El diagrama de secuencia UML representa el diseño previsto para el registro desde la interfaz gráfica. Los mensajes se leen de arriba abajo. Los nombres de los participantes indican responsabilidades de diseño y los bloques de alternativas distinguen el resultado de cada validación.

**Precondición.** El usuario ha abierto el formulario y dispone de al menos una categoría registrada.

```mermaid
sequenceDiagram
    actor U as Usuario
    participant V as Formulario JavaFX
    participant C as GestionGastos
    participant R as Repositorio de gastos
    U->>V: Introducir importe y fecha, elegir categoría y guardar
    V->>C: registrarGasto(importe, fecha, categoria)
    C->>C: comprobarCategoria(categoria)
    alt Categoría sin registrar
        C-->>V: Categoría inválida
        V-->>U: Indicar que debe elegir una categoría registrada
    else Categoría registrada
        create participant G as gasto de tipo Gasto
        C->>G: Crear Gasto(importe, fecha, categoria)
        G->>G: actualizarDatos(importe, fecha, categoria)
        alt Datos inválidos
            G-->>C: Error de validación
            C-->>V: Datos inválidos
            V-->>U: Mostrar el error para corregir los datos
        else Datos válidos
            G-->>C: Gasto creado
            C->>R: guardar(gasto)
            alt Escritura confirmada
                R-->>C: Guardado correcto
                C-->>V: Gasto registrado
                V-->>U: Mostrar el gasto en la lista
            else Fallo de escritura
                R-->>C: Error de persistencia
                C-->>V: Registro fallido
                V-->>U: Informar del fallo al guardar
            end
        end
    end
```

**Postcondición de éxito.** El gasto queda almacenado con el importe, la fecha y la categoría indicados. Si el usuario cierra y vuelve a abrir la aplicación, puede consultarlo en la lista.

**Comprobación de la HU-01.** Dada la categoría «Transporte», cuando el usuario guarda un gasto de 15 € del 24 de noviembre de 2026, entonces aparece un apunte con esos tres datos y sigue disponible tras reiniciar la aplicación.

**Alcance.** El repositorio representa el contrato de persistencia. Su implementación JDBC solo devuelve éxito tras completar la escritura. La recuperación al reiniciar se comprueba mediante el segundo criterio de [HU-01](historias-de-usuario.md), en una ejecución posterior.
