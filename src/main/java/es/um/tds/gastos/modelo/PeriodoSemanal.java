package es.um.tds.gastos.modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class PeriodoSemanal implements EstrategiaPeriodo {

    @Override
    public LocalDate inicio(LocalDate fecha) {
        return fecha.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );
    }

    @Override
    public LocalDate fin(LocalDate fecha) {
        return inicio(fecha).plusDays(6);
    }

    @Override
    public String codigo() {
        return "SEMANAL";
    }
}