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
import java.util.List;

public class KanbanUI extends JFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color BG           = new Color(15,  17,  23);
    private static final Color SURFACE      = new Color(22,  26,  35);
    private static final Color SURFACE2     = new Color(30,  35,  48);
    private static final Color BORDER_COL   = new Color(45,  52,  70);
    private static final Color ACCENT       = new Color(99, 179, 237);
    private static final Color SUCCESS      = new Color(72,  199, 142);
    private static final Color WARN         = new Color(250, 176,   5);
    private static final Color DANGER       = new Color(240,  62,  62);
    private static final Color TEXT_PRI     = new Color(237, 242, 247);
    private static final Color TEXT_SEC     = new Color(113, 128, 150);
    private static final Color CARD_BG      = new Color(28,  33,  45);
    private static final Color TAG_BLUE     = new Color(20,  60, 100);
    private static final Color TAG_GREEN    = new Color(20,  70,  50);

    // ── Tipografia ────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_COL    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_CARD   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CARD_B = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_TAG    = new Font("Segoe UI", Font.BOLD,  10);
    private static final Font FONT_COUNT  = new Font("Segoe UI", Font.BOLD,  11);

    // ── Controller ────────────────────────────────────────────────────────────
    private final KanbanController controller;

    // ── Colunas ───────────────────────────────────────────────────────────────
    private JPanel colunaAFazer;
    private JPanel colunaEmProgresso;
    private JPanel colunaConcluido;

    private JLabel lblCountAFazer;
    private JLabel lblCountEmProgresso;
    private JLabel lblCountConcluido;

    private JLabel lblProjetoAtivo;
    private String projetoAtivo = "Projeto Alpha";

    // Referência ao botão de projeto atualmente selecionado
    private JButton btnProjetoAtualSelecionado;

    // Dados de demo por projeto: cada entrada é { titulo, descricao, responsavel, coluna(0/1/2) }
    private static final Object[][][] DEMO = {
        // Projeto Alpha
        {
            {"Definir estrutura MVC",    "Separar Model, View e Controller",      "Ana Silva",   0},
            {"Criar testes unitários",   "JUnit para as classes do Model",         "Pedro Nunes", 0},
            {"Interface KanbanUI",       "Swing com dark theme profissional",      "Joao Costa",  1},
            {"IODataLibrary",            "Leitura e escrita em ficheiro .txt",     "Ana Silva",   1},
            {"Planeamento do projeto",   "Distribuicao de tarefas e prazos",       "Joao Costa",  2},
            {"Diagrama de classes UML",  "Modelacao das entidades principais",     "Pedro Nunes", 2},
        },
        // Projeto Beta
        {
            {"Modulo de autenticacao",   "Login e registo de utilizadores",        "Maria Sousa", 0},
            {"Base de dados SQLite",     "Configurar schema e conexao",            "Rui Faria",   0},
            {"API REST endpoints",       "CRUD para recursos principais",          "Maria Sousa", 1},
            {"Dashboard de metricas",    "Graficos e estatisticas em tempo real",  "Rui Faria",   2},
            {"Documentacao tecnica",     "Swagger e README completo",              "Maria Sousa", 2},
        },
        // Projeto Gama
        {
            {"Wireframes UI/UX",         "Mockups de todas as ecras da app",       "Ines Lopes",  0},
            {"Notificacoes push",        "Integrar Firebase Cloud Messaging",      "Tiago Reis",  0},
            {"Testes de integracao",     "Cenarios end-to-end com Selenium",       "Ines Lopes",  1},
            {"Otimizacao de queries",    "Indices e cache para queries lentas",    "Tiago Reis",  1},
            {"Deploy em producao",       "Pipeline CI/CD com GitHub Actions",      "Ines Lopes",  2},
        }
    };
    private static final String[] NOMES_PROJETOS = {"Projeto Alpha", "Projeto Beta", "Projeto Gama"};

    // ── Construtor ────────────────────────────────────────────────────────────
    public KanbanUI(KanbanController controller) {
        this.controller = controller;
        configurarJanela();
        construirUI();
        carregarDemoParaProjeto(0); // começa no Projeto Alpha
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Kanban — Gestao de Projetos");
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));
    }

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
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(0, 0, 0, 0)
        ));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        esquerda.setOpaque(false);
        esquerda.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        // Sem icone — apenas texto
        JLabel logo = new JLabel("KanbanApp");
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

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        direita.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        JButton btnGuardar = criarBotaoIcon("Guardar", SURFACE2, TEXT_PRI);
        btnGuardar.addActionListener(e -> guardarProjeto());
        direita.add(btnGuardar);

        bar.add(esquerda, BorderLayout.WEST);
        bar.add(direita,  BorderLayout.EAST);
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

        sb.add(criarSidebarLabel("PROJETOS"));
        sb.add(Box.createVerticalStrut(8));

        JButton btnAlpha = criarItemProjeto("Projeto Alpha", true,  0);
        JButton btnBeta  = criarItemProjeto("Projeto Beta",  false, 1);
        JButton btnGama  = criarItemProjeto("Projeto Gama",  false, 2);
        btnProjetoAtualSelecionado = btnAlpha;

        sb.add(btnAlpha);
        sb.add(Box.createVerticalStrut(4));
        sb.add(btnBeta);
        sb.add(Box.createVerticalStrut(4));
        sb.add(btnGama);
        sb.add(Box.createVerticalStrut(12));

        JButton btnNovoProjeto = criarBotaoSidebar("Novo Projeto");
        btnNovoProjeto.addActionListener(e -> criarNovoProjeto());
        sb.add(btnNovoProjeto);
        sb.add(Box.createVerticalStrut(28));

        sb.add(criarSidebarLabel("ACOES RAPIDAS"));
        sb.add(Box.createVerticalStrut(8));

        JButton btnNovaTarefa = criarBotaoPrimary("Nova Tarefa");
        btnNovaTarefa.addActionListener(e -> criarNovaTarefa());
        btnNovaTarefa.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(btnNovaTarefa);
        sb.add(Box.createVerticalStrut(8));

        JButton btnEquipa = criarBotaoSidebar("Gerir Equipa");
        btnEquipa.addActionListener(e -> gerirEquipa());
        sb.add(btnEquipa);

        sb.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("v1.0  -  ESTGA / UA");
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

    private JButton criarItemProjeto(String nome, boolean ativo, int indiceDemo) {
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
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        btn.addActionListener(e -> {
            selecionarProjeto(btn, nome);
            carregarDemoParaProjeto(indiceDemo);
        });
        return btn;
    }

    private void selecionarProjeto(JButton btnClicado, String nome) {
        if (btnProjetoAtualSelecionado != null && btnProjetoAtualSelecionado != btnClicado) {
            String nomeAntigo = btnProjetoAtualSelecionado.getText().substring(2);
            btnProjetoAtualSelecionado.setText("○ " + nomeAntigo);
            btnProjetoAtualSelecionado.setForeground(TEXT_SEC);
            btnProjetoAtualSelecionado.setBackground(SURFACE);
        }
        btnClicado.setText("● " + nome);
        btnClicado.setForeground(TEXT_PRI);
        btnClicado.setBackground(SURFACE2);
        btnProjetoAtualSelecionado = btnClicado;

        projetoAtivo = nome;
        lblProjetoAtivo.setText(nome);
    }

    // =========================================================================
    // BOARD
    // =========================================================================
    private JPanel construirBoard() {
        JPanel board = new JPanel(new GridLayout(1, 3, 12, 0));
        board.setBackground(BG);
        board.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] titulos = {"A Fazer",  "Em Progresso", "Concluido"};
        Color[]  cores   = {ACCENT,      WARN,           SUCCESS};

        JPanel[] cols   = new JPanel[3];
        JLabel[] counts = new JLabel[3];

        for (int i = 0; i < 3; i++) {
            cols[i]   = criarColunaConteudo();
            counts[i] = criarContador(cores[i]);
            board.add(criarWrapperColuna(titulos[i], cores[i], counts[i], cols[i]));
        }

        colunaAFazer        = cols[0];
        colunaEmProgresso   = cols[1];
        colunaConcluido     = cols[2];
        lblCountAFazer      = counts[0];
        lblCountEmProgresso = counts[1];
        lblCountConcluido   = counts[2];

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
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(SURFACE2);
        wrapper.setBorder(new LineBorder(BORDER_COL, 1, true));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE2);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(12, 14, 12, 14)
        ));

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

        JScrollPane scroll = new JScrollPane(coluna);
        scroll.setBorder(null);
        scroll.setBackground(SURFACE2);
        scroll.getViewport().setBackground(SURFACE2);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // =========================================================================
    // CARD DE TAREFA
    // =========================================================================
    private void adicionarCardNaUI(JPanel coluna, Tarefa tarefa) {
        adicionarCardNaUI(coluna, tarefa, "");
    }

    private void adicionarCardNaUI(JPanel coluna, Tarefa tarefa, String responsavel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(12, 14, 10, 14)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.putClientProperty("tarefaId", tarefa.getId());

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(35, 42, 58)); card.repaint(); }
            public void mouseExited(MouseEvent e)  { card.setBackground(CARD_BG);               card.repaint(); }
        });

        JLabel lblTag = criarTagEstado(tarefa.getEstado());
        card.add(lblTag, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JLabel lblTitulo = new JLabel(tarefa.getTitulo());
        lblTitulo.setFont(FONT_CARD_B);
        lblTitulo.setForeground(TEXT_PRI);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(lblTitulo);

        if (tarefa.getDescricao() != null && !tarefa.getDescricao().isEmpty()) {
            JTextArea areaDesc = new JTextArea(tarefa.getDescricao());
            areaDesc.setFont(FONT_SMALL);
            areaDesc.setForeground(TEXT_SEC);
            areaDesc.setBackground(CARD_BG);
            areaDesc.setEditable(false);
            areaDesc.setFocusable(false);
            areaDesc.setLineWrap(true);
            areaDesc.setWrapStyleWord(true);
            areaDesc.setOpaque(false);
            areaDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
            centro.add(Box.createVerticalStrut(4));
            centro.add(areaDesc);
        }

        card.add(centro, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel infoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        infoLeft.setOpaque(false);

        JLabel lblId = new JLabel("ID: " + tarefa.getId());
        lblId.setFont(FONT_SMALL);
        lblId.setForeground(new Color(70, 85, 110));
        infoLeft.add(lblId);

        if (responsavel != null && !responsavel.isEmpty()) {
            JLabel lblResp = new JLabel(responsavel);
            lblResp.setFont(FONT_SMALL);
            lblResp.setForeground(TEXT_SEC);
            infoLeft.add(lblResp);
        }
        rodape.add(infoLeft, BorderLayout.WEST);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        acoes.setOpaque(false);

        JButton btnEditar  = criarBotaoIconPequeno("Ed", new Color(60, 80, 110), ACCENT);
        JButton btnMover   = criarBotaoIconPequeno(">>", new Color(40, 70, 40),  SUCCESS);
        JButton btnRemover = criarBotaoIconPequeno("X",  new Color(80, 30, 30),  DANGER);

        btnEditar.setToolTipText("Editar tarefa");
        btnMover.setToolTipText("Mover para a proxima coluna");
        btnRemover.setToolTipText("Remover tarefa");

        btnEditar.addActionListener(e  -> editarTarefa(card, lblTitulo, lblTag));
        btnMover.addActionListener(e   -> moverTarefa(card, lblTag));
        btnRemover.addActionListener(e -> removerTarefa(card));

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

    private JLabel criarTagEstado(EstadoTarefa estado) {
        String texto;
        Color fg, bg;
        switch (estado) {
            case EM_PROGRESSO -> { texto = "EM PROGRESSO"; fg = WARN;    bg = new Color(80, 60, 10); }
            case FINALIZADA   -> { texto = "CONCLUIDA";    fg = SUCCESS; bg = TAG_GREEN; }
            default           -> { texto = "POR FAZER";    fg = ACCENT;  bg = TAG_BLUE; }
        }
        JLabel lbl = new JLabel(" " + texto + " ");
        lbl.setFont(FONT_TAG);
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(2, 6, 4, 6));
        return lbl;
    }

    // =========================================================================
    // ACOES DE TAREFAS
    // =========================================================================
    private void moverTarefa(JPanel card, JLabel lblTag) {
        int id = (int) card.getClientProperty("tarefaId");
        Container parent = card.getParent();
        int idx = indexDoCard(parent, card);

        parent.remove(card);
        if (idx >= 0 && idx < parent.getComponentCount()
                && parent.getComponent(idx) instanceof Box.Filler) {
            parent.remove(idx);
        }

        EstadoTarefa novoEstado;
        if (parent == colunaAFazer) {
            novoEstado = EstadoTarefa.EM_PROGRESSO;
            colunaEmProgresso.add(card);
            colunaEmProgresso.add(Box.createVerticalStrut(8));
        } else if (parent == colunaEmProgresso) {
            novoEstado = EstadoTarefa.FINALIZADA;
            colunaConcluido.add(card);
            colunaConcluido.add(Box.createVerticalStrut(8));
        } else {
            novoEstado = EstadoTarefa.POR_FAZER;
            colunaAFazer.add(card);
            colunaAFazer.add(Box.createVerticalStrut(8));
        }

        atualizarTagEstado(lblTag, novoEstado);
        controller.alterarEstadoTarefa(id, novoEstado);
        atualizarContadores();
        refreshUI();
    }

    private void atualizarTagEstado(JLabel lblTag, EstadoTarefa estado) {
        switch (estado) {
            case EM_PROGRESSO -> { lblTag.setText(" EM PROGRESSO "); lblTag.setForeground(WARN);    lblTag.setBackground(new Color(80, 60, 10)); }
            case FINALIZADA   -> { lblTag.setText(" CONCLUIDA ");    lblTag.setForeground(SUCCESS); lblTag.setBackground(TAG_GREEN); }
            default           -> { lblTag.setText(" POR FAZER ");    lblTag.setForeground(ACCENT);  lblTag.setBackground(TAG_BLUE); }
        }
    }

    private void removerTarefa(JPanel card) {
        int confirm = JOptionPane.showConfirmDialog(
                this, "Tens a certeza que queres remover esta tarefa?",
                "Remover Tarefa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) card.getClientProperty("tarefaId");
            Container parent = card.getParent();
            int idx = indexDoCard(parent, card);
            parent.remove(card);
            if (idx >= 0 && idx < parent.getComponentCount()
                    && parent.getComponent(idx) instanceof Box.Filler) {
                parent.remove(idx);
            }
            controller.removeTarefa(id);
            atualizarContadores();
            refreshUI();
        }
    }

    private void editarTarefa(JPanel card, JLabel lblTitulo, JLabel lblTag) {
        JTextField campoNome = new JTextField(lblTitulo.getText(), 20);
        estilizarCampo(campoNome);

        JPanel painel = new JPanel(new GridLayout(0, 1, 6, 6));
        painel.setBackground(SURFACE);
        painel.setBorder(new EmptyBorder(8, 8, 8, 8));
        painel.add(criarLabelDialogo("Nome da tarefa *"));
        painel.add(campoNome);

        int result = JOptionPane.showConfirmDialog(
                this, painel, "Editar Tarefa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION && !campoNome.getText().trim().isEmpty()) {
            lblTitulo.setText(campoNome.getText().trim());
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
    // DIALOGOS
    // =========================================================================
    private void criarNovaTarefa() {
        JTextField campoNome = new JTextField(20);
        JTextField campoDesc = new JTextField(20);
        JTextField campoResp = new JTextField(20);
        estilizarCampo(campoNome);
        estilizarCampo(campoDesc);
        estilizarCampo(campoResp);

        JPanel painel = new JPanel(new GridLayout(0, 1, 6, 6));
        painel.setBackground(SURFACE);
        painel.setBorder(new EmptyBorder(8, 8, 8, 8));
        painel.add(criarLabelDialogo("Nome da tarefa *"));
        painel.add(campoNome);
        painel.add(criarLabelDialogo("Descricao (opcional)"));
        painel.add(campoDesc);
        painel.add(criarLabelDialogo("Responsavel (opcional)"));
        painel.add(campoResp);

        int result = JOptionPane.showConfirmDialog(
                this, painel, "Nova Tarefa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            if (!nome.isEmpty()) {
                Tarefa nova = controller.criaTarefa(nome, campoDesc.getText().trim());
                adicionarCardNaUI(colunaAFazer, nova, campoResp.getText().trim());
            }
        }
    }

    private void criarNovoProjeto() {
        String nome = JOptionPane.showInputDialog(this, "Nome do novo projeto:", "Novo Projeto", JOptionPane.PLAIN_MESSAGE);
        if (nome != null && !nome.trim().isEmpty()) {
            projetoAtivo = nome.trim();
            lblProjetoAtivo.setText(projetoAtivo);
        }
    }

    private void gerirEquipa() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidade de Gestao de Equipa em desenvolvimento.",
                "Gerir Equipa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarProjeto() {
        // TODO: controller.guardarProjeto() quando IODataLibrary estiver integrada
        JOptionPane.showMessageDialog(this, "Projeto guardado com sucesso!", "Guardar", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // DADOS DE DEMO — diferentes por projeto
    // =========================================================================
    private void carregarDemoParaProjeto(int indice) {
        // Limpar colunas
        colunaAFazer.removeAll();
        colunaEmProgresso.removeAll();
        colunaConcluido.removeAll();

        Object[][] tarefas = DEMO[indice];
        for (Object[] t : tarefas) {
            String titulo      = (String)  t[0];
            String descricao   = (String)  t[1];
            String responsavel = (String)  t[2];
            int    colIdx      = (Integer) t[3];

            EstadoTarefa estado = switch (colIdx) {
                case 1  -> EstadoTarefa.EM_PROGRESSO;
                case 2  -> EstadoTarefa.FINALIZADA;
                default -> EstadoTarefa.POR_FAZER;
            };

            Tarefa nova = controller.criaTarefa(titulo, descricao);
            controller.alterarEstadoTarefa(nova.getId(), estado);

            JPanel destino = switch (colIdx) {
                case 1  -> colunaEmProgresso;
                case 2  -> colunaConcluido;
                default -> colunaAFazer;
            };

            adicionarCardNaUI(destino, nova, responsavel);
        }
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
        campo.setBorder(new CompoundBorder(new LineBorder(BORDER_COL), new EmptyBorder(4, 8, 4, 8)));
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

    private JButton criarBotaoIconPequeno(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 22));
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
    // METODO PUBLICO: carregar tarefas do Model (quando IODataLibrary pronta)
    // =========================================================================
    public void carregarTarefasDoModel(List<Tarefa> tarefas) {
        colunaAFazer.removeAll();
        colunaEmProgresso.removeAll();
        colunaConcluido.removeAll();
        for (Tarefa t : tarefas) {
            JPanel destino = switch (t.getEstado()) {
                case EM_PROGRESSO -> colunaEmProgresso;
                case FINALIZADA   -> colunaConcluido;
                default           -> colunaAFazer;
            };
            adicionarCardNaUI(destino, t);
        }
        atualizarContadores();
        refreshUI();
    }
}