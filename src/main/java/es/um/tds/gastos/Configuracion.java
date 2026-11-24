package es.um.tds.gastos;

import es.um.tds.gastos.aplicacion.GestionGastos;
import es.um.tds.gastos.controlador.ViewController;

public abstract class Configuracion {

    private static Configuracion instancia;
    private final ViewController viewController = new ViewController();

    static void setInstancia(Configuracion impl) {
        Configuracion.instancia = impl;
    }

    public static Configuracion getInstancia() {
        return Configuracion.instancia;
    }

    public abstract GestionGastos getGestionGastos();

    public ViewController getViewController() {
        return viewController;
    }
}