package es.um.tds.gastos.controlador;

import es.um.tds.gastos.modelo.*;
import es.um.tds.gastos.persistencia.RepositorioCuentas;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GestionCuentas {
    private final RepositorioCuentas cuentas;

    public GestionCuentas(RepositorioCuentas cuentas) {
        this.cuentas = cuentas;
    }

    public CuentaCompartida crearCuenta(String nombre, Reparto reparto, Map<String, BigDecimal> integrantes) {
        var cuenta = new CuentaCompartida(UUID.randomUUID(), nombre, reparto, integrantes);
        cuentas.agregar(cuenta);
        return cuenta;
    }

    public List<CuentaCompartida> listar() {
        return cuentas.listar();
    }

    public void registrarPago(CuentaCompartida cuenta, String persona, BigDecimal importe) {
        cuenta.comprobarPagador(persona);
        cuentas.guardarPago(cuenta.id(), new PagoCompartido(persona, importe));
    }

    public Map<String, BigDecimal> saldos(CuentaCompartida cuenta) {
        return cuenta.saldos(cuentas.pagos(cuenta.id()));
    }
}
