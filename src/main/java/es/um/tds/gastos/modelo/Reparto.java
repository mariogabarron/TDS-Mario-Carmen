package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Cada estrategia determina el peso de un integrante en el gasto de la cuenta. */
public enum Reparto {
    EQUITATIVO {
        @Override
        BigDecimal peso(BigDecimal porcentaje) {
            return BigDecimal.ONE;
        }
    },
    PORCENTUAL {
        @Override
        BigDecimal peso(BigDecimal porcentaje) {
            return porcentaje;
        }
    };

    abstract BigDecimal peso(BigDecimal porcentaje);

    Map<String, BigDecimal> cuotas(BigDecimal total, Map<String, BigDecimal> integrantes) {
        var cuotas = new LinkedHashMap<String, BigDecimal>();
        var restos = new LinkedHashMap<String, BigDecimal>();
        BigDecimal sumaPesos = integrantes.values().stream()
            .map(this::peso).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal centimos = total.movePointRight(2);
        BigDecimal pendientes = centimos;
        for (var integrante : integrantes.entrySet()) {
            BigDecimal producto = centimos.multiply(peso(integrante.getValue()));
            BigDecimal cuota = producto.divide(sumaPesos, 0, RoundingMode.DOWN);
            cuotas.put(integrante.getKey(), cuota.movePointLeft(2));
            restos.put(integrante.getKey(), producto.subtract(cuota.multiply(sumaPesos)));
            pendientes = pendientes.subtract(cuota);
        }
        // La ordenación estable respeta el orden de inscripción en caso de empate.
        var orden = new ArrayList<>(integrantes.keySet());
        orden.sort(Comparator.comparing(restos::get, Comparator.reverseOrder()));
        for (int i = 0; i < pendientes.intValueExact(); i++) {
            String nombre = orden.get(i);
            cuotas.put(nombre, cuotas.get(nombre).add(new BigDecimal("0.01")));
        }
        return cuotas;
    }
}
