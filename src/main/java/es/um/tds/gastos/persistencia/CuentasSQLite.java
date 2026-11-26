package es.um.tds.gastos.persistencia;

import es.um.tds.gastos.modelo.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class CuentasSQLite implements RepositorioCuentas {
    private final BaseDatos base;

    public CuentasSQLite(BaseDatos base) {
        this.base = base;
    }

    public void agregar(CuentaCompartida cuenta) {
        base.ejecutar(() -> base.conConexion(con -> {
            try (var alta = con.prepareStatement("INSERT INTO cuentas (id,nombre,reparto) VALUES (?,?,?)");
                 var integrante = con.prepareStatement(
                     "INSERT INTO integrantes (cuenta_id,nombre,peso,posicion) VALUES (?,?,?,?)")) {
                String id = cuenta.id().toString();
                alta.setString(1, id);
                alta.setString(2, cuenta.nombre());
                alta.setString(3, cuenta.reparto().name());
                alta.executeUpdate();
                int posicion = 0;
                for (var entrada : cuenta.integrantes().entrySet()) {
                    integrante.setString(1, id);
                    integrante.setString(2, entrada.getKey());
                    integrante.setString(3, entrada.getValue().toPlainString());
                    integrante.setInt(4, posicion++);
                    integrante.addBatch();
                }
                integrante.executeBatch();
            }
            return null;
        }));
    }

    public List<CuentaCompartida> listar() {
        return base.conConexion(con -> {
            var cuentas = new ArrayList<CuentaCompartida>();
            try (var consulta = con.prepareStatement("SELECT id,nombre,reparto FROM cuentas ORDER BY rowid");
                 var filas = consulta.executeQuery()) {
                while (filas.next()) {
                    String id = filas.getString("id");
                    cuentas.add(new CuentaCompartida(UUID.fromString(id), filas.getString("nombre"),
                        Reparto.valueOf(filas.getString("reparto")), integrantes(con, id)));
                }
            }
            return cuentas;
        });
    }

    private Map<String, BigDecimal> integrantes(Connection con, String id) throws SQLException {
        var integrantes = new LinkedHashMap<String, BigDecimal>();
        try (var consulta = con.prepareStatement(
                "SELECT nombre,peso FROM integrantes WHERE cuenta_id=? ORDER BY posicion")) {
            consulta.setString(1, id);
            try (var filas = consulta.executeQuery()) {
                while (filas.next()) {
                    integrantes.put(filas.getString("nombre"), new BigDecimal(filas.getString("peso")));
                }
            }
        }
        return integrantes;
    }

    public void guardarPago(UUID cuenta, PagoCompartido pago) {
        base.conConexion(con -> {
            try (var alta = con.prepareStatement("INSERT INTO pagos (cuenta_id,pagador,importe) VALUES (?,?,?)")) {
                alta.setString(1, cuenta.toString());
                alta.setString(2, pago.pagador());
                alta.setString(3, pago.importe().toPlainString());
                alta.executeUpdate();
            }
            return null;
        });
    }

    public List<PagoCompartido> pagos(UUID cuenta) {
        return base.conConexion(con -> {
            var pagos = new ArrayList<PagoCompartido>();
            try (var consulta = con.prepareStatement(
                    "SELECT pagador,importe FROM pagos WHERE cuenta_id=? ORDER BY id")) {
                consulta.setString(1, cuenta.toString());
                try (var filas = consulta.executeQuery()) {
                    while (filas.next()) {
                        pagos.add(new PagoCompartido(filas.getString("pagador"), new BigDecimal(filas.getString("importe"))));
                    }
                }
            }
            return pagos;
        });
    }
}
