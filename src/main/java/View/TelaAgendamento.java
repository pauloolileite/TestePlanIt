package View;

import Controller.*;
import Model.Cliente;
import Model.Funcionario;
import Model.Servico;
import Model.Usuario;
import Utils.DateLabelFormatter;
import Utils.DataUtils;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class TelaAgendamento extends JFrame {
    private static final String MSG_SUCESSO = "Agendamento cadastrado com sucesso para %s às %s!";
    private static final String MSG_AVISO = "Aviso";
    private static final String MSG_ERRO = "Erro";

    private JComboBox<Funcionario> comboFuncionario;
    private JComboBox<Cliente> comboCliente;
    private JComboBox<Servico> comboServico;
    private JComboBox<String> comboHora;
    private JDatePickerImpl datePicker;
    private JTextField campoObs;
    private JButton botaoAgendar, botaoCancelar;
    private final AgendamentoController controller;
    private final Usuario usuario;

    public TelaAgendamento(Usuario usuario, Connection conexao) {
        this.usuario = usuario;
        this.controller = new AgendamentoController(conexao);

        setTitle("Novo Agendamento");
        setSize(700, 400);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            ClienteController clienteController = new ClienteController(conexao);
            ServicoController servicoController = new ServicoController(conexao);
            FuncionarioController funcionarioController = new FuncionarioController(conexao);
            UsuarioController usuarioController = new UsuarioController(conexao);

            JPanel painelPrincipal = criarPainelFundo();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 20, 10, 20);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            adicionarTitulo(painelPrincipal, gbc);
            adicionarCampoDataHora(painelPrincipal, gbc);
            adicionarComboFuncionario(painelPrincipal, gbc, funcionarioController, usuarioController);
            adicionarComboCliente(painelPrincipal, gbc, clienteController);
            adicionarComboServico(painelPrincipal, gbc, servicoController, usuarioController);
            adicionarCampoObservacoes(painelPrincipal, gbc);
            adicionarBotoes(painelPrincipal, gbc);

            add(painelPrincipal);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar tela: " + e.getMessage());
        }
    }

    private JPanel criarPainelFundo() {
        JPanel painel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image fundo = new ImageIcon("src/main/resources/Imagens/PlanIt.png").getImage();
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                int x = (getWidth() - 300) / 2;
                int y = (getHeight() - 300) / 2;
                g2d.drawImage(fundo, x, y, 300, 300, this);
                g2d.dispose();
            }
        };
        painel.setBackground(Color.WHITE);
        return painel;
    }

    private void adicionarTitulo(JPanel painel, GridBagConstraints gbc) {
        JLabel titulo = new JLabel("Novo Agendamento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(new Color(33, 124, 188));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(titulo, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
    }

    private void adicionarCampoDataHora(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Data:"), gbc);

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Hoje");
        p.put("text.month", "Mês");
        p.put("text.year", "Ano");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        gbc.gridx = 1;
        painel.add(datePicker, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Horário:"), gbc);

        comboHora = new JComboBox<>();
        for (int h = 7; h <= 20; h++) {
            comboHora.addItem(String.format("%02d:00", h));
            comboHora.addItem(String.format("%02d:30", h));
        }
        gbc.gridx = 3;
        painel.add(comboHora, gbc);
    }

    private void adicionarComboFuncionario(JPanel painel, GridBagConstraints gbc, FuncionarioController fc, UsuarioController uc) throws Exception {
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Profissional:"), gbc);

        comboFuncionario = new JComboBox<>();
        for (Funcionario f : fc.listarFuncionarios()) comboFuncionario.addItem(f);
        gbc.gridx = 1;
        painel.add(comboFuncionario, gbc);

        if (UsuarioController.isAdmin(usuario)) {
            JButton btnNovoFuncionario = new JButton("Nova Profissional");
            btnNovoFuncionario.addActionListener(ev -> new TelaCadastroFuncionario(usuario).setVisible(true));
            gbc.gridx = 3;
            painel.add(btnNovoFuncionario, gbc);
        }
    }

    private void adicionarComboCliente(JPanel painel, GridBagConstraints gbc, ClienteController cc) throws Exception {
        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(new JLabel("Cliente:"), gbc);

        comboCliente = new JComboBox<>();
        for (Cliente c : cc.listarClientes()) comboCliente.addItem(c);
        gbc.gridx = 1;
        painel.add(comboCliente, gbc);

        JButton btnNovoCliente = new JButton("Nova Cliente");
        btnNovoCliente.addActionListener(ev -> new TelaCadastroCliente().setVisible(true));
        gbc.gridx = 3;
        painel.add(btnNovoCliente, gbc);
    }

    private void adicionarComboServico(JPanel painel, GridBagConstraints gbc, ServicoController sc, UsuarioController uc) throws Exception {
        gbc.gridx = 0; gbc.gridy = 4;
        painel.add(new JLabel("Serviço:"), gbc);

        comboServico = new JComboBox<>();
        for (Servico s : sc.listarServicos()) comboServico.addItem(s);
        gbc.gridx = 1;
        painel.add(comboServico, gbc);

        if (UsuarioController.isAdmin(usuario)) {
            JButton btnNovoServico = new JButton("Novo Serviço");
            btnNovoServico.addActionListener(ev -> new TelaCadastroServico().setVisible(true));
            gbc.gridx = 3;
            painel.add(btnNovoServico, gbc);
        }
    }

    private void adicionarCampoObservacoes(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 5;
        painel.add(new JLabel("Detalhes:"), gbc);

        campoObs = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3;
        painel.add(campoObs, gbc);
        gbc.gridwidth = 1;
    }

    private void adicionarBotoes(JPanel painel, GridBagConstraints gbc) {
        JPanel botoes = new JPanel();
        Color verde = new Color(0, 153, 0);
        Color vermelho = new Color(204, 0, 0);

        botaoAgendar = new JButton("Agendar");
        botaoAgendar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoAgendar.setBackground(verde);
        botaoAgendar.setForeground(Color.WHITE);
        botaoAgendar.addActionListener(e -> realizarAgendamento());

        botaoCancelar = new JButton("Cancelar");
        botaoCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoCancelar.setBackground(vermelho);
        botaoCancelar.setForeground(Color.WHITE);
        botaoCancelar.addActionListener(e -> dispose());

        botoes.setBackground(Color.WHITE);
        botoes.add(botaoAgendar);
        botoes.add(botaoCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botoes, gbc);
    }

    private void realizarAgendamento() {
        try {
            Cliente cliente = (Cliente) comboCliente.getSelectedItem();
            Servico servico = (Servico) comboServico.getSelectedItem();
            Funcionario funcionario = (Funcionario) comboFuncionario.getSelectedItem();
            Date data = (Date) datePicker.getModel().getValue();
            String hora = (String) comboHora.getSelectedItem();
            String observacoes = campoObs.getText();

            controller.cadastrarAgendamento(cliente, funcionario, servico, data, hora, observacoes);
            String dataFormatada = DataUtils.formatar(data, "dd-MM-yyyy");
            JOptionPane.showMessageDialog(this, String.format(MSG_SUCESSO, dataFormatada, hora));
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), MSG_AVISO, JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MSG_ERRO + ": " + ex.getMessage(), MSG_ERRO, JOptionPane.ERROR_MESSAGE);
        }
    }
}
