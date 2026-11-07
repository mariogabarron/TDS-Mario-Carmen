package es.um.tds.gastos.modelo;

public class Categoria {

    private final String nombre;

    public Categoria(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                "La categoría debe tener un nombre."
            );
        }

        this.nombre = nombre.strip();
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}