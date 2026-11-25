package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public record Alerta(UUID id, BigDecimal limite, PeriodoAlerta periodo, Categoria categoria) {
    public Alerta {
        Objects.requireNonNull(id);
        Objects.requireNonNull(periodo);
        if (limite == null || limite.signum() <= 0) {
            throw new IllegalArgumentException("Introduce un límite mayor que cero.");
        }
        try {
            limite = limite.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("El límite debe expresarse en céntimos enteros.");
        }
    }

    public String ambito() {
        return categoria == null ? "Todas las categorías" : categoria.getNombre();
    }

    public boolean corresponde(Gasto gasto) {
        return categoria == null || categoria.equals(gasto.getCategoria());
    }

    public String toString() {
        return periodo + " · " + ambito() + " · límite " + limite.toPlainString() + " €";
    }
}
