# Diagrama de interacción · HU-01 — Registrar un gasto personal

Como usuario, quiero registrar un gasto con su importe, fecha y categoría para llevar un control de mis gastos.

**Precondición.** El usuario ha abierto el formulario y dispone de al menos una categoría registrada.

```mermaid
sequenceDiagram
    participant V as GastosViewController
    participant C as GestionGastos
    participant G as RepositorioGastos
    participant E as EvaluadorAlertas
    participant A as RepositorioAlertas
    V->>C: registrarGasto(importe, fecha, categoria)
    C->>C: Comprobar la categoría y construir el gasto
    Note over C,A: Inicio de la transacción
    C->>G: agregar(gasto)
    C->>G: listar()
    G-->>C: Gastos registrados
    C->>A: listar()
    A-->>C: Alertas configuradas
    loop Por cada alerta
        C->>E: evaluar(alerta, gasto, null, registrados)
        opt El total cruza el límite
            E-->>C: Notificación
            C->>A: guardarNotificacion(alerta, notificacion)
        end
    end
    Note over C,A: Confirmación de la transacción
    C-->>V: Gasto registrado
    V->>V: Actualizar la tabla
```

**Validación.** El controlador comprueba que la categoría esté registrada. El constructor de `Gasto` valida los datos del apunte. Si algún dato es inválido, la vista muestra el error y el alta se interrumpe antes de escribir en el repositorio.

**Postcondición.** El gasto y los avisos que haya generado quedan guardados en la misma transacción. Si falla una escritura en SQLite, se revierten los cambios de esa operación.

**Comprobación de la [HU-01](historias-de-usuario.md).** Al registrar un gasto de 15 € del 24 de noviembre de 2026 en «Transporte», la tabla muestra el importe, la fecha y la categoría. El apunte se conserva al volver a abrir la aplicación.
