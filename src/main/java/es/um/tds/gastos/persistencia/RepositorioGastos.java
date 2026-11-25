package es.um.tds.gastos.persistencia;

import java.util.List;
import es.um.tds.gastos.modelo.Gasto;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioGastos {
    List<Gasto> listar();
    void agregar(Gasto elemento);
    void modificar(Gasto elemento);
    void eliminar(Gasto elemento);
    Optional<Gasto> buscarPorId(UUID id);
}
