package es.um.tds.gastos.modelo;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class PeriodoMensual implements EstrategiaPeriodo {

    @Override
    public LocalDate inicio(LocalDate fecha) {
        return fecha.withDayOfMonth(1);
    }

    @Override
    public LocalDate fin(LocalDate fecha) {
        return fecha.with(
            TemporalAdjusters.lastDayOfMonth()
        );
    }

    @Override
    public String codigo() {
        return "MENSUAL";
    }
}