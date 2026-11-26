package es.um.tds.gastos;

import java.util.Objects;

import es.um.tds.gastos.controlador.GestionGastos;
import es.um.tds.gastos.controlador.GestionCuentas;
import es.um.tds.gastos.vista.SceneManager;

public abstract class Configuracion {

    private static Configuracion instancia;

    static void setInstancia(Configuracion impl) {
        if (instancia != null) {
            throw new IllegalStateException(
                "La configuración ya está inicializada."
            );
        }
        instancia = Objects.requireNonNull(impl);
    }

    public static Configuracion getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException(
                "La configuración no está inicializada."
            );
        }
        return instancia;
    }

    public abstract GestionCuentas getGestionCuentas();

    public abstract GestionGastos getGestionGastos();

    public SceneManager getViewController() {
        return SceneManager.getInstancia();
    }
}