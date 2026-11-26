package es.um.tds.gastos.persistencia;

import es.um.tds.gastos.modelo.CuentaCompartida;
import es.um.tds.gastos.modelo.PagoCompartido;
import java.util.List;
import java.util.UUID;

public interface RepositorioCuentas {
    void agregar(CuentaCompartida cuenta);
    List<CuentaCompartida> listar();
    void guardarPago(UUID cuenta, PagoCompartido pago);
    List<PagoCompartido> pagos(UUID cuenta);
}
