/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author franc
 */
import java.io.*;
import java.util.*;

/**
 * PersistenciaKanban
 *
 * Guarda e carrega o estado completo da aplicação num ficheiro de texto.
 * Formato do ficheiro:
 *
 *   PROJETO|<nome>
 *   MEMBRO|<nome>|<papel>
 *   TAREFA|<titulo>|<descricao>|<responsavel>|<estado>
 *   PROJETO|...
 *   ...
 *
 * Compatível com a lógica da IODataLibrary (leitura linha a linha,
 * separador "|"). Quando o professor disponibilizar o .jar, basta
 * substituir as chamadas BufferedReader/Writer pelas da IODataLibrary.
 */
public class PersistenciaKanban {

    private static final String FICHEIRO = "kanban_data.txt";
    private static final String SEP      = "|";

    // ── Guardar ───────────────────────────────────────────────────────────────
    public static void guardar(QuadroKanban quadro) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FICHEIRO))) {
            for (Projeto p : quadro.getProjetos()) {
                bw.write("PROJETO" + SEP + p.getNome());
                bw.newLine();

                for (Membro m : p.getMembros()) {
                    bw.write("MEMBRO" + SEP + m.getNome() + SEP + m.getPapel());
                    bw.newLine();
                }

                for (Tarefa t : p.getTarefas()) {
                    bw.write("TAREFA"
                            + SEP + t.getTitulo()
                            + SEP + t.getDescricao()
                            + SEP + t.getResponsavel()
                            + SEP + t.getEstado().name());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar: " + e.getMessage());
        }
    }

    // ── Carregar ──────────────────────────────────────────────────────────────
    public static void carregar(QuadroKanban quadro) {
        File f = new File(FICHEIRO);
        if (!f.exists()) return;

        // Limpar estado atual
        quadro.getProjetos().clear();

        Projeto projetoAtual = null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] partes = linha.split("\\" + SEP, -1);

                switch (partes[0]) {
                    case "PROJETO" -> {
                        projetoAtual = quadro.criarProjeto(partes[1]);
                    }
                    case "MEMBRO" -> {
                        if (projetoAtual != null && partes.length >= 3) {
                            projetoAtual.adicionarMembro(new Membro(partes[1], partes[2]));
                        }
                    }
                    case "TAREFA" -> {
                        if (projetoAtual != null && partes.length >= 5) {
                            Tarefa t = projetoAtual.criaTarefa(partes[1], partes[2]);
                            t.setResponsavel(partes[3]);
                            t.setEstado(EstadoTarefa.valueOf(partes[4]));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar: " + e.getMessage());
        }

        // Definir o primeiro projeto como ativo
        if (!quadro.getProjetos().isEmpty()) {
            quadro.setProjetoAtivo(quadro.getProjetos().get(0));
        }
    }

    public static boolean ficheirExiste() {
        return new File(FICHEIRO).exists();
    }
}
