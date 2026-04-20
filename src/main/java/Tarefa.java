public class Tarefa {
    private int id;
    private static int contadorId = 0;
    private String titulo;
    private String descricao;
    private String responsavel;
    private EstadoTarefa estado;

    public Tarefa(String titulo, String descricao) {
        this.id          = ++contadorId;
        this.titulo      = titulo;
        this.descricao   = descricao;
        this.responsavel = "";
        this.estado      = EstadoTarefa.POR_FAZER;
    }

    public int          getId()           { return id; }
    public String       getTitulo()       { return titulo; }
    public String       getDescricao()    { return descricao; }
    public String       getResponsavel()  { return responsavel; }
    public EstadoTarefa getEstado()       { return estado; }

    public void setTitulo(String titulo)           { this.titulo = titulo; }
    public void setDescricao(String descricao)     { this.descricao = descricao; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public void setEstado(EstadoTarefa estado)     { this.estado = estado; }

    // Usado pelo ID interno ao carregar de ficheiro
    public static void setContadorId(int val) { contadorId = val; }
}
