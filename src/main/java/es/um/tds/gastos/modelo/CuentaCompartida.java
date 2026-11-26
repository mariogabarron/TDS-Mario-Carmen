package es.um.tds.gastos.modelo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CuentaCompartida {
    private final UUID id;
    private final String nombre;
    private final Reparto reparto;
    private final Map<String, BigDecimal> integrantes;

    public CuentaCompartida(UUID id, String nombre, Reparto reparto, Map<String, BigDecimal> integrantes) {
        this.id = Objects.requireNonNull(id);
        if (nombre == null || nombre.isBlank() || integrantes == null || integrantes.isEmpty()) {
            throw new IllegalArgumentException("Introduce el nombre de la cuenta y sus integrantes.");
        }
        this.nombre = nombre.strip();
        this.reparto = Objects.requireNonNull(reparto);
        var copia = new LinkedHashMap<String, BigDecimal>();
        BigDecimal total = BigDecimal.ZERO;
        for (var entrada : integrantes.entrySet()) {
            String persona = entrada.getKey();
            if (persona == null || persona.isBlank() || copia.containsKey(persona.strip())) {
                throw new IllegalArgumentException("Cada integrante debe tener un nombre distinto.");
            }
            BigDecimal peso = reparto.peso(entrada.getValue());
            if (peso == null || peso.signum() < 0) {
                throw new IllegalArgumentException("Introduce un porcentaje válido para cada integrante.");
            }
            copia.put(persona.strip(), peso);
            total = total.add(peso);
        }
        if (reparto == Reparto.PORCENTUAL && total.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new IllegalArgumentException("Los porcentajes deben sumar 100 %.");
        }
        this.integrantes = Collections.unmodifiableMap(copia);
    }

    public UUID id() { return id; }
    public String nombre() { return nombre; }
    public Reparto reparto() { return reparto; }
    public Map<String, BigDecimal> integrantes() { return integrantes; }

    public void comprobarPagador(String persona) {
        if (!integrantes.containsKey(persona)) {
            throw new IllegalArgumentException("El pagador debe pertenecer a la cuenta.");
        }
    }

    public Map<String, BigDecimal> saldos(List<PagoCompartido> pagos) {
        var pagados = new LinkedHashMap<String, BigDecimal>();
        integrantes.keySet().forEach(persona -> pagados.put(persona, BigDecimal.ZERO));
        BigDecimal total = BigDecimal.ZERO;
        for (PagoCompartido pago : pagos) {
            comprobarPagador(pago.pagador());
            pagados.merge(pago.pagador(), pago.importe(), BigDecimal::add);
            total = total.add(pago.importe());
        }
        var cuotas = reparto.cuotas(total, integrantes);
        pagados.replaceAll((persona, importe) -> importe.subtract(cuotas.get(persona)));
        return pagados;
    }

    @Override
    public String toString() { return nombre; }
}
