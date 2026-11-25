package es.um.tds.gastos.modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/** Cada estrategia calcula los dos extremos inclusivos de su periodo natural. */
public enum PeriodoAlerta {
    SEMANAL {
        public LocalDate inicio(LocalDate fecha) {
            return fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        public LocalDate fin(LocalDate fecha) {
            return inicio(fecha).plusDays(6);
        }
    },
    MENSUAL {
        public LocalDate inicio(LocalDate fecha) {
            return fecha.withDayOfMonth(1);
        }
        public LocalDate fin(LocalDate fecha) {
            return fecha.with(TemporalAdjusters.lastDayOfMonth());
        }
    };

    public abstract LocalDate inicio(LocalDate fecha);
    public abstract LocalDate fin(LocalDate fecha);
}
