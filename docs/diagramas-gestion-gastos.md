# Diagramas de Gestión de Gastos

## Diagrama de clases del dominio

### Gastos personales y filtros

Esta parte recoge las clases de `modelo` que ya existen. Cada gasto pertenece a una categoría y una categoría puede aparecer en muchos gastos. Un filtro puede seleccionar varios meses y categorías, y sus dos fechas límite son opcionales e inclusivas. Si se seleccionan varios criterios, el gasto debe cumplirlos todos.

```mermaid
classDiagram
direction LR
class Gasto {
    BigDecimal importe
    LocalDate fecha
}
class Categoria {
    String nombre
}
class FiltroGastos {
    Set~Month~ meses
    LocalDate desde
    LocalDate hasta
}
Gasto "0..*" --> "1" Categoria : categoría
FiltroGastos --> "0..*" Categoria : categorías seleccionadas
FiltroGastos ..> Gasto : comprueba
```

Por ejemplo, un gasto de 12 € en Transporte del 24 de noviembre cumple un filtro de noviembre y Transporte. La flecha discontinua indica que el filtro recibe un gasto para comprobarlo. `GestionGastos` coordina estas operaciones desde la capa de aplicación y aparece en el diagrama de interacción.

### Alertas y notificaciones

Propuesta de dominio basada en el enunciado. La alerta define un límite semanal o mensual. Puede aplicarse al total general o a una categoría concreta. Las notificaciones se generan al superar el límite y forman el historial consultable. `Categoria` representa el mismo concepto que en el primer dibujo.

```mermaid
classDiagram
direction LR
class Alerta {
    importeLimite
    periodicidad
}
class Categoria
class Notificacion {
    fecha
    mensaje
}
Alerta "0..*" --> "0..1" Categoria : restringe a
Alerta "1" -- "0..*" Notificacion : genera
```

### Cuentas compartidas

Propuesta de dominio basada en el enunciado. La lista de participantes queda fijada al crear la cuenta. Cada gasto se registra con un pagador de esa cuenta. El reparto es igualitario o por porcentajes personalizados que suman el 100 %. El saldo de cada participante se calcula restando a lo que ha pagado la parte que le corresponde.

```mermaid
classDiagram
direction LR
class CuentaCompartida {
    tipoReparto
}
class Participante {
    nombre
    porcentajePersonalizado
}
class GastoCompartido {
    importe
}
CuentaCompartida "1" *-- "1..*" Participante : incluye
CuentaCompartida "1" *-- "0..*" GastoCompartido : registra
GastoCompartido "0..*" --> "1" Participante : pagado por
```

El rombo negro indica que los participantes y los gastos de este dibujo pertenecen a una cuenta concreta. Se usa `1..*` porque el enunciado no fija un mínimo explícito. `GastoCompartido` se modela por separado porque el enunciado le exige cuenta y pagador, y no le asigna expresamente una categoría.

## Diagrama de interacción

### Registrar un gasto personal

> Como usuario, quiero registrar un gasto con su importe, fecha y categoría para llevar un control de mis gastos.

La secuencia muestra la lógica de registro que ya se puede ejecutar desde la clase de prueba en Eclipse. La categoría se obtiene previamente de la gestión. El constructor de `Gasto` valida los datos mediante `actualizarDatos`. Si las validaciones pasan, la gestión añade el gasto a su lista en memoria y lo devuelve.

```mermaid
sequenceDiagram
    participant P as PruebaGestionGastos
    participant S as GestionGastos
    P->>S: registrarGasto(importe, fecha, categoria)
    S->>S: comprobarCategoria(categoria)
    alt Categoría registrada
        create participant G as Gasto
        S->>G: Constructor con importe, fecha y categoría
        G->>G: actualizarDatos(importe, fecha, categoria)
        alt Datos válidos
            G-->>S: gasto creado
            S->>S: gastos.add(gasto)
            S-->>P: gasto registrado
        else Datos inválidos
            G-->>S: IllegalArgumentException
            S-->>P: IllegalArgumentException
        end
    else Categoría sin registrar
        S-->>P: IllegalArgumentException
    end
```

La secuencia se puede ampliar con el controlador JavaFX y el repositorio cuando se compruebe cómo se conectan al registro.

