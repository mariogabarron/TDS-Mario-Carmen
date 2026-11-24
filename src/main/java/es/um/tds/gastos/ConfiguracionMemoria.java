package es.um.tds.gastos;

import es.um.tds.gastos.aplicacion.GestionGastos;

public class ConfiguracionMemoria extends Configuracion {

    private final GestionGastos gestion;

    public ConfiguracionMemoria() {
        this.gestion = new GestionGastos();
    }

    @Override
    public GestionGastos getGestionGastos() {
        return gestion;
    }
}