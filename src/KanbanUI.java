/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author franc
 */
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * KanbanUI - Interface gráfica principal da aplicação Kanban.
 *
 * Estrutura para integração com MVC:
 *   - KanbanController  → chamar métodos de controlo (ex: controller.criarTarefa(...))
 *   - KanbanModel       → receber dados (ex: model.getTarefasDaColuna(...))
 *
 * Substituir os comentários "TODO: integrar com Model/Controller"
 * pelos métodos reais quando for integrar com os ficheiros do António.
 */
public class KanbanUI extends JFrame {

    // ── cores ───────────────────────────────────────────────────────
    private static final Color BG          = new Color(15,  17,  23);   // fundo geral
    private static final Color SURFACE     = new Color(22,  26,  35);   // painéis
    private static final Color SURFACE2    = new Color(30,  35,  48);   // colunas
    private static final Color BORDER_COL  = new Color(45,  52,  70);   // bordas subtis
    private static final Color ACCENT      = new Color(99, 179, 237);   // azul-céu
    private static final Color ACCENT2     = new Color(154, 117, 234);  // violeta
    private static final Color SUCCESS     = new Color(72,  199, 142);  // verde
    private static final Color WARN        = new Color(250, 176,  5);   // amarelo
    private static final Color DANGER      = new Color(240,  62,  62);  // vermelho
    private static final Color TEXT_PRI    = new Color(237, 242, 247);  // texto principal
    private static final Color TEXT_SEC    = new Color(113, 128, 150);  // texto secundário
    private static final Color CARD_BG     = new Color(28,  33,  45);   // card background
    private static final Color TAG_BLUE    = new Color(20,  60, 100);
    private static final Color TAG_PURPLE  = new Color(60,  30,  90);
    private static final Color TAG_GREEN   = new Color(20,  70,  50);

    // ── Tipos de Letra ────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,   22);
    private static final Font FONT_COL     = new Font("Segoe UI", Font.BOLD,   13);
    private static final Font FONT_CARD    = new Font("Segoe UI", Font.PLAIN,  13);
    private static final Font FONT_CARD_B  = new Font("Segoe UI", Font.BOLD,   13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN,  11);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,   12);
    private static final Font FONT_TAG     = new Font("Segoe UI", Font.BOLD,   10);
    private static final Font FONT_COUNT   = new Font("Segoe UI", Font.BOLD,   11);

    // ── Colunas ─────────────────────────────────────────
    private JPanel colunaAFazer;
    private JPanel colunaEmProgresso;
    private JPanel colunaConcluido;

    // Contadores de tarefas por coluna
    private JLabel lblCountAFazer;
    private JLabel lblCountEmProgresso;
    private JLabel lblCountConcluido;

    // Label do projeto ativo (substituir pela lista real quando integrar)
    private JLabel lblProjetoAtivo;
    private String projetoAtivo = "Projeto Alpha";   // TODO: vem do Model

    // ── Construtor ────────────────────────────────────────────────────────────
    public KanbanUI() {
        configurarJanela();
        construirUI();
        carregarDadosIniciais(); // TODO: integrar com Model
        setVisible(true);
    }

    // ── Configuração da janela ────────────────────────────────────────────────
    private void configurarJanela() {
        setTitle("Kanban — Gestão de Projetos");
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));
    }

    // ── Construção principal ──────────────────────────────────────────────────
    private void construirUI() {
        add(construirTopBar(),  BorderLayout.NORTH);
        add(construirSidebar(), BorderLayout.WEST);
        add(construirBoard(),   BorderLayout.CENTER);
    }

    // =========================================================================
    // TOP BAR
    // =========================================================================
    private JPanel construirTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SURFACE);
        bar.setPreferredSize(new Dimension(0, 58));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));

        // Esquerda: logo + nome projeto
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        esquerda.setOpaque(false);

        JLabel logo = new JLabel("⬡ KanbanApp");
        logo.setFont(FONT_TITLE);
        logo.setForeground(ACCENT);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(BORDER_COL);

        lblProjetoAtivo = new JLabel(projetoAtivo);
        lblProjetoAtivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblProjetoAtivo.setForeground(TEXT_SEC);

        esquerda.add(logo);
        esquerda.add(sep);
        esquerda.add(lblProjetoAtivo);
        esquerda.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        // Centro: vertically centered
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        // Direita: botão guardar
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        JButton btnGuardar = criarBotaoIcon("💾 Guardar", SURFACE2, TEXT_PRI);
        btnGuardar.addActionListener(e -> guardarProjeto());
        direita.add(btnGuardar);
        direita.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        bar.add(esquerda, BorderLayout.WEST);
        bar.add(centro,   BorderLayout.CENTER);
        bar.add(direita,  BorderLayout.EAST);

        // Centrar verticalmente
        esquerda.setAlignmentY(Component.CENTER_ALIGNMENT);
        for (Component c : esquerda.getComponents()) {
            if (c instanceof JComponent) ((JComponent) c).setAlignmentY(Component.CENTER_ALIGNMENT);
        }
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(0, 0, 0, 0)
        ));

        return bar;
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================
    private JPanel construirSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(SURFACE);
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, BORDER_COL),
                new EmptyBorder(20, 14, 20, 14)
        ));

        // ---- Secção: Projetos ----
        sb.add(criarSidebarLabel("PROJETOS"));
        sb.add(Box.createVerticalStrut(8));

        // TODO: substituir por lista dinâmica do Model
        sb.add(criarItemProjeto("Projeto Alpha", true));
        sb.add(Box.createVerticalStrut(4));
        sb.add(criarItemProjeto("Projeto Beta",  false));
        sb.add(Box.createVerticalStrut(4));
        sb.add(criarItemProjeto("Projeto Gama",  false));
        sb.add(Box.createVerticalStrut(12));

        JButton btnNovoProjeto = criarBotaoSidebar("＋  Novo Projeto");
        btnNovoProjeto.addActionListener(e -> criarNovoProjeto());
        sb.add(btnNovoProjeto);
        sb.add(Box.createVerticalStrut(28));

        // ---- Secção: Tarefas ----
        sb.add(criarSidebarLabel("AÇÕES RÁPIDAS"));
        sb.add(Box.createVerticalStrut(8));

        JButton btnNovaTarefa = criarBotaoPrimary("＋  Nova Tarefa");
        btnNovaTarefa.addActionListener(e -> criarNovaTarefa());
        btnNovaTarefa.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(btnNovaTarefa);
        sb.add(Box.createVerticalStrut(8));

        JButton btnEquipa = criarBotaoSidebar("👥  Gerir Equipa");
        btnEquipa.addActionListener(e -> gerirEquipa());
        sb.add(btnEquipa);
        sb.add(Box.createVerticalStrut(8));

        sb.add(Box.createVerticalGlue());

        // ---- Rodapé sidebar ----
        JLabel ver = new JLabel("v1.0  ·  ESTGA / UA");
        ver.setFont(FONT_SMALL);
        ver.setForeground(new Color(60, 70, 90));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(ver);

        return sb;
    }

    private JLabel criarSidebarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton criarItemProjeto(String nome, boolean ativo) {
        JButton btn = new JButton(ativo ? "● " + nome : "○ " + nome);
        btn.setFont(FONT_CARD);
        btn.setForeground(ativo ? TEXT_PRI : TEXT_SEC);
        btn.setBackground(ativo ? SURFACE2 : SURFACE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (ativo) btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        else       btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        btn.addActionListener(e -> {
            projetoAtivo = nome;
            lblProjetoAtivo.setText(nome);
            // TODO: controller.selecionarProjeto(nome)
        });
        return btn;
    }

    // =========================================================================
    // BOARD (3 colunas)
    // =========================================================================
    private JPanel construirBoard() {
        JPanel board = new JPanel(new GridLayout(1, 3, 12, 0));
        board.setBackground(BG);
        board.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Criação das colunas
        JPanel[] cols = new JPanel[3];
        JLabel[] counts = new JLabel[3];

        String[] titulos = {"A Fazer", "Em Progresso", "Concluído"};
        Color[]  cores   = {ACCENT,    WARN,           SUCCESS};

        for (int i = 0; i < 3; i++) {
            cols[i]   = criarColunaConteudo();
            counts[i] = criarContador(cores[i]);
        }

        colunaAFazer       = cols[0];
        colunaEmProgresso  = cols[1];
        colunaConcluido    = cols[2];
        lblCountAFazer     = counts[0];
        lblCountEmProgresso= counts[1];
        lblCountConcluido  = counts[2];

        for (int i = 0; i < 3; i++) {
            board.add(criarWrapperColuna(titulos[i], cores[i], counts[i], cols[i]));
        }

        return board;
    }

    private JLabel criarContador(Color cor) {
        JLabel lbl = new JLabel("0");
        lbl.setFont(FONT_COUNT);
        lbl.setForeground(cor);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 30));
        lbl.setBorder(new EmptyBorder(1, 7, 1, 7));
        return lbl;
    }

    private JPanel criarColunaConteudo() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(SURFACE2);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        return p;
    }

    private JPanel criarWrapperColuna(String titulo, Color cor, JLabel contador, JPanel coluna) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(SURFACE2);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Header da coluna
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE2);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Barra de cor lateral
        JPanel stripe = new JPanel();
        stripe.setBackground(cor);
        stripe.setPreferredSize(new Dimension(3, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_COL);
        lblTitulo.setForeground(TEXT_PRI);

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerLeft.setOpaque(false);
        headerLeft.add(lblTitulo);
        headerLeft.add(contador);

        header.add(stripe,     BorderLayout.WEST);
        header.add(headerLeft, BorderLayout.CENTER);

        // Scroll
        JScrollPane scroll = new JScrollPane(coluna);
        scroll.setBorder(null);
        scroll.setBackground(SURFACE2);
        scroll.getViewport().setBackground(SURFACE2);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.getVerticalScrollBar().setBackground(SURFACE2);
        // Esconder scrollbar horizontal
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    // =========================================================================
    // CARD DE TAREFA
    // =========================================================================
    /**
     * Adiciona um card a uma coluna.
     *
     * @param coluna     Painel destino
     * @param titulo     Texto da tarefa
     * @param descricao  Descrição curta (pode ser null)
     * @param tag        Tag/etiqueta (pode ser null)
     * @param responsavel Membro atribuído (pode ser null)
     */
    public void adicionarTarefa(JPanel coluna, String titulo, String descricao,
                                 String tag, String responsavel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito hover
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(35, 42, 58));
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                card.repaint();
            }
        });

        // Topo: tag (opcional)
        if (tag != null && !tag.isEmpty()) {
            JLabel lblTag = criarTag(tag);
            card.add(lblTag, BorderLayout.NORTH);
        }

        // Centro: título + descrição
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_CARD_B);
        lblTitulo.setForeground(TEXT_PRI);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(lblTitulo);

        if (descricao != null && !descricao.isEmpty()) {
            JLabel lblDesc = new JLabel(descricao);
            lblDesc.setFont(FONT_SMALL);
            lblDesc.setForeground(TEXT_SEC);
            lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
            centro.add(Box.createVerticalStrut(3));
            centro.add(lblDesc);
        }

        card.add(centro, BorderLayout.CENTER);

        // Rodapé: responsável + botões
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);

        if (responsavel != null && !responsavel.isEmpty()) {
            JLabel lblResp = new JLabel("👤 " + responsavel);
            lblResp.setFont(FONT_SMALL);
            lblResp.setForeground(TEXT_SEC);
            rodape.add(lblResp, BorderLayout.WEST);
        }

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        acoes.setOpaque(false);

        JButton btnEditar = criarBotaoIconPequeno("✎", new Color(60, 80, 110), ACCENT);
        JButton btnMover  = criarBotaoIconPequeno("→", new Color(60, 80, 50), SUCCESS);
        JButton btnRemover= criarBotaoIconPequeno("✕", new Color(80, 30, 30), DANGER);

        btnMover.setToolTipText("Mover para a próxima coluna");
        btnEditar.setToolTipText("Editar tarefa");
        btnRemover.setToolTipText("Remover tarefa");

        btnMover.addActionListener(e -> moverTarefa(card));
        btnRemover.addActionListener(e -> removerTarefa(card));
        btnEditar.addActionListener(e -> editarTarefa(card, lblTitulo));

        acoes.add(btnEditar);
        acoes.add(btnMover);
        acoes.add(btnRemover);
        rodape.add(acoes, BorderLayout.EAST);

        card.add(rodape, BorderLayout.SOUTH);

        coluna.add(card);
        coluna.add(Box.createVerticalStrut(8));

        atualizarContadores();
        refreshUI();
    }

    private JLabel criarTag(String texto) {
        JLabel lbl = new JLabel(" " + texto.toUpperCase() + " ");
        lbl.setFont(FONT_TAG);
        // Cor da tag baseada no conteúdo
        if (texto.toLowerCase().contains("bug") || texto.toLowerCase().contains("erro")) {
            lbl.setForeground(DANGER); lbl.setBackground(new Color(80, 20, 20));
        } else if (texto.toLowerCase().contains("feature") || texto.toLowerCase().contains("ui")) {
            lbl.setForeground(ACCENT); lbl.setBackground(TAG_BLUE);
        } else if (texto.toLowerCase().contains("docs") || texto.toLowerCase().contains("test")) {
            lbl.setForeground(ACCENT2); lbl.setBackground(TAG_PURPLE);
        } else {
            lbl.setForeground(SUCCESS); lbl.setBackground(TAG_GREEN);
        }
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(2, 6, 2, 6));
        return lbl;
    }

    // =========================================================================
    // AÇÕES DE TAREFAS
    // =========================================================================
    private void moverTarefa(JPanel card) {
        Container parent = card.getParent();
        // Remover card + espaço a seguir
        int idx = indexDoCard(parent, card);
        parent.remove(card);
        if (idx >= 0 && idx < parent.getComponentCount()
                && parent.getComponent(idx) instanceof Box.Filler) {
            parent.remove(idx);
        }

        if (parent == colunaAFazer) {
            colunaEmProgresso.add(card);
            colunaEmProgresso.add(Box.createVerticalStrut(8));
        } else if (parent == colunaEmProgresso) {
            colunaConcluido.add(card);
            colunaConcluido.add(Box.createVerticalStrut(8));
        } else {
            colunaAFazer.add(card);
            colunaAFazer.add(Box.createVerticalStrut(8));
        }

        // TODO: controller.moverTarefa(tarefaId, novaColuna)
        atualizarContadores();
        refreshUI();
    }

    private void removerTarefa(JPanel card) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tens a certeza que queres remover esta tarefa?",
                "Remover Tarefa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            Container parent = card.getParent();
            int idx = indexDoCard(parent, card);
            parent.remove(card);
            if (idx >= 0 && idx < parent.getComponentCount()
                    && parent.getComponent(idx) instanceof Box.Filler) {
                parent.remove(idx);
            }
            // TODO: controller.removerTarefa(tarefaId)
            atualizarContadores();
            refreshUI();
        }
    }

    private void editarTarefa(JPanel card, JLabel lblTitulo) {
        String novoNome = (String) JOptionPane.showInputDialog(
                this,
                "Editar nome da tarefa:",
                "Editar Tarefa",
                JOptionPane.PLAIN_MESSAGE,
                null, null,
                lblTitulo.getText()
        );
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            lblTitulo.setText(novoNome.trim());
            // TODO: controller.editarTarefa(tarefaId, novoNome)
            refreshUI();
        }
    }

    private int indexDoCard(Container parent, JPanel card) {
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == card) return i;
        }
        return -1;
    }

    // =========================================================================
    // DIÁLOGOS
    // =========================================================================
    private void criarNovaTarefa() {
        JTextField campoNome  = new JTextField(20);
        JTextField campoDesc  = new JTextField(20);
        JTextField campoTag   = new JTextField(20);
        JTextField campoResp  = new JTextField(20);

        estilizarCampo(campoNome);
        estilizarCampo(campoDesc);
        estilizarCampo(campoTag);
        estilizarCampo(campoResp);

        JPanel painel = new JPanel(new GridLayout(0, 1, 6, 6));
        painel.setBackground(SURFACE);
        painel.setBorder(new EmptyBorder(8, 8, 8, 8));
        painel.add(criarLabelDialogo("Nome da tarefa *"));
        painel.add(campoNome);
        painel.add(criarLabelDialogo("Descrição (opcional)"));
        painel.add(campoDesc);
        painel.add(criarLabelDialogo("Tag (ex: UI, Bug, Feature)"));
        painel.add(campoTag);
        painel.add(criarLabelDialogo("Responsável (opcional)"));
        painel.add(campoResp);

        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("Panel.background", SURFACE);

        int result = JOptionPane.showConfirmDialog(
                this, painel, "Nova Tarefa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            if (!nome.isEmpty()) {
                adicionarTarefa(
                        colunaAFazer,
                        nome,
                        campoDesc.getText().trim(),
                        campoTag.getText().trim(),
                        campoResp.getText().trim()
                );
                // TODO: controller.criarTarefa(nome, desc, tag, responsavel)
            }
        }
    }

    private void criarNovoProjeto() {
        String nome = JOptionPane.showInputDialog(this, "Nome do novo projeto:", "Novo Projeto", JOptionPane.PLAIN_MESSAGE);
        if (nome != null && !nome.trim().isEmpty()) {
            projetoAtivo = nome.trim();
            lblProjetoAtivo.setText(projetoAtivo);
            // TODO: controller.criarProjeto(nome)
            JOptionPane.showMessageDialog(this, "Projeto \"" + projetoAtivo + "\" criado!", "Projeto Criado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void gerirEquipa() {
        // TODO: abrir diálogo de gestão de equipa quando integrar com Model
        JOptionPane.showMessageDialog(this,
                "Funcionalidade de Gestão de Equipa\n(Integração pendente com o Model)",
                "Gerir Equipa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarProjeto() {
        // TODO: controller.guardarProjeto()
        JOptionPane.showMessageDialog(this,
                "Projeto guardado com sucesso! ✔",
                "Guardar", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // HELPERS UI
    // =========================================================================
    private JLabel criarLabelDialogo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SEC);
        return lbl;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(SURFACE2);
        campo.setForeground(TEXT_PRI);
        campo.setCaretColor(ACCENT);
        campo.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL),
                new EmptyBorder(4, 8, 4, 8)
        ));
        campo.setFont(FONT_CARD);
    }

    private JButton criarBotaoPrimary(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BTN);
        btn.setBackground(ACCENT);
        btn.setForeground(new Color(10, 20, 35));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private JButton criarBotaoSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_CARD);
        btn.setBackground(SURFACE2);
        btn.setForeground(TEXT_PRI);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(6, 10, 6, 10));
        return btn;
    }

    private JButton criarBotaoIcon(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private JButton criarBotaoIconPequeno(String icone, Color bg, Color fg) {
        JButton btn = new JButton(icone);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(26, 22));
        btn.setBorder(new EmptyBorder(2, 4, 2, 4));
        return btn;
    }

    private void atualizarContadores() {
        lblCountAFazer.setText(String.valueOf(contarCards(colunaAFazer)));
        lblCountEmProgresso.setText(String.valueOf(contarCards(colunaEmProgresso)));
        lblCountConcluido.setText(String.valueOf(contarCards(colunaConcluido)));
    }

    private int contarCards(JPanel coluna) {
        int count = 0;
        for (Component c : coluna.getComponents()) {
            if (c instanceof JPanel) count++;
        }
        return count;
    }

    private void refreshUI() {
        revalidate();
        repaint();
    }

    // =========================================================================
    // DADOS INICIAIS DE DEMONSTRAÇÃO
    // TODO: substituir por carregamento do Model (ficheiro / base de dados)
    // =========================================================================
    private void carregarDadosIniciais() {
        adicionarTarefa(colunaAFazer,      "Definir estrutura MVC",   "Separar Model, View e Controller",   "Feature",  "Ana Silva");
        adicionarTarefa(colunaAFazer,      "Criar testes unitários",  "JUnit para classes do Model",         "Docs",     "");
        adicionarTarefa(colunaEmProgresso, "Interface KanbanUI",      "Swing com design profissional",       "UI",       "João Costa");
        adicionarTarefa(colunaEmProgresso, "IODataLibrary",           "Leitura e escrita em ficheiro .txt",  "Feature",  "Ana Silva");
        adicionarTarefa(colunaConcluido,   "Planeamento do projeto",  "Distribuição de tarefas na equipa",   "Docs",     "João Costa");
    }

    // =========================================================================
    // INTEGRAÇÃO COM MVC (métodos públicos para o Controller chamar)
    // =========================================================================

    /** Limpa todas as colunas e recarrega tarefas vindas do Model. */
    public void recarregarBoard(List<Object[]> tarefas) {
        colunaAFazer.removeAll();
        colunaEmProgresso.removeAll();
        colunaConcluido.removeAll();
        for (Object[] t : tarefas) {
            // t[0]=coluna(0,1,2), t[1]=titulo, t[2]=descricao, t[3]=tag, t[4]=responsavel
            JPanel destino = switch ((int) t[0]) {
                case 1  -> colunaEmProgresso;
                case 2  -> colunaConcluido;
                default -> colunaAFazer;
            };
            adicionarTarefa(destino,
                    (String) t[1],
                    t[2] != null ? (String) t[2] : "",
                    t[3] != null ? (String) t[3] : "",
                    t[4] != null ? (String) t[4] : "");
        }
        atualizarContadores();
        refreshUI();
    }

    /** Atualiza o nome do projeto exibido na top bar. */
    public void setProjetoAtivo(String nome) {
        projetoAtivo = nome;
        lblProjetoAtivo.setText(nome);
    }
}
