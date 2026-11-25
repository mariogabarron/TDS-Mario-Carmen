package es.um.tds.gastos.persistencia;

public class ErrorPersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErrorPersistenciaException(String mensaje) {
        super(mensaje);
    }

    public ErrorPersistenciaException(
            String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}