package es.um.tds.gastos.persistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import es.um.tds.gastos.modelo.Categoria;
import es.um.tds.gastos.modelo.Gasto;
import java.util.Optional;

public final class GastosSQLite implements RepositorioGastos {
    private final BaseDatos base;

    public GastosSQLite(BaseDatos base) {
        this.base = base;
    }

	
	public List<Gasto> listar() {
	    return base.conConexion(con -> {
	        String sql = """
	            SELECT g.id, g.importe, g.fecha,
	                   c.id AS categoria_id, c.nombre
	            FROM gastos g
	            JOIN categorias c ON c.id = g.categoria_id
	            ORDER BY g.rowid
	            """;
	        var gastos = new ArrayList<Gasto>();
	        try (var orden = con.createStatement();
	             var filas = orden.executeQuery(sql)) {
	            while (filas.next()) {
	                Categoria categoria = new Categoria(
	                    UUID.fromString(filas.getString("categoria_id")),
	                    filas.getString("nombre")
	                );
	                gastos.add(new Gasto(
	                    UUID.fromString(filas.getString("id")),
	                    new BigDecimal(filas.getString("importe")),
	                    LocalDate.parse(filas.getString("fecha")),
	                    categoria
	                ));
	            }
	        }
	        return List.copyOf(gastos);
	    });
	}

	
	public void agregar(Gasto gasto) {
	    base.conConexion(con -> {
	        escribir(con,
	            """
	            INSERT INTO gastos (importe, fecha, categoria_id, id)
	            VALUES (?, ?, ?, ?)
	            """,
	            gasto
	        );
	        return null;
	    });
	}
	
	public void modificar(Gasto gasto) {
	    base.conConexion(con -> {
	        escribir(con,
	            """
	            UPDATE gastos
	            SET importe=?, fecha=?, categoria_id=?
	            WHERE id=?
	            """,
	            gasto
	        );
	        return null;
	    });
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

	@Override
	public void eliminar(Gasto gasto) {
	    base.conConexion(con -> {
	        try (var orden = con.prepareStatement(
	                "DELETE FROM gastos WHERE id=?")) {
	            orden.setString(1, gasto.getId().toString());
	            comprobar(orden.executeUpdate());
	        }
	        return null;
	    });
	}

    private static void comprobar(int filas) {
        if (filas == 0) {
            throw new IllegalArgumentException("El gasto no está registrado.");
        }
    }

	@Override
	public Optional<Gasto> buscarPorId(UUID id) {
	    return base.conConexion(con -> {
	        String sql = """
	            SELECT g.id, g.importe, g.fecha,
	                   c.id AS categoria_id, c.nombre
	            FROM gastos g
	            JOIN categorias c ON c.id = g.categoria_id
	            WHERE g.id = ?
	            """;
	
	        try (var orden = con.prepareStatement(sql)) {
	            orden.setString(1, id.toString());
	            try (var filas = orden.executeQuery()) {
	                if (!filas.next()) {
	                    return Optional.empty();
	                }
	                Categoria categoria = new Categoria(
	                    UUID.fromString(filas.getString("categoria_id")),
	                    filas.getString("nombre")
	                );
	                return Optional.of(new Gasto(
	                    UUID.fromString(filas.getString("id")),
	                    new BigDecimal(filas.getString("importe")),
	                    LocalDate.parse(filas.getString("fecha")),
	                    categoria
	                ));
	            }
	        }
	    });
	}

}
