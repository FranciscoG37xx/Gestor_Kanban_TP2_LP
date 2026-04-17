public class Main {
    static void main(String[] args) {

        QuadroKanban quadro = new QuadroKanban();

        quadro.criaTarefa("TP-1", "Biblioteca RandomGroupGeneratorLibrary");
        quadro.criaTarefa("TP-2", "Gestor Kanban para Projetos" );
        quadro.criaTarefa("TP-3", "Por anunciar");
        quadro.criaTarefa("Teste LP", "Teste prático");
        quadro.listaTarefas();

        System.out.println();
        quadro.removeTarefa(4);

        quadro.alterarEstadoTarefa(1, EstadoTarefa.FINALIZADA);
        quadro.alterarEstadoTarefa(2, EstadoTarefa.EM_PROGRESSO);

        quadro.listaTarefas();

        System.out.println();
        quadro.listaTarefasPorEstado(EstadoTarefa.FINALIZADA);

        quadro.procuraTarefaPorID(1);


    }
}