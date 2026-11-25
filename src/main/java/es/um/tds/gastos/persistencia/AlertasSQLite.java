package es.um.tds.gastos.persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import es.um.tds.gastos.modelo.*;

public final class AlertasSQLite implements RepositorioAlertas {
    private final BaseDatos base;

    public AlertasSQLite(BaseDatos base) {
        this.base = base;
    }

	@Override
	public void agregar(Alerta alerta) {
	    base.conConexion(con -> {
	        String sql = """
	            INSERT INTO alertas(id, limite, periodo, categoria_id)
	            VALUES (?, ?, ?, ?)
	            """;
	        try (var orden = con.prepareStatement(sql)) {
	            orden.setString(1, alerta.id().toString());
	            orden.setString(2, alerta.limite().toPlainString());
	            orden.setString(3, alerta.periodo().codigo());
	            if (alerta.categoria() == null) {
	                orden.setNull(4, Types.VARCHAR);
	            } else {
	                orden.setString(
	                    4, alerta.categoria().getId().toString()
	                );
	            }
	            orden.executeUpdate();
	        }
	        return null;
	    });
	}

    public List<Alerta> listar() {
        return base.conConexion(con -> listar(con));
    }
    
    static List<Alerta> listar(Connection con) throws SQLException {
        var alertas = new ArrayList<Alerta>();
        String sql = """
            SELECT a.id, a.limite, a.periodo, a.categoria_id, c.nombre
            FROM alertas a LEFT JOIN categorias c ON c.id=a.categoria_id ORDER BY a.rowid
            """;
        try (var orden = con.createStatement(); var filas = orden.executeQuery(sql)) {
            while (filas.next()) {
                String id = filas.getString("categoria_id");
                Categoria categoria = id == null ? null : new Categoria(UUID.fromString(id), filas.getString("nombre"));
                alertas.add(new Alerta(UUID.fromString(filas.getString("id")), new BigDecimal(filas.getString("limite")),
                		PeriodoAlerta.valueOf(filas.getString("periodo")).estrategia(), categoria));
            }
        }
        return List.copyOf(alertas);
    }

	@Override
	public List<Notificacion> historial() {
	    return base.conConexion(con -> {
	        var avisos = new ArrayList<Notificacion>();
	        String sql = """
	            SELECT creada, inicio, fin, ambito, limite, total
	            FROM notificaciones
	            ORDER BY id DESC
	            """;
	        try (var orden = con.createStatement();
	             var filas = orden.executeQuery(sql)) {
	            while (filas.next()) {
	                avisos.add(new Notificacion(
	                    LocalDateTime.parse(filas.getString(1)),
	                    LocalDate.parse(filas.getString(2)),
	                    LocalDate.parse(filas.getString(3)),
	                    filas.getString(4),
	                    new BigDecimal(filas.getString(5)),
	                    new BigDecimal(filas.getString(6))
	                ));
	            }
	        }
	        return List.copyOf(avisos);
	    });
	}

	@Override
	public void guardarNotificacion(
	        Alerta alerta,
	        Notificacion notificacion) {
	
	    base.conConexion(con -> {
	
	        String sql = """
	            INSERT INTO notificaciones(
	                alerta_id, creada, inicio, fin,
	                ambito, limite, total
	            ) VALUES (?, ?, ?, ?, ?, ?, ?)
	            """;
	
	        try (var orden = con.prepareStatement(sql)) {
	            orden.setString(1, alerta.id().toString());
	            orden.setString(2, notificacion.creada().toString());
	            orden.setString(3, notificacion.inicio().toString());
	            orden.setString(4, notificacion.fin().toString());
	            orden.setString(5, notificacion.ambito());
	            orden.setString(6, notificacion.limite().toPlainString());
	            orden.setString(7, notificacion.total().toPlainString());
	            orden.executeUpdate();
	        }
	        
	        return null;
	    });
	}

}
