package es.um.tds.gastos.persistencia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import es.um.tds.gastos.modelo.Categoria;

public final class CategoriasSQLite implements RepositorioCategorias {
    private final BaseDatos base;

    public CategoriasSQLite(BaseDatos base) {
        this.base = base;
    }

    public List<Categoria> listar() {
        var lista = new ArrayList<Categoria>();
        try (var con = base.abrir(); var orden = con.createStatement();
             var filas = orden.executeQuery("SELECT id, nombre FROM categorias ORDER BY rowid")) {
            while (filas.next()) {
                lista.add(new Categoria(UUID.fromString(filas.getString("id")), filas.getString("nombre")));
            }
            return List.copyOf(lista);
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    static void insertar(Connection con, Categoria categoria) throws SQLException {
        try (var orden = con.prepareStatement("INSERT INTO categorias (id, nombre, clave) VALUES (?, ?, ?) ON CONFLICT(clave) DO NOTHING")) {
            orden.setString(1, categoria.getId().toString());
            orden.setString(2, categoria.getNombre());
            orden.setString(3, categoria.getNombre().toUpperCase(Locale.ROOT));
            if (orden.executeUpdate() == 0) {
                throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
            }
        }
    }

    public void agregar(Categoria categoria) {
        try (var con = base.abrir()) {
            insertar(con, categoria);
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    public void modificar(Categoria categoria) {
        try (var con = base.abrir(); var orden = con.prepareStatement(
                "UPDATE categorias SET nombre=?, clave=? WHERE id=?")) {
            orden.setString(1, categoria.getNombre());
            orden.setString(2, categoria.getNombre().toUpperCase(Locale.ROOT));
            orden.setString(3, categoria.getId().toString());
            comprobar(orden.executeUpdate());
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new IllegalArgumentException("Ya existe una categoría con ese nombre.", e);
            }
            throw BaseDatos.error(e);
        }
    }

    public void eliminar(Categoria categoria) {
        try (var con = base.abrir(); var orden = con.prepareStatement("DELETE FROM categorias WHERE id=?")) {
            orden.setString(1, categoria.getId().toString());
            comprobar(orden.executeUpdate());
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new IllegalArgumentException("La categoría tiene gastos asociados.", e);
            }
            throw BaseDatos.error(e);
        }
    }

    private static void comprobar(int filas) {
        if (filas == 0) {
            throw new IllegalArgumentException("La categoría no está registrada.");
        }
    }
}
