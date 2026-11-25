
package es.um.tds.gastos.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PeriodoAlertaTest {

    private final EstrategiaPeriodo semanal =
        new PeriodoSemanal();

    private final EstrategiaPeriodo mensual =
        new PeriodoMensual();

    @Test
    void periodoSemanal() {

        LocalDate fecha = LocalDate.of(2026, 11, 25);

        assertEquals(
            LocalDate.of(2026, 11, 23),
            semanal.inicio(fecha)
        );

        assertEquals(
            LocalDate.of(2026, 11, 29),
            semanal.fin(fecha)
        );
    }

    @Test
    void semanaEntreDosAnios() {

        LocalDate fecha = LocalDate.of(2027, 1, 1);

        assertEquals(
            LocalDate.of(2026, 12, 28),
            semanal.inicio(fecha)
        );

        assertEquals(
            LocalDate.of(2027, 1, 3),
            semanal.fin(fecha)
        );
    }

    @Test
    void periodoMensual() {

        LocalDate fecha = LocalDate.of(2026, 11, 25);

        assertEquals(
            LocalDate.of(2026, 11, 1),
            mensual.inicio(fecha)
        );

        assertEquals(
            LocalDate.of(2026, 11, 30),
            mensual.fin(fecha)
        );
    }

    @Test
    void febreroBisiesto() {

        LocalDate fecha = LocalDate.of(2028, 2, 15);

        assertEquals(
            LocalDate.of(2028, 2, 29),
            mensual.fin(fecha)
        );
    }

    @Test
    void febreroNoBisiesto() {

        LocalDate fecha = LocalDate.of(2027, 2, 15);

        assertEquals(
            LocalDate.of(2027, 2, 28),
            mensual.fin(fecha)
        );
    }
}
