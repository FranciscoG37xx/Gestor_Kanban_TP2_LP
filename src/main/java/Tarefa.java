public class Tarefa {

    private int id;
    private static int contadorId;
    private String titulo;
    private String descricao;
    private EstadoTarefa estado;

    public Tarefa(String titulo, String descricao) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.estado = EstadoTarefa.POR_FAZER;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public EstadoTarefa getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarefa estado) {
        this.estado = estado;
    }
}
