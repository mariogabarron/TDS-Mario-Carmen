package es.um.tds.gastos;

import es.um.tds.gastos.controlador.GestionGastos;
import es.um.tds.gastos.controlador.GestionCuentas;
import es.um.tds.gastos.persistencia.AlmacenMemoria;
import es.um.tds.gastos.persistencia.CuentasMemoria;

public class ConfiguracionMemoria extends Configuracion {

    private final GestionCuentas cuentas;
    private final GestionGastos gestion;

    public ConfiguracionMemoria() {

        AlmacenMemoria almacen = new AlmacenMemoria();

        this.cuentas = new GestionCuentas(new CuentasMemoria());
        this.gestion = new GestionGastos(
            almacen.getRepositorioGastos(),
            almacen.getRepositorioCategorias(),
            almacen.getRepositorioAlertas(),
            almacen
        );
    }

    @Override
    public GestionCuentas getGestionCuentas() {
        return cuentas;
    }

    @Override
    public GestionGastos getGestionGastos() {
        return gestion;
    }
}