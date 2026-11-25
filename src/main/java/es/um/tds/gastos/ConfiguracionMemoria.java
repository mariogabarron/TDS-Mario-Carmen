package es.um.tds.gastos;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.persistencia.AlmacenMemoria;

public class ConfiguracionMemoria extends Configuracion {

    private final GestionGastos gestion;

    public ConfiguracionMemoria() {

        AlmacenMemoria almacen = new AlmacenMemoria();

        this.gestion = new GestionGastos(
            almacen.getRepositorioGastos(),
            almacen.getRepositorioCategorias(),
            almacen.getRepositorioAlertas(),
            almacen
        );
    }

    @Override
    public GestionGastos getGestionGastos() {
        return gestion;
    }
}