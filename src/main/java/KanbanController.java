import java.util.List;

public class KanbanController {
    private QuadroKanban model;

    public KanbanController(QuadroKanban model) {
        this.model = model;
    }

    // ── Projetos ──────────────────────────────────────────────────────────────
    public Projeto criarProjeto(String nome) {
        return model.criarProjeto(nome);
    }

    public boolean editarProjeto(int id, String novoNome) {
        return model.editarProjeto(id, novoNome);
    }

    public boolean eliminarProjeto(int id) {
        return model.eliminarProjeto(id);
    }

    public List<Projeto> getProjetos() {
        return model.getProjetos();
    }

    public Projeto getProjetoAtivo() {
        return model.getProjetoAtivo();
    }

    public void setProjetoAtivo(Projeto p) {
        model.setProjetoAtivo(p);
    }

    // ── Tarefas ───────────────────────────────────────────────────────────────
    public Tarefa criaTarefa(String titulo, String descricao) {
        return model.criaTarefa(titulo, descricao);
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

    public List<Tarefa> getTarefasDoProjetoAtivo() {
        Projeto p = model.getProjetoAtivo();
        return p == null ? List.of() : p.getTarefas();
    }

    // ── Membros ───────────────────────────────────────────────────────────────
    public void adicionarMembro(String nome, String papel) {
        Projeto p = model.getProjetoAtivo();
        if (p != null) p.adicionarMembro(new Membro(nome, papel));
    }

    public boolean removerMembro(String nome) {
        Projeto p = model.getProjetoAtivo();
        return p != null && p.removerMembro(nome);
    }

    public List<Membro> getMembrosDoProjetoAtivo() {
        Projeto p = model.getProjetoAtivo();
        return p == null ? List.of() : p.getMembros();
    }

    // ── Persistência ──────────────────────────────────────────────────────────
    public void guardarProjetos() {
        PersistenciaKanban.guardar(model);
    }

    public void carregarProjetos() {
        PersistenciaKanban.carregar(model);
    }

    public boolean existemDadosGuardados() {
        return PersistenciaKanban.ficheirExiste();
    }
}