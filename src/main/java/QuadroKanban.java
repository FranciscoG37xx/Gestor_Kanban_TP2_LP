import java.util.ArrayList;
import java.util.List;

public class QuadroKanban {
    private List<Projeto> projetos;
    private Projeto projetoAtivo;

    public QuadroKanban() {
        this.projetos = new ArrayList<>();
    }

    // ── Projetos ──────────────────────────────────────────────────────────────
    public Projeto criarProjeto(String nome) {
        Projeto p = new Projeto(nome);
        projetos.add(p);
        if (projetoAtivo == null) projetoAtivo = p;
        return p;
    }

    public boolean editarProjeto(int id, String novoNome) {
        Projeto p = procurarProjetoPorId(id);
        if (p != null) { p.setNome(novoNome); return true; }
        return false;
    }

    public boolean eliminarProjeto(int id) {
        boolean removido = projetos.removeIf(p -> p.getId() == id);
        if (removido && projetoAtivo != null && projetoAtivo.getId() == id) {
            projetoAtivo = projetos.isEmpty() ? null : projetos.get(0);
        }
        return removido;
    }

    public Projeto procurarProjetoPorId(int id) {
        for (Projeto p : projetos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public List<Projeto> getProjetos() { return projetos; }

    public Projeto getProjetoAtivo()          { return projetoAtivo; }
    public void    setProjetoAtivo(Projeto p) { this.projetoAtivo = p; }

    // ── Tarefas (delegam para o projeto ativo) ────────────────────────────────
    public Tarefa criaTarefa(String titulo, String descricao) {
        if (projetoAtivo == null) return null;
        return projetoAtivo.criaTarefa(titulo, descricao);
    }

    public void removeTarefa(int id) {
        if (projetoAtivo != null) projetoAtivo.removeTarefa(id);
    }

    public Tarefa procuraTarefaPorID(int id) {
        if (projetoAtivo == null) return null;
        return projetoAtivo.procuraTarefaPorID(id);
    }

    public boolean alterarEstadoTarefa(int id, EstadoTarefa novoEstado) {
        if (projetoAtivo == null) return false;
        return projetoAtivo.alterarEstadoTarefa(id, novoEstado);
    }

    public void listaTarefas() {
        if (projetoAtivo == null) { System.out.println("Sem projeto ativo."); return; }
        for (Tarefa t : projetoAtivo.getTarefas()) {
            System.out.println("ID: " + t.getId() + " | " + t.getTitulo() + " | " + t.getEstado());
        }
    }

    public void listaTarefasPorEstado(EstadoTarefa estado) {
        if (projetoAtivo == null) return;
        projetoAtivo.getTarefas().stream()
                .filter(t -> t.getEstado() == estado)
                .forEach(t -> System.out.println("ID: " + t.getId() + " | " + t.getTitulo()));
    }
}
