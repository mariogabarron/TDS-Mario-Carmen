
package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EvaluadorAlertas {

    public Optional<Notificacion> evaluar(
            Alerta alerta,
            Gasto actual,
            Gasto anterior,
            List<Gasto> gastos) {

        if (!alerta.corresponde(actual)) {
            return Optional.empty();
        }

        LocalDate inicio =
            alerta.periodo().inicio(actual.getFecha());

        LocalDate fin =
            alerta.periodo().fin(actual.getFecha());

        BigDecimal total = gastos.stream()
            .filter(gasto -> corresponde(
                alerta, gasto, inicio, fin
            ))
            .map(Gasto::getImporte)
            .reduce(new BigDecimal("0.00"), BigDecimal::add);

        BigDecimal antes = total.subtract(actual.getImporte());

        if (anterior != null &&
                corresponde(alerta, anterior, inicio, fin)) {

            antes = antes.add(anterior.getImporte());
        }

        if (antes.compareTo(alerta.limite()) <= 0
                && total.compareTo(alerta.limite()) > 0) {

            return Optional.of(new Notificacion(
                LocalDateTime.now(),
                inicio,
                fin,
                alerta.ambito(),
                alerta.limite(),
                total
            ));
        }

        return Optional.empty();
    }

    private boolean corresponde(
            Alerta alerta,
            Gasto gasto,
            LocalDate inicio,
            LocalDate fin) {

        return alerta.corresponde(gasto)
            && !gasto.getFecha().isBefore(inicio)
            && !gasto.getFecha().isAfter(fin);
    }
}
