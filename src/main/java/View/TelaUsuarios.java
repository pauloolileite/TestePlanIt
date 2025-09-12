package View;

import Controller.UsuarioController;
import Model.Usuario;
import Model.TipoUsuario;
import Utils.TabelaUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.List;
import java.util.Objects;

public class TelaUsuarios extends JFrame {

    private static final String MSG_ERRO_CONEXAO = "Erro ao conectar: ";
    private static final String MSG_ERRO_CARREGAR = "Erro ao carregar usuários: ";
    private static final String MSG_ERRO_ATUALIZAR = "Erro ao atualizar: ";
    private static final String MSG_ERRO_EXCLUIR = "Erro ao excluir: ";
    private static final String MSG_NENHUM_SELECIONADO = "Selecione um usuário.";
    private static final String MSG_CONFIRMA_EXCLUSAO = "Deseja excluir o usuário?";
    private static final String MSG_CAMPOS_OBRIGATORIOS = "Preencha todos os campos antes de salvar.";

    private JTable tabela;
    private DefaultTableModel modelo;
    private UsuarioController controller;

    public TelaUsuarios() {
        setTitle("Gerenciar Usuários");
        setSize(700, 500);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color roxo = new Color(75, 0, 130);
        Color azul = new Color(33, 124, 188);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new UsuarioController(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CONEXAO + e.getMessage());
            return;
        }

        JLabel titulo = new JLabel("Gerenciar Usuários", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(azul);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Usuário", "Senha", "Tipo"}, 0);
        tabela = new JTable(modelo);
        TabelaUtils.aplicar(tabela, roxo);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        painelBotoes.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        for (JButton btn : new JButton[]{btnEditar, btnExcluir}) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBackground(azul);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(azul, 1, true));
            btn.setPreferredSize(new Dimension(120, 35));
            painelBotoes.add(btn);
        }

        add(painelBotoes, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> editarUsuario());
        btnExcluir.addActionListener(e -> excluirUsuario());

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        try {
            List<Usuario> lista = controller.listarUsuarios();
            preencherTabela(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CARREGAR + e.getMessage());
        }
    }

    private void preencherTabela(List<Usuario> lista) {
        modelo.setRowCount(0);
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{u.getId(), u.getUsername(), u.getSenha(), u.getTipo()});
        }
    }

    private void editarUsuario() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            int id = (int) tabela.getValueAt(linha, 0);
            String usernameAtual = (String) tabela.getValueAt(linha, 1);
            String senhaAtual = (String) tabela.getValueAt(linha, 2);
            TipoUsuario tipoAtual = TipoUsuario.fromString(tabela.getValueAt(linha, 3).toString());

            String username = JOptionPane.showInputDialog(this, "Novo nome de usuário:", usernameAtual);
            String senha = JOptionPane.showInputDialog(this, "Nova senha:", senhaAtual);
            TipoUsuario tipo = (TipoUsuario) JOptionPane.showInputDialog(
                    this, "Tipo de usuário:", "Selecionar tipo",
                    JOptionPane.QUESTION_MESSAGE, null, TipoUsuario.values(), tipoAtual
            );

            if (username == null || username.trim().isEmpty() ||
                    senha == null || senha.trim().isEmpty() ||
                    tipo == null) {
                JOptionPane.showMessageDialog(this, MSG_CAMPOS_OBRIGATORIOS);
                return;
            }

            try {
                controller.atualizarUsuario(new Usuario(id, username.trim(), senha.trim(), tipo));
                carregarUsuarios();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, MSG_ERRO_ATUALIZAR + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, MSG_NENHUM_SELECIONADO);
        }
    }

    private void excluirUsuario() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            int id = (int) tabela.getValueAt(linha, 0);
            int confirm = JOptionPane.showConfirmDialog(this, MSG_CONFIRMA_EXCLUSAO, "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.excluirUsuario(id);
                    carregarUsuarios();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, MSG_ERRO_EXCLUIR + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, MSG_NENHUM_SELECIONADO);
        }
    }
}
