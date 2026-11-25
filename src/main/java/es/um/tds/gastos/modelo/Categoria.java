package es.um.tds.gastos.modelo;

import java.util.UUID;
import java.util.Objects;

public final class Categoria {
    private final UUID id;

    public UUID getId() {
        return id;
    }


    private final String nombre;

    public Categoria(String nombre) {
        this(UUID.randomUUID(), nombre);
    }

    public Categoria(UUID id, String nombre) {
        this.id = Objects.requireNonNull(id);
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
    public boolean equals(Object otro) {
        return otro instanceof Categoria categoria && id.equals(categoria.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}