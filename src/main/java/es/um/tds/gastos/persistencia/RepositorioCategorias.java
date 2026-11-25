package es.um.tds.gastos.persistencia;

import java.util.List;
import es.um.tds.gastos.modelo.Categoria;

public interface RepositorioCategorias {
    List<Categoria> listar();
    void agregar(Categoria elemento);
    void modificar(Categoria elemento);
    void eliminar(Categoria elemento);
}
