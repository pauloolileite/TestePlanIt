package View;

import Controller.FuncionarioController;
import Controller.UsuarioController;
import Model.Usuario;
import Model.TipoUsuario;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.Objects;

public class TelaCadastroFuncionario extends JFrame {
    private static final String MSG_ERRO_CONEXAO = "Erro ao conectar: ";
    private static final String MSG_ERRO_CADASTRAR = "Erro ao cadastrar: ";
    private static final String MSG_SUCESSO = "Profissional cadastrado com sucesso!";
    private static final String MSG_CAMPOS_OBRIGATORIOS = "Preencha nome e cargo.";
    private static final String MSG_CAMPOS_USUARIO_OBRIGATORIOS = "Preencha os campos de usuário.";
    private static final String MSG_USUARIO_DUPLICADO = "Nome de usuário já está em uso.";

    private JTextField campoNome, campoCargo, campoUsername;
    private JPasswordField campoSenha;
    private JComboBox<TipoUsuario> comboTipo;
    private JButton botaoSalvar;
    private FuncionarioController funcionarioController;
    private UsuarioController usuarioController;

    public TelaCadastroFuncionario(Usuario usuarioLogado) {
        setTitle("Cadastro de Profissional");
        setSize(450, 650);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        Color azul1 = new Color(33, 124, 188);
        boolean isAdmin = UsuarioController.isAdmin(usuarioLogado);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            funcionarioController = new FuncionarioController(conexao);
            usuarioController = new UsuarioController(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CONEXAO + e.getMessage());
            return;
        }

        JPanel painelPrincipal = new JPanel(new GridBagLayout()) {
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
        painelPrincipal.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Cadastro de Profissional");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(azul1);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelPrincipal.add(titulo, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        criarCampoNome(painelPrincipal, gbc, azul1);
        criarCampoCargo(painelPrincipal, gbc, azul1);

        if (isAdmin) {
            criarCampoUsuario(painelPrincipal, gbc, azul1);
        }

        botaoSalvar = new JButton("Salvar");
        botaoSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setBackground(azul1);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoSalvar.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelPrincipal.add(botaoSalvar, gbc);

        botaoSalvar.addActionListener(e -> salvarFuncionario(isAdmin));
        add(painelPrincipal);
    }

    private void criarCampoNome(JPanel painel, GridBagConstraints gbc, Color azul) {
        JLabel label = new JLabel("Nome:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(label, gbc);

        campoNome = new JTextField();
        campoNome.setPreferredSize(new Dimension(250, 30));
        campoNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(azul, 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        painel.add(campoNome, gbc);
    }

    private void criarCampoCargo(JPanel painel, GridBagConstraints gbc, Color azul) {
        JLabel label = new JLabel("Cargo:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        painel.add(label, gbc);

        campoCargo = new JTextField();
        campoCargo.setPreferredSize(new Dimension(250, 30));
        campoCargo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(azul, 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        painel.add(campoCargo, gbc);
    }

    private void criarCampoUsuario(JPanel painel, GridBagConstraints gbc, Color azul) {
        JLabel labelUsername = new JLabel("Nome de usuário:");
        labelUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 5;
        painel.add(labelUsername, gbc);

        campoUsername = new JTextField();
        campoUsername.setPreferredSize(new Dimension(250, 30));
        campoUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(azul, 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        painel.add(campoUsername, gbc);

        JLabel labelSenha = new JLabel("Senha:");
        labelSenha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 7;
        painel.add(labelSenha, gbc);

        campoSenha = new JPasswordField();
        campoSenha.setPreferredSize(new Dimension(250, 30));
        campoSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(azul, 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 8;
        painel.add(campoSenha, gbc);

        JLabel labelTipo = new JLabel("Tipo de usuário:");
        labelTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 9;
        painel.add(labelTipo, gbc);

        comboTipo = new JComboBox<>(TipoUsuario.values());
        comboTipo.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        painel.add(comboTipo, gbc);
    }

    private void salvarFuncionario(boolean isAdmin) {
        String nome = campoNome.getText().trim();
        String cargo = campoCargo.getText().trim();

        if (nome.isEmpty() || cargo.isEmpty()) {
            JOptionPane.showMessageDialog(this, MSG_CAMPOS_OBRIGATORIOS, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            funcionarioController.cadastrarFuncionario(nome, cargo);

            if (isAdmin) {
                cadastrarUsuario();
            }

            JOptionPane.showMessageDialog(this, MSG_SUCESSO);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CADASTRAR + ex.getMessage());
        }
    }

    private void cadastrarUsuario() {
        String username = campoUsername.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        TipoUsuario tipo = (TipoUsuario) comboTipo.getSelectedItem();

        try {
            assert usuarioController != null;
            usuarioController.cadastrarUsuario(username, senha, tipo);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CADASTRAR + ex.getMessage());
        }
    }
}
