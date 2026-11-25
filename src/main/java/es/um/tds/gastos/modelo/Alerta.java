package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.util.UUID;

public record Alerta(
	    UUID id,
	    BigDecimal limite,
	    EstrategiaPeriodo periodo,
	    Categoria categoria
	) {

    public String ambito() {
        return categoria == null ? "Todas las categorías" : categoria.getNombre();
    }

    public boolean corresponde(Gasto gasto) {
        return categoria == null || categoria.equals(gasto.getCategoria());
    }

    @Override
    public String toString() {
        return periodo.codigo()
            + " · " + ambito()
            + " · límite " + limite.toPlainString() + " €";
    }
}
