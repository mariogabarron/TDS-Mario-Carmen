package es.um.tds.gastos.persistencia;

import es.um.tds.gastos.modelo.CuentaCompartida;
import es.um.tds.gastos.modelo.PagoCompartido;
import java.util.*;

public final class CuentasMemoria implements RepositorioCuentas {
    private final Map<UUID, CuentaCompartida> cuentas = new LinkedHashMap<>();
    private final Map<UUID, List<PagoCompartido>> pagos = new HashMap<>();

    public synchronized void agregar(CuentaCompartida cuenta) {
        if (cuentas.putIfAbsent(cuenta.id(), cuenta) != null) {
            throw new IllegalArgumentException("La cuenta ya existe.");
        }
        pagos.put(cuenta.id(), new ArrayList<>());
    }

    public synchronized List<CuentaCompartida> listar() {
        return List.copyOf(cuentas.values());
    }

    public synchronized void guardarPago(UUID id, PagoCompartido pago) {
        var cuenta = cuentas.get(id);
        if (cuenta == null) throw new IllegalArgumentException("La cuenta no existe.");
        cuenta.comprobarPagador(pago.pagador());
        pagos.get(id).add(pago);
    }

    public synchronized List<PagoCompartido> pagos(UUID id) {
        return List.copyOf(pagos.getOrDefault(id, List.of()));
    }
}
