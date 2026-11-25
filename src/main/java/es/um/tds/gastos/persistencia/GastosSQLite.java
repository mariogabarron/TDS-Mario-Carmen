package es.um.tds.gastos.persistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.Gasto;

public final class GastosSQLite implements RepositorioGastos {
    private final BaseDatos base;

    public GastosSQLite(BaseDatos base) {
        this.base = base;
    }

    public List<Gasto> listar() {
        String sql = """
            SELECT g.id, g.importe, g.fecha, c.id AS categoria_id, c.nombre
            FROM gastos g JOIN categorias c ON c.id = g.categoria_id
            ORDER BY g.rowid
            """;
        var gastos = new ArrayList<Gasto>();
        try (var con = base.abrir(); var orden = con.createStatement(); var filas = orden.executeQuery(sql)) {
            while (filas.next()) {
                Categoria categoria = new Categoria(UUID.fromString(filas.getString("categoria_id")), filas.getString("nombre"));
                gastos.add(new Gasto(UUID.fromString(filas.getString("id")), new BigDecimal(filas.getString("importe")),
                    LocalDate.parse(filas.getString("fecha")), categoria));
            }
            return List.copyOf(gastos);
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    public void agregar(Gasto gasto) {
        importar(List.of(gasto));
    }

    @Override
    public void importar(List<Gasto> gastos) {
        try (var con = base.abrir()) {
            con.setAutoCommit(false);
            try {
                for (Gasto gasto : gastos) {
                    escribir(con, "INSERT INTO gastos (importe, fecha, categoria_id, id) VALUES (?, ?, ?, ?)", gasto);
                    AlertasSQLite.evaluar(con, gasto, null);
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    public void modificar(Gasto gasto) {
        try (var con = base.abrir()) {
            con.setAutoCommit(false);
            try {
                Gasto anterior = recuperar(con, gasto.getId());
                escribir(con, "UPDATE gastos SET importe=?, fecha=?, categoria_id=? WHERE id=?", gasto);
                AlertasSQLite.evaluar(con, gasto, anterior);
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    private static Gasto recuperar(java.sql.Connection con, UUID id) throws SQLException {
        String sql = """
            SELECT g.importe, g.fecha, c.id, c.nombre FROM gastos g
            JOIN categorias c ON c.id=g.categoria_id WHERE g.id=?
            """;
        try (var orden = con.prepareStatement(sql)) {
            orden.setString(1, id.toString());
            try (var fila = orden.executeQuery()) {
                if (!fila.next()) {
                    throw new IllegalArgumentException("El gasto no está registrado.");
                }
                var categoria = new Categoria(UUID.fromString(fila.getString(3)), fila.getString(4));
                return new Gasto(id, new BigDecimal(fila.getString(1)), LocalDate.parse(fila.getString(2)), categoria);
            }
        }
    }

    private static void escribir(java.sql.Connection con, String sql, Gasto gasto) throws SQLException {
        try (var orden = con.prepareStatement(sql)) {
            orden.setString(1, gasto.getImporte().toPlainString());
            orden.setString(2, gasto.getFecha().toString());
            orden.setString(3, gasto.getCategoria().getId().toString());
            orden.setString(4, gasto.getId().toString());
            comprobar(orden.executeUpdate());
        }
    }

    public void eliminar(Gasto gasto) {
        try (var con = base.abrir(); var orden = con.prepareStatement("DELETE FROM gastos WHERE id=?")) {
            orden.setString(1, gasto.getId().toString());
            comprobar(orden.executeUpdate());
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    private static void comprobar(int filas) {
        if (filas == 0) {
            throw new IllegalArgumentException("El gasto no está registrado.");
        }
    }
}
