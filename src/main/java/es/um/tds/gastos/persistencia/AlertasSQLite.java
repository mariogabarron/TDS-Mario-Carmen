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

    public void agregar(Alerta alerta) {
        try (var con = base.abrir(); var orden = con.prepareStatement(
                "INSERT INTO alertas(id, limite, periodo, categoria_id) VALUES (?, ?, ?, ?)")) {
            orden.setString(1, alerta.id().toString());
            orden.setString(2, alerta.limite().toPlainString());
            orden.setString(3, alerta.periodo().name());
            if (alerta.categoria() == null) {
                orden.setNull(4, Types.VARCHAR);
            } else {
                orden.setString(4, alerta.categoria().getId().toString());
            }
            orden.executeUpdate();
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
    }

    public List<Alerta> listar() {
        try (var con = base.abrir()) {
            return listar(con);
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
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
                    PeriodoAlerta.valueOf(filas.getString("periodo")), categoria));
            }
        }
        return List.copyOf(alertas);
    }

    static void evaluar(Connection con, Gasto actual, Gasto anterior) throws SQLException {
        for (Alerta alerta : listar(con)) {
            if (!alerta.corresponde(actual)) {
                continue;
            }
            LocalDate inicio = alerta.periodo().inicio(actual.getFecha());
            LocalDate fin = alerta.periodo().fin(actual.getFecha());
            BigDecimal total = sumar(con, alerta, inicio, fin);
            BigDecimal antes = total.subtract(actual.getImporte());
            if (anterior != null && alerta.corresponde(anterior)
                    && !anterior.getFecha().isBefore(inicio) && !anterior.getFecha().isAfter(fin)) {
                antes = antes.add(anterior.getImporte());
            }
            if (antes.compareTo(alerta.limite()) <= 0 && total.compareTo(alerta.limite()) > 0) {
                guardarAviso(con, alerta, inicio, fin, total);
            }
        }
    }

    private static BigDecimal sumar(Connection con, Alerta alerta, LocalDate inicio, LocalDate fin) throws SQLException {
        String sql = "SELECT importe FROM gastos WHERE fecha BETWEEN ? AND ?";
        if (alerta.categoria() != null) {
            sql += " AND categoria_id=?";
        }
        BigDecimal total = BigDecimal.ZERO;
        try (var orden = con.prepareStatement(sql)) {
            orden.setString(1, inicio.toString());
            orden.setString(2, fin.toString());
            if (alerta.categoria() != null) {
                orden.setString(3, alerta.categoria().getId().toString());
            }
            try (var filas = orden.executeQuery()) {
                while (filas.next()) {
                    total = total.add(new BigDecimal(filas.getString(1)));
                }
            }
        }
        return total;
    }

    private static void guardarAviso(Connection con, Alerta alerta, LocalDate inicio, LocalDate fin,
                                    BigDecimal total) throws SQLException {
        String sql = "INSERT INTO notificaciones(alerta_id, creada, inicio, fin, ambito, limite, total) VALUES(?,?,?,?,?,?,?)";
        try (var orden = con.prepareStatement(sql)) {
            orden.setString(1, alerta.id().toString());
            orden.setString(2, LocalDateTime.now().toString());
            orden.setString(3, inicio.toString());
            orden.setString(4, fin.toString());
            orden.setString(5, alerta.ambito());
            orden.setString(6, alerta.limite().toPlainString());
            orden.setString(7, total.toPlainString());
            orden.executeUpdate();
        }
    }

    public List<Notificacion> historial() {
        var avisos = new ArrayList<Notificacion>();
        try (var con = base.abrir(); var orden = con.createStatement();
             var filas = orden.executeQuery("SELECT creada,inicio,fin,ambito,limite,total FROM notificaciones ORDER BY id DESC")) {
            while (filas.next()) {
                avisos.add(new Notificacion(LocalDateTime.parse(filas.getString(1)), LocalDate.parse(filas.getString(2)),
                    LocalDate.parse(filas.getString(3)), filas.getString(4), new BigDecimal(filas.getString(5)),
                    new BigDecimal(filas.getString(6))));
            }
        } catch (SQLException e) {
            throw BaseDatos.error(e);
        }
        return List.copyOf(avisos);
    }
}
