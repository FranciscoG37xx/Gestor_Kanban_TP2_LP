import java.util.ArrayList;
import java.util.List;

public class QuadroKanban {

    private List<Tarefa> tarefas;

    public QuadroKanban() {
        this.tarefas = new ArrayList<>();
    }

    //Cria nova tarefa
    public void criaTarefa(String titulo, String descricao) {
        Tarefa nova = new Tarefa(titulo, descricao);
        tarefas.add(nova);
    }

    //remove tarefa criada
    public void removeTarefa(int id) {
        if (tarefas.isEmpty()) {
            System.out.println("Sem tarefas no quadro.");
            return;
        }

        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).getId() == id) {
                tarefas.remove(i);
                return;
            }
        }

        System.out.println("Tarefa não encontrada");
    }

    //procura tarefa por id
    public Tarefa procuraTarefaPorID(int id) {
        for (Tarefa t : tarefas) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    //altera estado da tarefa
    public boolean alterarEstadoTarefa(int id, EstadoTarefa novoEstado) {
        for (Tarefa t : tarefas) {
            if (t.getId() == id) {
                t.setEstado(novoEstado);
                return true;
            }
        }
        System.out.println("Tarefa não encontrada. ID inexistente");
        return false;
    }

    // lista tarefas através de id
    public void listaTarefas() {

        if (tarefas.isEmpty()) {
            System.out.println("Sem tarefas no quadro.");
            return;
        }

        for (Tarefa t : tarefas) {
            System.out.println("ID: " + t.getId() + " | Título: " + t.getTitulo() + " | Estado: " + t.getEstado());
        }
    }

    // lista tarefas por estado
    public void listaTarefasPorEstado(EstadoTarefa estado) {
        boolean encontrou = false;

        for (Tarefa t : tarefas) {
            if (t.getEstado() == estado) {
                System.out.println("ID: " + t.getId() + " | Título: " + t.getTitulo() + " | Estado: " + t.getEstado());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Não existem tarefas com esse estado.");
        }
    }

}
