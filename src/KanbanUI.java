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

    // ── Cores ─────────────────────────────────────────────────────────────────
    private static final Color BG         = new Color(15,  17,  23);
    private static final Color SURFACE    = new Color(22,  26,  35);
    private static final Color SURFACE2   = new Color(30,  35,  48);
    private static final Color BORDER_COL = new Color(45,  52,  70);
    private static final Color ACCENT     = new Color(99, 179, 237);
    private static final Color SUCCESS    = new Color(72, 199, 142);
    private static final Color WARN       = new Color(250,176,   5);
    private static final Color DANGER     = new Color(240, 62,  62);
    private static final Color TEXT_PRI   = new Color(237,242, 247);
    private static final Color TEXT_SEC   = new Color(113,128, 150);
    private static final Color CARD_BG    = new Color(28,  33,  45);
    private static final Color TAG_BLUE   = new Color(20,  60, 100);
    private static final Color TAG_GREEN  = new Color(20,  70,  50);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_COL    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_CARD_B = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_CARD   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_TAG    = new Font("Segoe UI", Font.BOLD,  10);
    private static final Font FONT_COUNT  = new Font("Segoe UI", Font.BOLD,  11);

    // ── Estado ────────────────────────────────────────────────────────────────
    private final KanbanController controller;

    private JPanel colunaAFazer;
    private JPanel colunaEmProgresso;
    private JPanel colunaConcluido;
    private JLabel lblCountAFazer;
    private JLabel lblCountEmProgresso;
    private JLabel lblCountConcluido;
    private JLabel lblProjetoAtivo;

    private JPanel painelListaProjetos;       // painel dinâmico na sidebar
    private JButton btnProjetoSelecionado;    // botão atualmente ativo

    // ── Construtor ────────────────────────────────────────────────────────────
    public KanbanUI(KanbanController controller) {
        this.controller = controller;
        configurarJanela();
        construirUI();

        // Carregar dados guardados, ou criar dados de demo
        if (controller.existemDadosGuardados()) {
            controller.carregarProjetos();
            atualizarListaProjetos();
            if (controller.getProjetoAtivo() != null) {
                lblProjetoAtivo.setText(controller.getProjetoAtivo().getNome());
                carregarTarefasDoProjetoAtivo();
            }
        } else {
            carregarDadosDemo();
            atualizarListaProjetos();
        }

        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Kanban - Gestao de Projetos");
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
                new EmptyBorder(0, 0, 0, 0)));

        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        esq.setOpaque(false);
        esq.setBorder(new EmptyBorder(0, 8, 0, 0));

        JLabel logo = new JLabel("KanbanApp");
        logo.setFont(FONT_TITLE);
        logo.setForeground(ACCENT);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(BORDER_COL);

        lblProjetoAtivo = new JLabel("Sem projeto");
        lblProjetoAtivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblProjetoAtivo.setForeground(TEXT_SEC);

        esq.add(logo); esq.add(sep); esq.add(lblProjetoAtivo);

        JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        dir.setOpaque(false);
        dir.setBorder(new EmptyBorder(0, 0, 0, 12));

        JButton btnGuardar = criarBotaoIcon("Guardar", SURFACE2, TEXT_PRI);
        btnGuardar.addActionListener(e -> guardarProjetos());
        dir.add(btnGuardar);

        bar.add(esq, BorderLayout.WEST);
        bar.add(dir, BorderLayout.EAST);
        return bar;
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================
    private JPanel construirSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(SURFACE);
        sb.setPreferredSize(new Dimension(215, 0));
        sb.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, BORDER_COL),
                new EmptyBorder(20, 14, 20, 14)));

        // Secção Projetos
        sb.add(criarSidebarLabel("PROJETOS"));
        sb.add(Box.createVerticalStrut(8));

        // Painel dinâmico que será preenchido em atualizarListaProjetos()
        painelListaProjetos = new JPanel();
        painelListaProjetos.setLayout(new BoxLayout(painelListaProjetos, BoxLayout.Y_AXIS));
        painelListaProjetos.setOpaque(false);
        sb.add(painelListaProjetos);
        sb.add(Box.createVerticalStrut(8));

        JButton btnNovoProjeto = criarBotaoSidebar("+ Novo Projeto");
        btnNovoProjeto.addActionListener(e -> dialogCriarProjeto());
        sb.add(btnNovoProjeto);
        sb.add(Box.createVerticalStrut(28));

        // Secção Acoes Rapidas
        sb.add(criarSidebarLabel("ACOES RAPIDAS"));
        sb.add(Box.createVerticalStrut(8));

        JButton btnNovaTarefa = criarBotaoPrimary("+ Nova Tarefa");
        btnNovaTarefa.addActionListener(e -> dialogCriarTarefa());
        sb.add(btnNovaTarefa);
        sb.add(Box.createVerticalStrut(8));

        JButton btnEquipa = criarBotaoSidebar("Gerir Equipa");
        btnEquipa.addActionListener(e -> dialogGerirEquipa());
        sb.add(btnEquipa);
        sb.add(Box.createVerticalStrut(8));

        JButton btnEliminar = criarBotaoSidebar("Eliminar Projeto");
        btnEliminar.setForeground(DANGER);
        btnEliminar.addActionListener(e -> eliminarProjetoAtivo());
        sb.add(btnEliminar);

        sb.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("v1.0 - ESTGA / UA");
        ver.setFont(FONT_SMALL);
        ver.setForeground(new Color(60, 70, 90));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(ver);

        return sb;
    }

    // Reconstroi a lista de projetos na sidebar
    private void atualizarListaProjetos() {
        painelListaProjetos.removeAll();
        btnProjetoSelecionado = null;

        for (Projeto p : controller.getProjetos()) {
            boolean ativo = controller.getProjetoAtivo() != null
                    && controller.getProjetoAtivo().getId() == p.getId();
            JButton btn = criarBotaoProjeto(p, ativo);
            if (ativo) btnProjetoSelecionado = btn;
            painelListaProjetos.add(btn);
            painelListaProjetos.add(Box.createVerticalStrut(4));
        }

        painelListaProjetos.revalidate();
        painelListaProjetos.repaint();
    }

    private JButton criarBotaoProjeto(Projeto p, boolean ativo) {
        JButton btn = new JButton((ativo ? "● " : "○ ") + p.getNome());
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
        btn.addActionListener(e -> selecionarProjeto(btn, p));
        return btn;
    }

    private void selecionarProjeto(JButton btnClicado, Projeto p) {
        if (btnProjetoSelecionado != null && btnProjetoSelecionado != btnClicado) {
            String nomeAnt = btnProjetoSelecionado.getText().substring(2);
            btnProjetoSelecionado.setText("○ " + nomeAnt);
            btnProjetoSelecionado.setForeground(TEXT_SEC);
            btnProjetoSelecionado.setBackground(SURFACE);
        }
        btnClicado.setText("● " + p.getNome());
        btnClicado.setForeground(TEXT_PRI);
        btnClicado.setBackground(SURFACE2);
        btnProjetoSelecionado = btnClicado;

        controller.setProjetoAtivo(p);
        lblProjetoAtivo.setText(p.getNome());
        carregarTarefasDoProjetoAtivo();
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
        JPanel[] cols    = new JPanel[3];
        JLabel[] counts  = new JLabel[3];

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
                new EmptyBorder(12, 14, 12, 14)));

        JPanel stripe = new JPanel();
        stripe.setBackground(cor);
        stripe.setPreferredSize(new Dimension(3, 0));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(FONT_COL);
        lblT.setForeground(TEXT_PRI);

        JPanel hl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hl.setOpaque(false);
        hl.add(lblT); hl.add(contador);

        header.add(stripe, BorderLayout.WEST);
        header.add(hl,     BorderLayout.CENTER);

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
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1, true),
                new EmptyBorder(12, 14, 10, 14)));
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
            JTextArea area = new JTextArea(tarefa.getDescricao());
            area.setFont(FONT_SMALL);
            area.setForeground(TEXT_SEC);
            area.setBackground(CARD_BG);
            area.setEditable(false); area.setFocusable(false);
            area.setLineWrap(true); area.setWrapStyleWord(true);
            area.setOpaque(false);
            area.setAlignmentX(Component.LEFT_ALIGNMENT);
            centro.add(Box.createVerticalStrut(4));
            centro.add(area);
        }
        card.add(centro, BorderLayout.CENTER);

        // Rodapé: ID + responsável + botões
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel infoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        infoLeft.setOpaque(false);
        JLabel lblId = new JLabel("ID:" + tarefa.getId());
        lblId.setFont(FONT_SMALL);
        lblId.setForeground(new Color(70, 85, 110));
        infoLeft.add(lblId);
        if (tarefa.getResponsavel() != null && !tarefa.getResponsavel().isEmpty()) {
            JLabel lblR = new JLabel(tarefa.getResponsavel());
            lblR.setFont(FONT_SMALL);
            lblR.setForeground(TEXT_SEC);
            infoLeft.add(lblR);
        }
        rodape.add(infoLeft, BorderLayout.WEST);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        acoes.setOpaque(false);
        JButton btnEd  = criarBotaoIconPequeno("Ed", new Color(60, 80,110), ACCENT);
        JButton btnMov = criarBotaoIconPequeno(">>", new Color(40, 70, 40), SUCCESS);
        JButton btnRem = criarBotaoIconPequeno("X",  new Color(80, 30, 30), DANGER);
        btnEd.setToolTipText("Editar");
        btnMov.setToolTipText("Mover para proxima coluna");
        btnRem.setToolTipText("Remover");
        btnEd.addActionListener(e  -> dialogEditarTarefa(card, lblTitulo, lblTag));
        btnMov.addActionListener(e -> moverTarefa(card, lblTag));
        btnRem.addActionListener(e -> removerTarefa(card));
        acoes.add(btnEd); acoes.add(btnMov); acoes.add(btnRem);
        rodape.add(acoes, BorderLayout.EAST);

        card.add(rodape, BorderLayout.SOUTH);
        coluna.add(card);
        coluna.add(Box.createVerticalStrut(8));
        atualizarContadores();
        refreshUI();
    }

    private JLabel criarTagEstado(EstadoTarefa estado) {
        String txt; Color fg, bg;
        switch (estado) {
            case EM_PROGRESSO -> { txt="EM PROGRESSO"; fg=WARN;    bg=new Color(80,60,10); }
            case FINALIZADA   -> { txt="CONCLUIDA";    fg=SUCCESS; bg=TAG_GREEN; }
            default           -> { txt="POR FAZER";    fg=ACCENT;  bg=TAG_BLUE; }
        }
        JLabel lbl = new JLabel(" " + txt + " ");
        lbl.setFont(FONT_TAG); lbl.setForeground(fg); lbl.setBackground(bg);
        lbl.setOpaque(true); lbl.setBorder(new EmptyBorder(2, 6, 4, 6));
        return lbl;
    }

    private void atualizarTagEstado(JLabel lbl, EstadoTarefa estado) {
        switch (estado) {
            case EM_PROGRESSO -> { lbl.setText(" EM PROGRESSO "); lbl.setForeground(WARN);    lbl.setBackground(new Color(80,60,10)); }
            case FINALIZADA   -> { lbl.setText(" CONCLUIDA ");    lbl.setForeground(SUCCESS); lbl.setBackground(TAG_GREEN); }
            default           -> { lbl.setText(" POR FAZER ");    lbl.setForeground(ACCENT);  lbl.setBackground(TAG_BLUE); }
        }
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
                && parent.getComponent(idx) instanceof Box.Filler)
            parent.remove(idx);

        EstadoTarefa novoEstado;
        if (parent == colunaAFazer) {
            novoEstado = EstadoTarefa.EM_PROGRESSO;
            colunaEmProgresso.add(card); colunaEmProgresso.add(Box.createVerticalStrut(8));
        } else if (parent == colunaEmProgresso) {
            novoEstado = EstadoTarefa.FINALIZADA;
            colunaConcluido.add(card); colunaConcluido.add(Box.createVerticalStrut(8));
        } else {
            novoEstado = EstadoTarefa.POR_FAZER;
            colunaAFazer.add(card); colunaAFazer.add(Box.createVerticalStrut(8));
        }
        atualizarTagEstado(lblTag, novoEstado);
        controller.alterarEstadoTarefa(id, novoEstado);
        atualizarContadores(); refreshUI();
    }

    private void removerTarefa(JPanel card) {
        if (JOptionPane.showConfirmDialog(this,
                "Tens a certeza que queres remover esta tarefa?", "Remover Tarefa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            int id = (int) card.getClientProperty("tarefaId");
            Container parent = card.getParent();
            int idx = indexDoCard(parent, card);
            parent.remove(card);
            if (idx >= 0 && idx < parent.getComponentCount()
                    && parent.getComponent(idx) instanceof Box.Filler)
                parent.remove(idx);
            controller.removeTarefa(id);
            atualizarContadores(); refreshUI();
        }
    }

    private int indexDoCard(Container parent, JPanel card) {
        for (int i = 0; i < parent.getComponentCount(); i++)
            if (parent.getComponent(i) == card) return i;
        return -1;
    }

    // =========================================================================
    // DIALOGO: CRIAR PROJETO
    // =========================================================================
    private void dialogCriarProjeto() {
        JTextField campoNome = new JTextField(20);
        estilizarCampo(campoNome);

        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.setBackground(SURFACE); p.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.add(criarLabelDialogo("Nome do projeto *")); p.add(campoNome);

        if (JOptionPane.showConfirmDialog(this, p, "Novo Projeto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            if (!nome.isEmpty()) {
                Projeto novo = controller.criarProjeto(nome);
                atualizarListaProjetos();
                // Selecionar o novo projeto automaticamente
                for (Component c : painelListaProjetos.getComponents()) {
                    if (c instanceof JButton) {
                        JButton btn = (JButton) c;
                        if (btn.getText().contains(nome)) {
                            selecionarProjeto(btn, novo);
                            break;
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // DIALOGO: CRIAR TAREFA
    // =========================================================================
    private void dialogCriarTarefa() {
        if (controller.getProjetoAtivo() == null) {
            JOptionPane.showMessageDialog(this, "Cria ou seleciona um projeto primeiro.", "Sem Projeto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField campoNome = new JTextField(20);
        JTextField campoDesc = new JTextField(20);
        JTextField campoResp = new JTextField(20);
        estilizarCampo(campoNome); estilizarCampo(campoDesc); estilizarCampo(campoResp);

        // Lista de membros como sugestão
        List<Membro> membros = controller.getMembrosDoProjetoAtivo();
        if (!membros.isEmpty()) {
            String[] nomes = membros.stream().map(Membro::getNome).toArray(String[]::new);
            JComboBox<String> combo = new JComboBox<>(nomes);
            combo.setEditable(true);
            combo.setBackground(SURFACE2);
            combo.setForeground(TEXT_PRI);

            JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
            p.setBackground(SURFACE); p.setBorder(new EmptyBorder(8, 8, 8, 8));
            p.add(criarLabelDialogo("Nome da tarefa *")); p.add(campoNome);
            p.add(criarLabelDialogo("Descricao")); p.add(campoDesc);
            p.add(criarLabelDialogo("Responsavel")); p.add(combo);

            if (JOptionPane.showConfirmDialog(this, p, "Nova Tarefa",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                String nome = campoNome.getText().trim();
                if (!nome.isEmpty()) {
                    Tarefa t = controller.criaTarefa(nome, campoDesc.getText().trim());
                    t.setResponsavel((String) combo.getSelectedItem());
                    adicionarCardNaUI(colunaAFazer, t);
                }
            }
        } else {
            JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
            p.setBackground(SURFACE); p.setBorder(new EmptyBorder(8, 8, 8, 8));
            p.add(criarLabelDialogo("Nome da tarefa *")); p.add(campoNome);
            p.add(criarLabelDialogo("Descricao")); p.add(campoDesc);
            p.add(criarLabelDialogo("Responsavel")); p.add(campoResp);

            if (JOptionPane.showConfirmDialog(this, p, "Nova Tarefa",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                String nome = campoNome.getText().trim();
                if (!nome.isEmpty()) {
                    Tarefa t = controller.criaTarefa(nome, campoDesc.getText().trim());
                    t.setResponsavel(campoResp.getText().trim());
                    adicionarCardNaUI(colunaAFazer, t);
                }
            }
        }
    }

    // =========================================================================
    // DIALOGO: EDITAR TAREFA
    // =========================================================================
    private void dialogEditarTarefa(JPanel card, JLabel lblTitulo, JLabel lblTag) {
        int id = (int) card.getClientProperty("tarefaId");
        Tarefa t = controller.procuraTarefaPorID(id);
        if (t == null) return;

        JTextField campoNome = new JTextField(t.getTitulo(), 20);
        JTextField campoDesc = new JTextField(t.getDescricao(), 20);
        JTextField campoResp = new JTextField(t.getResponsavel(), 20);
        estilizarCampo(campoNome); estilizarCampo(campoDesc); estilizarCampo(campoResp);

        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.setBackground(SURFACE); p.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.add(criarLabelDialogo("Nome da tarefa *")); p.add(campoNome);
        p.add(criarLabelDialogo("Descricao")); p.add(campoDesc);
        p.add(criarLabelDialogo("Responsavel")); p.add(campoResp);

        if (JOptionPane.showConfirmDialog(this, p, "Editar Tarefa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            if (!nome.isEmpty()) {
                t.setTitulo(nome);
                t.setDescricao(campoDesc.getText().trim());
                t.setResponsavel(campoResp.getText().trim());
                lblTitulo.setText(nome);
                refreshUI();
            }
        }
    }

    // =========================================================================
    // DIALOGO: GERIR EQUIPA
    // =========================================================================
    private void dialogGerirEquipa() {
        if (controller.getProjetoAtivo() == null) {
            JOptionPane.showMessageDialog(this, "Seleciona um projeto primeiro.", "Sem Projeto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Painel principal do dialogo
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 10));
        painelPrincipal.setBackground(SURFACE);
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelPrincipal.setPreferredSize(new Dimension(380, 320));

        // Lista de membros
        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setBackground(SURFACE2);

        Runnable atualizarLista = () -> {
            painelLista.removeAll();
            List<Membro> membros = controller.getMembrosDoProjetoAtivo();
            if (membros.isEmpty()) {
                JLabel vazio = new JLabel("  Sem membros neste projeto.");
                vazio.setFont(FONT_SMALL);
                vazio.setForeground(TEXT_SEC);
                painelLista.add(vazio);
            } else {
                for (Membro m : membros) {
                    JPanel linha = new JPanel(new BorderLayout());
                    linha.setBackground(SURFACE2);
                    linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
                    linha.setBorder(new CompoundBorder(
                            new MatteBorder(0, 0, 1, 0, BORDER_COL),
                            new EmptyBorder(4, 10, 4, 6)));

                    JLabel lblM = new JLabel(m.getNome() + "  —  " + m.getPapel());
                    lblM.setFont(FONT_CARD);
                    lblM.setForeground(TEXT_PRI);

                    JButton btnRem = criarBotaoIconPequeno("X", new Color(80, 30, 30), DANGER);
                    btnRem.addActionListener(ev -> {
                        controller.removerMembro(m.getNome());
                        // o dialogo fecha e reabre não — só atualizamos a lista
                        atualizarListaMembrosNoDialogo(painelLista);
                    });

                    linha.add(lblM, BorderLayout.CENTER);
                    linha.add(btnRem, BorderLayout.EAST);
                    painelLista.add(linha);
                }
            }
            painelLista.revalidate();
            painelLista.repaint();
        };

        atualizarLista.run();

        JScrollPane scroll = new JScrollPane(painelLista);
        scroll.setBorder(new LineBorder(BORDER_COL));
        scroll.setBackground(SURFACE2);
        scroll.getViewport().setBackground(SURFACE2);

        // Formulario para adicionar membro
        JTextField campoNomeMembro = new JTextField();
        JTextField campoPapel      = new JTextField();
        estilizarCampo(campoNomeMembro);
        estilizarCampo(campoPapel);

        JPanel painelAdd = new JPanel(new GridLayout(0, 1, 4, 4));
        painelAdd.setBackground(SURFACE);
        painelAdd.add(criarLabelDialogo("Nome do membro"));
        painelAdd.add(campoNomeMembro);
        painelAdd.add(criarLabelDialogo("Papel (ex: Developer, Designer)"));
        painelAdd.add(campoPapel);

        JButton btnAdicionar = criarBotaoPrimary("Adicionar Membro");
        btnAdicionar.addActionListener(ev -> {
            String nome  = campoNomeMembro.getText().trim();
            String papel = campoPapel.getText().trim();
            if (!nome.isEmpty()) {
                controller.adicionarMembro(nome, papel.isEmpty() ? "Membro" : papel);
                campoNomeMembro.setText("");
                campoPapel.setText("");
                atualizarListaMembrosNoDialogo(painelLista);
            }
        });

        painelPrincipal.add(scroll,       BorderLayout.CENTER);
        painelPrincipal.add(painelAdd,    BorderLayout.SOUTH);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(SURFACE);
        JLabel titulo = new JLabel("Equipa: " + controller.getProjetoAtivo().getNome());
        titulo.setFont(FONT_CARD_B);
        titulo.setForeground(TEXT_PRI);
        topo.add(titulo, BorderLayout.WEST);
        topo.add(btnAdicionar, BorderLayout.EAST);
        painelPrincipal.add(topo, BorderLayout.NORTH);

        JOptionPane.showMessageDialog(this, painelPrincipal, "Gerir Equipa", JOptionPane.PLAIN_MESSAGE);
    }

    // Atualiza a lista de membros dentro do dialogo sem o fechar
    private void atualizarListaMembrosNoDialogo(JPanel painelLista) {
        painelLista.removeAll();
        List<Membro> membros = controller.getMembrosDoProjetoAtivo();
        if (membros.isEmpty()) {
            JLabel vazio = new JLabel("  Sem membros neste projeto.");
            vazio.setFont(FONT_SMALL); vazio.setForeground(TEXT_SEC);
            painelLista.add(vazio);
        } else {
            for (Membro m : membros) {
                JPanel linha = new JPanel(new BorderLayout());
                linha.setBackground(SURFACE2);
                linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
                linha.setBorder(new CompoundBorder(
                        new MatteBorder(0, 0, 1, 0, BORDER_COL),
                        new EmptyBorder(4, 10, 4, 6)));
                JLabel lblM = new JLabel(m.getNome() + "  —  " + m.getPapel());
                lblM.setFont(FONT_CARD); lblM.setForeground(TEXT_PRI);
                JButton btnR = criarBotaoIconPequeno("X", new Color(80, 30, 30), DANGER);
                btnR.addActionListener(ev -> {
                    controller.removerMembro(m.getNome());
                    atualizarListaMembrosNoDialogo(painelLista);
                });
                linha.add(lblM, BorderLayout.CENTER);
                linha.add(btnR, BorderLayout.EAST);
                painelLista.add(linha);
            }
        }
        painelLista.revalidate();
        painelLista.repaint();
    }

    // =========================================================================
    // ELIMINAR PROJETO ATIVO
    // =========================================================================
    private void eliminarProjetoAtivo() {
        Projeto p = controller.getProjetoAtivo();
        if (p == null) return;
        if (JOptionPane.showConfirmDialog(this,
                "Eliminar o projeto \"" + p.getNome() + "\" e todas as suas tarefas?",
                "Eliminar Projeto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.YES_OPTION) {
            controller.eliminarProjeto(p.getId());
            atualizarListaProjetos();
            limparColunas();
            Projeto ativo = controller.getProjetoAtivo();
            if (ativo != null) {
                lblProjetoAtivo.setText(ativo.getNome());
                carregarTarefasDoProjetoAtivo();
            } else {
                lblProjetoAtivo.setText("Sem projeto");
            }
        }
    }

    // =========================================================================
    // GUARDAR
    // =========================================================================
    private void guardarProjetos() {
        controller.guardarProjetos();
        JOptionPane.showMessageDialog(this, "Dados guardados com sucesso!", "Guardar", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // CARREGAR TAREFAS DO PROJETO ATIVO PARA A UI
    // =========================================================================
    private void carregarTarefasDoProjetoAtivo() {
        limparColunas();
        Projeto p = controller.getProjetoAtivo();
        if (p == null) return;
        for (Tarefa t : p.getTarefas()) {
            JPanel destino = switch (t.getEstado()) {
                case EM_PROGRESSO -> colunaEmProgresso;
                case FINALIZADA   -> colunaConcluido;
                default           -> colunaAFazer;
            };
            adicionarCardNaUI(destino, t);
        }
    }

    private void limparColunas() {
        colunaAFazer.removeAll();
        colunaEmProgresso.removeAll();
        colunaConcluido.removeAll();
        atualizarContadores();
        refreshUI();
    }

    // =========================================================================
    // DADOS DE DEMO
    // =========================================================================
    private void carregarDadosDemo() {
        // Projeto Alpha
        Projeto alpha = controller.criarProjeto("Projeto Alpha");
        controller.setProjetoAtivo(alpha);
        alpha.adicionarMembro(new Membro("Ana Silva",   "Developer"));
        alpha.adicionarMembro(new Membro("Joao Costa",  "Designer"));
        alpha.adicionarMembro(new Membro("Pedro Nunes", "Tester"));
        criarTarefaDemo("Definir estrutura MVC",   "Separar Model, View e Controller",   "Ana Silva",   EstadoTarefa.POR_FAZER);
        criarTarefaDemo("Criar testes unitarios",  "JUnit para classes do Model",         "Pedro Nunes", EstadoTarefa.POR_FAZER);
        criarTarefaDemo("Interface KanbanUI",      "Swing com dark theme profissional",   "Joao Costa",  EstadoTarefa.EM_PROGRESSO);
        criarTarefaDemo("IODataLibrary",           "Leitura e escrita em ficheiro .txt",  "Ana Silva",   EstadoTarefa.EM_PROGRESSO);
        criarTarefaDemo("Planeamento do projeto",  "Distribuicao de tarefas e prazos",    "Joao Costa",  EstadoTarefa.FINALIZADA);
        criarTarefaDemo("Diagrama UML",            "Modelacao das entidades principais",  "Pedro Nunes", EstadoTarefa.FINALIZADA);

        // Projeto Beta
        Projeto beta = controller.criarProjeto("Projeto Beta");
        controller.setProjetoAtivo(beta);
        beta.adicionarMembro(new Membro("Maria Sousa", "Developer"));
        beta.adicionarMembro(new Membro("Rui Faria",   "DevOps"));
        criarTarefaDemo("Modulo de autenticacao", "Login e registo de utilizadores",      "Maria Sousa", EstadoTarefa.POR_FAZER);
        criarTarefaDemo("Base de dados SQLite",   "Configurar schema e conexao",          "Rui Faria",   EstadoTarefa.EM_PROGRESSO);
        criarTarefaDemo("Dashboard de metricas",  "Graficos e estatisticas",              "Maria Sousa", EstadoTarefa.FINALIZADA);

        // Projeto Gama
        Projeto gama = controller.criarProjeto("Projeto Gama");
        controller.setProjetoAtivo(gama);
        gama.adicionarMembro(new Membro("Ines Lopes", "Designer"));
        gama.adicionarMembro(new Membro("Tiago Reis", "Developer"));
        criarTarefaDemo("Wireframes UI/UX",    "Mockups de todas as ecras",               "Ines Lopes",  EstadoTarefa.POR_FAZER);
        criarTarefaDemo("Notificacoes push",   "Integrar Firebase Cloud Messaging",       "Tiago Reis",  EstadoTarefa.EM_PROGRESSO);
        criarTarefaDemo("Deploy em producao",  "Pipeline CI/CD com GitHub Actions",       "Ines Lopes",  EstadoTarefa.FINALIZADA);

        // Voltar ao Alpha como ativo
        controller.setProjetoAtivo(alpha);
        lblProjetoAtivo.setText(alpha.getNome());
        carregarTarefasDoProjetoAtivo();
    }

    private void criarTarefaDemo(String titulo, String desc, String resp, EstadoTarefa estado) {
        Tarefa t = controller.criaTarefa(titulo, desc);
        if (t != null) { t.setResponsavel(resp); t.setEstado(estado); }
    }

    // =========================================================================
    // HELPERS UI
    // =========================================================================
    private JLabel criarSidebarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel criarLabelDialogo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SEC);
        return lbl;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(SURFACE2); campo.setForeground(TEXT_PRI);
        campo.setCaretColor(ACCENT);
        campo.setBorder(new CompoundBorder(new LineBorder(BORDER_COL), new EmptyBorder(4, 8, 4, 8)));
        campo.setFont(FONT_CARD);
    }

    private JButton criarBotaoPrimary(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BTN); btn.setBackground(ACCENT);
        btn.setForeground(new Color(10, 20, 35));
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private JButton criarBotaoSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_CARD); btn.setBackground(SURFACE2); btn.setForeground(TEXT_PRI);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(6, 10, 6, 10));
        return btn;
    }

    private JButton criarBotaoIcon(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BTN); btn.setBackground(bg); btn.setForeground(fg);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private JButton criarBotaoIconPequeno(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
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
        int c = 0;
        for (Component comp : coluna.getComponents())
            if (comp instanceof JPanel) c++;
        return c;
    }

    private void refreshUI() { revalidate(); repaint(); }
}