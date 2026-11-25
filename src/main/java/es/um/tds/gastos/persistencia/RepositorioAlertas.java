package es.um.tds.gastos.persistencia;

import java.util.List;
import es.um.tds.gastos.modelo.Alerta;
import es.um.tds.gastos.modelo.Notificacion;

public interface RepositorioAlertas {
    void agregar(Alerta alerta);
    List<Alerta> listar();
    List<Notificacion> historial();
}
