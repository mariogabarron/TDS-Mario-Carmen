
package es.um.tds.gastos.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PeriodoAlertaTest {

    @Test
    void periodoSemanal() {

        LocalDate fecha = LocalDate.of(2026, 11, 25);

        assertEquals(
            LocalDate.of(2026, 11, 23),
            PeriodoAlerta.SEMANAL.inicio(fecha)
        );

        assertEquals(
            LocalDate.of(2026, 11, 29),
            PeriodoAlerta.SEMANAL.fin(fecha)
        );
    }

    @Test
    void semanaEntreDosAnios() {

        LocalDate fecha = LocalDate.of(2027, 1, 1);

        assertEquals(
            LocalDate.of(2026, 12, 28),
            PeriodoAlerta.SEMANAL.inicio(fecha)
        );

        assertEquals(
            LocalDate.of(2027, 1, 3),
            PeriodoAlerta.SEMANAL.fin(fecha)
        );
    }

    @Test
    void periodoMensual() {

        LocalDate fecha = LocalDate.of(2026, 11, 25);

        assertEquals(
            LocalDate.of(2026, 11, 1),
            PeriodoAlerta.MENSUAL.inicio(fecha)
        );

        assertEquals(
            LocalDate.of(2026, 11, 30),
            PeriodoAlerta.MENSUAL.fin(fecha)
        );
    }

    @Test
    void febreroBisiesto() {

        LocalDate fecha = LocalDate.of(2028, 2, 15);

        assertEquals(
            LocalDate.of(2028, 2, 29),
            PeriodoAlerta.MENSUAL.fin(fecha)
        );
    }

    @Test
    void febreroNoBisiesto() {

        LocalDate fecha = LocalDate.of(2027, 2, 15);

        assertEquals(
            LocalDate.of(2027, 2, 28),
            PeriodoAlerta.MENSUAL.fin(fecha)
        );
    }
}
