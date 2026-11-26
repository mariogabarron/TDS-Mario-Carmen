package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PagoCompartido(String pagador, BigDecimal importe) {
    public PagoCompartido {
        if (pagador == null || pagador.isBlank()) {
            throw new IllegalArgumentException("Selecciona quién ha pagado.");
        }
        if (importe == null || importe.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que cero.");
        }
        try {
            importe = importe.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("El importe no puede tener fracciones de céntimo.", e);
        }
    }
}
