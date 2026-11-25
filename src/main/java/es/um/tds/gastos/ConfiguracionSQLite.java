package es.um.tds.gastos;

import java.nio.file.Path;

import es.um.tds.gastos.controlador.GestionGastos;
import es.um.tds.gastos.persistencia.*;

public class ConfiguracionSQLite extends Configuracion {

    private final GestionGastos gestion;

    public ConfiguracionSQLite() {

        this(Path.of(
            System.getProperty("user.home"),
            "gestion-gastos-tds.db"
        ));
    }

    public ConfiguracionSQLite(Path fichero) {

        BaseDatos base = new BaseDatos(fichero);

        this.gestion = new GestionGastos(
            new GastosSQLite(base),
            new CategoriasSQLite(base),
            new AlertasSQLite(base),
            base
        );
    }

    @Override
    public GestionGastos getGestionGastos() {
        return gestion;
    }
}