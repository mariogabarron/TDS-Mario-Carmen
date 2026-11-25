
package es.um.tds.gastos.persistencia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import es.um.tds.gastos.modelo.Categoria;

public final class CategoriasSQLite
        implements RepositorioCategorias {

    private final BaseDatos base;

    public CategoriasSQLite(BaseDatos base) {
        this.base = base;
    }

    @Override
    public List<Categoria> listar() {

        return base.conConexion(con -> {

            var lista = new ArrayList<Categoria>();

            String sql = """
                SELECT id, nombre
                FROM categorias
                ORDER BY rowid
                """;

            try (var orden = con.createStatement();
                 var filas = orden.executeQuery(sql)) {

                while (filas.next()) {

                    lista.add(new Categoria(
                        UUID.fromString(filas.getString("id")),
                        filas.getString("nombre")
                    ));
                }
            }

            return List.copyOf(lista);
        });
    }

    static void insertar(Connection con, Categoria categoria)
            throws SQLException {

        String sql = """
            INSERT INTO categorias (id, nombre, clave)
            VALUES (?, ?, ?)
            ON CONFLICT(clave) DO NOTHING
            """;

        try (var orden = con.prepareStatement(sql)) {

            orden.setString(1, categoria.getId().toString());
            orden.setString(2, categoria.getNombre());
            orden.setString(
                3, categoria.getNombre().toUpperCase(Locale.ROOT)
            );

            if (orden.executeUpdate() == 0) {
                throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre."
                );
            }
        }
    }

    @Override
    public void agregar(Categoria categoria) {
        base.conConexion(con -> {
            insertar(con, categoria);
            return null;
        });
    }

    @Override
    public void modificar(Categoria categoria) {

        base.conConexion(con -> {
            String sql = """
                UPDATE categorias
                SET nombre=?, clave=?
                WHERE id=?
                """;
            try (var orden = con.prepareStatement(sql)) {
                orden.setString(1, categoria.getNombre());
                orden.setString(
                    2, categoria.getNombre().toUpperCase(Locale.ROOT)
                );

                orden.setString(3, categoria.getId().toString());
                comprobar(orden.executeUpdate());

            } catch (SQLException e) {
                if (e.getErrorCode() == 19) {
                    throw new IllegalArgumentException(
                        "Ya existe una categoría con ese nombre.",
                        e
                    );
                }
                throw e;
            }
            return null;
        });
    }

    @Override
    public void eliminar(Categoria categoria) {
        base.conConexion(con -> {
            String sql = "DELETE FROM categorias WHERE id=?";
            try (var orden = con.prepareStatement(sql)) {
                orden.setString(1, categoria.getId().toString());
                comprobar(orden.executeUpdate());

            } catch (SQLException e) {
                if (e.getErrorCode() == 19) {
                    throw new IllegalArgumentException(
                        "La categoría tiene gastos o alertas asociados.",
                        e
                    );
                }
                throw e;
            }
            return null;
        });
    }

    private static void comprobar(int filas) {

        if (filas == 0) {
            throw new IllegalArgumentException(
                "La categoría no está registrada."
            );
        }
    }
}
