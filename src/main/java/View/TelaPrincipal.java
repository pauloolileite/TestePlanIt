package View;

import Controller.UsuarioController;
import Model.Usuario;
import Utils.BotaoGradiente;
import Utils.ImagePanel;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Objects;

public class TelaPrincipal extends JFrame {

    private final Usuario usuario;
    private final Connection conexao;
    private final Color azul1 = new Color(33, 124, 188);
    private final Color azul2 = new Color(33, 100, 188);
    private final Color roxo1 = new Color(75, 0, 130);
    private final Color roxo2 = new Color(100, 0, 130);

    public TelaPrincipal(Usuario usuario, Connection conexao) {
        this.usuario = usuario;
        this.conexao = conexao;

        setTitle("Plan It Agenda");
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarBarraMenu(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
    }

    private JPanel criarBarraMenu() {
        JPanel barraMenu = new JPanel(new BorderLayout());
        barraMenu.setBackground(Color.WHITE);
        barraMenu.setBorder(BorderFactory.createTitledBorder("Menu Principal"));

        JPanel painelBotoesMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoesMenu.setBackground(Color.WHITE);

        JButton btnCadastro = new JButton("Cadastro");
        JButton btnAgendamentos = new JButton("Agendamentos");
        JButton btnRelatorios = new JButton("Relatórios");
        JButton btnSistema = new JButton("Sistema");

        for (JButton btn : new JButton[]{btnCadastro, btnAgendamentos, btnRelatorios, btnSistema}) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBackground(Color.WHITE);
            btn.setForeground(roxo1);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            painelBotoesMenu.add(btn);
        }

        JLabel labelUsuarioLogado = new JLabel("Usuário: " + usuario.getUsername());
        labelUsuarioLogado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelUsuarioLogado.setForeground(azul1);
        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelUsuario.setBackground(Color.WHITE);
        painelUsuario.add(labelUsuarioLogado);

        barraMenu.add(painelBotoesMenu, BorderLayout.WEST);
        barraMenu.add(painelUsuario, BorderLayout.EAST);

        btnCadastro.addActionListener(e -> criarMenuCadastro().show(btnCadastro, 0, btnCadastro.getHeight()));
        btnAgendamentos.addActionListener(e -> criarMenuAgendamento().show(btnAgendamentos, 0, btnAgendamentos.getHeight()));
        btnRelatorios.addActionListener(e -> criarMenuRelatorios().show(btnRelatorios, 0, btnRelatorios.getHeight()));
        btnSistema.addActionListener(e -> criarMenuSistema().show(btnSistema, 0, btnSistema.getHeight()));

        return barraMenu;
    }

    private JPopupMenu criarMenuCadastro() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(criarMenuItem("Clientes", () -> new TelaCadastroCliente().setVisible(true)));
        menu.add(criarMenuItem("Serviços", () -> new TelaCadastroServico().setVisible(true)));
        menu.add(criarMenuItem("Funcionários", () -> new TelaCadastroFuncionario(usuario).setVisible(true)));
        if (UsuarioController.isAdmin(usuario)) {
            menu.add(criarMenuItem("Usuários", () -> new TelaUsuarios().setVisible(true)));
        }
        return menu;
    }

    private JPopupMenu criarMenuAgendamento() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(criarMenuItem("Novo Agendamento", () -> new TelaAgendamento(usuario, conexao).setVisible(true)));
        menu.add(criarMenuItem("Cancelar Agendamento", () -> new TelaCancelarAgendamento().setVisible(true)));
        return menu;
    }

    private JPopupMenu criarMenuRelatorios() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(criarMenuItem("Relatório de Agendamentos", () -> new TelaRelatorioAgendamentos().setVisible(true)));
        return menu;
    }

    private JPopupMenu criarMenuSistema() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(criarMenuItem("Logout", () -> {
            dispose();
            new TelaLogin().setVisible(true);
        }));
        return menu;
    }

    private JMenuItem criarMenuItem(String texto, Runnable acao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> acao.run());
        return item;
    }

    private JPanel criarPainelCentral() {
        JPanel painelCentral = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image fundo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Imagens/PlanIt.png"))).getImage();
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
                int x = (getWidth() - 400) / 2;
                int y = (getHeight() - 500) / 2;
                g2d.drawImage(fundo, x, y, 400, 500, this);
                g2d.dispose();
            }
        };

        painelCentral.setOpaque(false);
        painelCentral.setBackground(Color.WHITE);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JPanel painelBotoes = new JPanel(new GridLayout(0, 2, 20, 20));
        painelBotoes.setOpaque(false);

        painelBotoes.add(botaoComIcone("Novo Agendamento", azul1, azul2, "/Icons/agendamento.png", () -> new TelaAgendamento(usuario, conexao).setVisible(true)));
        painelBotoes.add(botaoComIcone("Cancelar Agendamento", azul1, azul2, "/Icons/cancelar_agendamento.png", () -> new TelaCancelarAgendamento().setVisible(true)));
        painelBotoes.add(botaoComIcone("Cadastrar Cliente", azul1, azul2, "/Icons/cliente.png", () -> new TelaCadastroCliente().setVisible(true)));
        painelBotoes.add(botaoComIcone("Visualizar Agendamentos", azul1, azul2, "/Icons/visualizar_agendamento.png", () -> new TelaRelatorioAgendamentos().setVisible(true)));

        if (UsuarioController.isAdmin(usuario)) {
            painelBotoes.add(botaoComIcone("Cadastrar Serviço", roxo2, roxo1, "/Icons/servico.png", () -> new TelaCadastroServico().setVisible(true)));
            painelBotoes.add(botaoComIcone("Cadastrar Profissional", roxo2, roxo1, "/Icons/novo_profissional.png", () -> new TelaCadastroFuncionario(usuario).setVisible(true)));
            painelBotoes.add(botaoComIcone("Gerenciar Usuários", roxo2, roxo1, "/Icons/usuarios.png", () -> new TelaUsuarios().setVisible(true)));
        }

        JPanel containerCentro = new JPanel();
        containerCentro.setOpaque(false);
        containerCentro.setLayout(new BoxLayout(containerCentro, BoxLayout.Y_AXIS));
        containerCentro.add(Box.createVerticalGlue());
        containerCentro.add(painelBotoes);
        containerCentro.add(Box.createVerticalGlue());

        painelCentral.add(containerCentro, BorderLayout.CENTER);
        return painelCentral;
    }

    private JPanel botaoComIcone(String texto, Color cor1, Color cor2, String caminhoIcone, Runnable acao) {
        BotaoGradiente painelBotao = new BotaoGradiente(cor1, cor2);

        ImagePanel icone = new ImagePanel(getClass().getResource(caminhoIcone), 60, 60, SwingConstants.CENTER);
        icone.setPreferredSize(new Dimension(60, 60));
        icone.setOpaque(false);

        JLabel labelTexto = new JLabel(texto);
        labelTexto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelTexto.setForeground(Color.WHITE);
        labelTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel painelTexto = new JPanel(new GridBagLayout());
        painelTexto.setOpaque(false);
        painelTexto.add(labelTexto);

        painelBotao.add(icone);
        painelBotao.add(painelTexto);

        painelBotao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                acao.run();
            }
        });

        return painelBotao;
    }
}
