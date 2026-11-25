package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Notificacion(LocalDateTime creada, LocalDate inicio, LocalDate fin,
                           String ambito, BigDecimal limite, BigDecimal total) {
    public String toString() {
        return creada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " · " + ambito
            + " · " + inicio + " a " + fin + " · " + total.toPlainString()
            + " € (límite " + limite.toPlainString() + " €)";
    }
}
