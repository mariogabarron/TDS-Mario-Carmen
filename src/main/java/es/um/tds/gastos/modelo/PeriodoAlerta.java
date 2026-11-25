package es.um.tds.gastos.modelo;

public enum PeriodoAlerta {

    SEMANAL(new PeriodoSemanal()),
    MENSUAL(new PeriodoMensual());

    private final EstrategiaPeriodo estrategia;

    PeriodoAlerta(EstrategiaPeriodo estrategia) {
        this.estrategia = estrategia;
    }

    public EstrategiaPeriodo estrategia() {
        return estrategia;
    }
}