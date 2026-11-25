package es.um.tds.gastos.persistencia;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import es.um.tds.gastos.modelo.Categoria;

public final class BaseDatos implements GestorTransacciones {
	
    private final String url;
	private Connection conexionActiva;
	
	@FunctionalInterface
	interface OperacionSQL<T> {
	    T ejecutar(Connection conexion) throws SQLException;
	}
	
	synchronized <T> T conConexion(OperacionSQL<T> operacion) {
	    try {
	        if (conexionActiva != null) {
	            return operacion.ejecutar(conexionActiva);
	        }
	
	        try (Connection conexion = abrir()) {
	            return operacion.ejecutar(conexion);
	        }
	    } catch (SQLException e) {
	        throw error(e);
	    }
	}
	
	@Override
	public synchronized void ejecutar(Runnable operacion) {
	    if (conexionActiva != null) {
	        operacion.run();
	        return;
	    }
	
	    try (Connection conexion = abrir()) {
	
	        conexion.setAutoCommit(false);
	        conexionActiva = conexion;
	
	        try {
	            operacion.run();
	            conexion.commit();
	        } catch (SQLException | RuntimeException | Error e) {
	            try {
	                conexion.rollback();
	            } catch (SQLException falloRollback) {
	                e.addSuppressed(falloRollback);
	            }
	            throw e;
	
	        } finally {
	            conexionActiva = null;
	        }
	    } catch (SQLException e) {
	        throw error(e);
	    }
	}


    public BaseDatos(Path fichero) {
        url = "jdbc:sqlite:" + fichero.toAbsolutePath();
        try (var conexion = abrir(); var orden = conexion.createStatement()) {
            conexion.setAutoCommit(false);
            try {
                orden.executeUpdate("CREATE TABLE IF NOT EXISTS categorias (id TEXT PRIMARY KEY NOT NULL, nombre TEXT NOT NULL, clave TEXT NOT NULL UNIQUE)");
                orden.executeUpdate("CREATE TABLE IF NOT EXISTS gastos (id TEXT PRIMARY KEY NOT NULL, importe TEXT NOT NULL, fecha TEXT NOT NULL, categoria_id TEXT NOT NULL REFERENCES categorias(id))");
                // La versión se guarda en el fichero, incluso si después se borran categorías.
                boolean inicial;
                try (var resultado = orden.executeQuery("PRAGMA user_version")) {
                    resultado.next();
                    inicial = resultado.getInt(1) == 0;
                }
                if (inicial) {
                    for (String nombre : List.of("Alimentación", "Transporte", "Entretenimiento")) {
                        CategoriasSQLite.insertar(conexion, new Categoria(nombre));
                    }
                    orden.execute("PRAGMA user_version = 1");
                }
                orden.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS alertas (
                        id TEXT PRIMARY KEY NOT NULL, limite TEXT NOT NULL, periodo TEXT NOT NULL,
                        categoria_id TEXT REFERENCES categorias(id))
                    """);
                orden.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS notificaciones (
                        id INTEGER PRIMARY KEY, alerta_id TEXT NOT NULL REFERENCES alertas(id),
                        creada TEXT NOT NULL, inicio TEXT NOT NULL, fin TEXT NOT NULL,
                        ambito TEXT NOT NULL, limite TEXT NOT NULL, total TEXT NOT NULL)
                    """);
                orden.executeUpdate("CREATE INDEX IF NOT EXISTS gastos_fecha ON gastos(fecha)");
                orden.execute("PRAGMA user_version = 2");
                conexion.commit();
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw error(e);
        }
    }

    private Connection abrir() throws SQLException {
        Connection conexion = DriverManager.getConnection(url);
        try (var orden = conexion.createStatement()) {
            orden.execute("PRAGMA foreign_keys = ON");
            return conexion;
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }
    }

    static IllegalStateException error(SQLException e) {
        return new IllegalStateException("Error de acceso a SQLite: " + e.getMessage(), e);
    }
}
