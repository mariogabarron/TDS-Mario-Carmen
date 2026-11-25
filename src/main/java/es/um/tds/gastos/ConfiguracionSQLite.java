package es.um.tds.gastos;

import es.um.tds.gastos.aplicacion.GestionGastos;

public class ConfiguracionSQLite extends Configuracion {

    private final GestionGastos gestion;

    public ConfiguracionSQLite() {
        this.gestion = new GestionGastos();
    }

    @Override
    public GestionGastos getGestionGastos() {
        return gestion;
    }
}