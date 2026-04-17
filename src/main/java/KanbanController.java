public class KanbanController {

    private QuadroKanban model;

    public KanbanController(QuadroKanban model) {
        this.model = model;
    }

    public void criaTarefa(String titulo, String descricao) {
        model.criaTarefa(titulo, descricao);
    }

    public void removeTarefa(int id) {
        model.removeTarefa(id);
    }

    public Tarefa procuraTarefaPorID(int id) {
        return model.procuraTarefaPorID(id);
    }

    public boolean alterarEstadoTarefa(int id, EstadoTarefa novoEstado) {
        return model.alterarEstadoTarefa(id, novoEstado);
    }

    public void listaTarefas() {
        model.listaTarefas();
    }

    public void listaTarefasPorEstado(EstadoTarefa estado) {
        model.listaTarefasPorEstado(estado);
    }

}