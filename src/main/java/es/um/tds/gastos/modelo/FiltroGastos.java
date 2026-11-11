package es.um.tds.gastos.modelo;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

public class FiltroGastos {

    private final Set<Month> meses;
    private final Set<Categoria> categorias;
    private final LocalDate desde;
    private final LocalDate hasta;

    public FiltroGastos(Set<Month> meses,
                       Set<Categoria> categorias,
                       LocalDate desde,
                       LocalDate hasta) {

        if (desde != null && hasta != null
                && desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                "La fecha inicial no puede ser posterior a la final."
            );
        }

        this.meses = Set.copyOf(meses);
        this.categorias = Set.copyOf(categorias);
        this.desde = desde;
        this.hasta = hasta;
    }

    public static FiltroGastos sinRestricciones() {
        return new FiltroGastos(
            Set.of(), Set.of(), null, null
        );
    }

    public boolean cumple(Gasto gasto) {
        LocalDate fecha = gasto.getFecha();

        boolean cumpleMes =
            meses.isEmpty() || meses.contains(fecha.getMonth());

        boolean cumpleCategoria =
            categorias.isEmpty()
                || categorias.contains(gasto.getCategoria());

        boolean cumpleDesde =
            desde == null || !fecha.isBefore(desde);

        boolean cumpleHasta =
            hasta == null || !fecha.isAfter(hasta);

        return cumpleMes
            && cumpleCategoria
            && cumpleDesde
            && cumpleHasta;
    }
}