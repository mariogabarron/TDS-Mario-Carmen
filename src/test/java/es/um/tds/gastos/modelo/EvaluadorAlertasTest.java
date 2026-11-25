
package es.um.tds.gastos.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class EvaluadorAlertasTest {

    private final EvaluadorAlertas evaluador =
        new EvaluadorAlertas();

    private final Categoria juegos =
        new Categoria("Videojuegos");

    private final Categoria transporte =
        new Categoria("Transporte");

    private final LocalDate fecha =
        LocalDate.of(2026, 11, 24);

    private final Alerta alerta = new Alerta(
        UUID.randomUUID(),
        new BigDecimal("100"),
        PeriodoAlerta.MENSUAL,
        juegos
    );

    @Test
    void noNotificarAlIgualarLimite() {

        Gasto anterior = gasto("95", fecha, juegos);
        Gasto actual = gasto("5", fecha, juegos);

        var resultado = evaluador.evaluar(
            alerta, actual, null, List.of(anterior, actual)
        );

        assertTrue(resultado.isEmpty());
    }

    @Test
    void notificarAlSuperarLimite() {

        Gasto anterior = gasto("95", fecha, juegos);
        Gasto actual = gasto("6", fecha, juegos);

        var resultado = evaluador.evaluar(
            alerta, actual, null, List.of(anterior, actual)
        );

        assertTrue(resultado.isPresent());

        Notificacion aviso = resultado.orElseThrow();

        assertEquals(new BigDecimal("101.00"), aviso.total());
        assertEquals(new BigDecimal("100.00"), aviso.limite());
        assertEquals("Videojuegos", aviso.ambito());
        assertEquals(LocalDate.of(2026, 11, 1), aviso.inicio());
        assertEquals(LocalDate.of(2026, 11, 30), aviso.fin());
    }

    @Test
    void ignorarOtrasCategorias() {

        Gasto otro = gasto("200", fecha, transporte);
        Gasto actual = gasto("50", fecha, juegos);

        assertTrue(evaluador.evaluar(
            alerta, actual, null, List.of(otro, actual)
        ).isEmpty());
    }

    @Test
    void ignorarOtrosPeriodos() {

        Gasto anterior = gasto(
            "200", fecha.minusMonths(1), juegos
        );

        Gasto actual = gasto("50", fecha, juegos);

        assertTrue(evaluador.evaluar(
            alerta, actual, null, List.of(anterior, actual)
        ).isEmpty());
    }

    @Test
    void notificarAlModificarGasto() {

        Gasto anterior = gasto("100", fecha, juegos);

        Gasto actual = new Gasto(
            anterior.getId(),
            new BigDecimal("101"),
            fecha,
            juegos
        );

        assertTrue(evaluador.evaluar(
            alerta, actual, anterior, List.of(actual)
        ).isPresent());
    }

    @Test
    void noNotificarSiYaSuperabaLimite() {

        Gasto anterior = gasto("101", fecha, juegos);

        Gasto actual = new Gasto(
            anterior.getId(),
            new BigDecimal("102"),
            fecha,
            juegos
        );

        assertTrue(evaluador.evaluar(
            alerta, actual, anterior, List.of(actual)
        ).isEmpty());
    }

    @Test
    void alertaGeneralIncluyeTodasLasCategorias() {

        Alerta general = new Alerta(
            UUID.randomUUID(),
            new BigDecimal("100"),
            PeriodoAlerta.MENSUAL,
            null
        );

        Gasto primero = gasto("60", fecha, transporte);
        Gasto actual = gasto("50", fecha, juegos);

        var resultado = evaluador.evaluar(
            general, actual, null, List.of(primero, actual)
        );

        assertEquals(
            new BigDecimal("110.00"),
            resultado.orElseThrow().total()
        );
    }

    @Test
    void notificarAlCambiarDeCategoria() {

        Gasto anterior = gasto("110", fecha, transporte);

        Gasto actual = new Gasto(
            anterior.getId(),
            anterior.getImporte(),
            fecha,
            juegos
        );

        assertTrue(evaluador.evaluar(
            alerta, actual, anterior, List.of(actual)
        ).isPresent());
    }

    private Gasto gasto(
            String importe,
            LocalDate fecha,
            Categoria categoria) {

        return new Gasto(
            new BigDecimal(importe), fecha, categoria
        );
    }
}
