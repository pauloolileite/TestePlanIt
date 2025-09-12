package View;

import Controller.UsuarioController;
import Model.Usuario;
import Utils.DatabaseConnection;
import Utils.ImagePanel;
import Utils.PainelGradiente;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.Connection;
import java.util.Objects;

public class TelaLogin extends JFrame {
    private static final String MSG_LOGIN_SUCESSO = "Login realizado com sucesso!";
    private static final String MSG_ERRO_AUTENTICACAO = "Erro ao autenticar: ";
    private static final String MSG_ERRO_INICIALIZACAO = "Erro ao iniciar aplicação: ";

    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private UsuarioController controller;

    public TelaLogin() {
        setTitle("Login");
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new UsuarioController(conexao); // Instância da clase

            JPanel painelEsquerdo = new PainelGradiente(new Color(43, 36, 150), new Color(33, 124, 188));
            painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
            painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 30));

            ImagePanel logoPanel = new ImagePanel(
                    getClass().getResource("/Imagens/PlanIt_Branco_quadrado.png"),
                    350, 350, SwingConstants.CENTER
            );
            logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelEsquerdo.add(Box.createVerticalGlue());
            painelEsquerdo.add(logoPanel);
            painelEsquerdo.add(Box.createVerticalGlue());

            JPanel painelDireito = new JPanel(new GridBagLayout());
            painelDireito.setBackground(new Color(245, 245, 245));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 20, 10, 20);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel titulo = new JLabel("Login");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
            titulo.setForeground(new Color(33, 124, 188));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            painelDireito.add(titulo, gbc);

            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridwidth = 1;

            JLabel labelUsuario = new JLabel("Usuário");
            labelUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 1;
            painelDireito.add(labelUsuario, gbc);

            campoUsuario = new JTextField();
            campoUsuario.setPreferredSize(new Dimension(200, 30));
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            painelDireito.add(campoUsuario, gbc);

            JLabel labelSenha = new JLabel("Senha");
            labelSenha.setFont(new Font("Segoe UI", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 3;
            painelDireito.add(labelSenha, gbc);

            campoSenha = new JPasswordField();
            campoSenha.setPreferredSize(new Dimension(200, 30));
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            painelDireito.add(campoSenha, gbc);

            Border campoBorder = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(33, 124, 188), 1, true),
                    BorderFactory.createEmptyBorder(6, 6, 6, 6)
            );
            campoUsuario.setBorder(campoBorder);
            campoSenha.setBorder(campoBorder);

            JButton botaoLogin = new JButton("Login");
            botaoLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
            botaoLogin.setBackground(new Color(33, 124, 188));
            botaoLogin.setForeground(Color.WHITE);
            botaoLogin.setFocusPainted(false);
            botaoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoLogin.setPreferredSize(new Dimension(100, 30));
            botaoLogin.setBorder(BorderFactory.createLineBorder(new Color(33, 124, 188), 1, true));
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            painelDireito.add(botaoLogin, gbc);

            botaoLogin.addActionListener(e -> realizarLogin());
            getRootPane().setDefaultButton(botaoLogin);

            add(painelEsquerdo);
            add(painelDireito);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_INICIALIZACAO + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarLogin() {
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());

        try {
            Usuario u = controller.autenticarUsuario(usuario, senha);
            JOptionPane.showMessageDialog(this, MSG_LOGIN_SUCESSO);
            dispose();
            new TelaPrincipal(u, controller.getConexao()).setVisible(true);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_AUTENTICACAO + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
