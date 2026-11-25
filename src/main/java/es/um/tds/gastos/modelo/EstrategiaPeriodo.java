package es.um.tds.gastos.modelo;

import java.time.LocalDate;

public interface EstrategiaPeriodo {

    LocalDate inicio(LocalDate fecha);

    LocalDate fin(LocalDate fecha);

    String codigo();
}