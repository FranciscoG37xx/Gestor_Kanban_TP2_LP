/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.util.ArrayList;
import java.util.List;

public class Projeto {
    private static int contadorId = 0;

    private int id;
    private String nome;
    private List<Tarefa>  tarefas;
    private List<Membro>  membros;

    public Projeto(String nome) {
        this.id      = ++contadorId;
        this.nome    = nome;
        this.tarefas = new ArrayList<>();
        this.membros = new ArrayList<>();
    }

    // ── Tarefas ───────────────────────────────────────────────────────────────
    public Tarefa criaTarefa(String titulo, String descricao) {
        Tarefa t = new Tarefa(titulo, descricao);
        tarefas.add(t);
        return t;
    }

    public boolean removeTarefa(int id) {
        return tarefas.removeIf(t -> t.getId() == id);
    }

    public Tarefa procuraTarefaPorID(int id) {
        for (Tarefa t : tarefas) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public boolean alterarEstadoTarefa(int id, EstadoTarefa estado) {
        Tarefa t = procuraTarefaPorID(id);
        if (t != null) { t.setEstado(estado); return true; }
        return false;
    }

    public List<Tarefa> getTarefas() { return tarefas; }

    // ── Membros ───────────────────────────────────────────────────────────────
    public void adicionarMembro(Membro m) {
        membros.add(m);
    }

    public boolean removerMembro(String nome) {
        return membros.removeIf(m -> m.getNome().equalsIgnoreCase(nome));
    }

    public List<Membro> getMembros() { return membros; }

    // ── Getters/Setters ───────────────────────────────────────────────────────
    public int    getId()   { return id; }
    public String getNome() { return nome; }
    public void   setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() { return nome; }
}
