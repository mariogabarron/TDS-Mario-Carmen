package es.um.tds.gastos.persistencia;

public interface GestorTransacciones {

    void ejecutar(Runnable operacion);
}