package View;

import Controller.AgendamentoController;
import Model.Agendamento;
import Model.Cliente;
import Model.Funcionario;
import Utils.DataUtils;
import Utils.TabelaUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.List;
import java.util.Objects;

public class TelaCancelarAgendamento extends JFrame {

    private static final String MSG_ERRO_CONEXAO = "Erro ao conectar: ";
    private static final String MSG_ERRO_CARREGAMENTO = "Erro ao carregar agendamentos: ";
    private static final String MSG_CONFIRMACAO = "Deseja realmente cancelar o agendamento?";
    private static final String MSG_SUCESSO_CANCELAMENTO = "Agendamento cancelado com sucesso!";
    private static final String MSG_ERRO_CANCELAMENTO = "Erro ao cancelar: ";
    private static final String MSG_NENHUMA_SELECAO = "Selecione um agendamento para cancelar.";
    private static final String MSG_TABELA_VAZIA = "Nenhum agendamento ativo encontrado.";

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private AgendamentoController controller;

    public TelaCancelarAgendamento() {
        setTitle("Cancelar Agendamento");
        setSize(800, 500);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/plan_it.png"))).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color roxo = new Color(75, 0, 130);
        Color azul = new Color(33, 124, 188);
        Color vermelho = new Color(255, 0, 0);

        try {
            Connection conexao = DatabaseConnection.getConnection();
            controller = new AgendamentoController(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CONEXAO + e.getMessage());
            return;
        }

        JLabel titulo = new JLabel("Cancelar Agendamento", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(azul);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Profissional", "Serviço", "Data", "Hora"}, 0
        );
        tabela = new JTable(modeloTabela);
        TabelaUtils.aplicar(tabela, roxo);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(Color.WHITE);

        JButton botaoCancelar = new JButton("Cancelar Agendamento");
        botaoCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botaoCancelar.setFocusPainted(false);
        botaoCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoCancelar.setBackground(vermelho);
        botaoCancelar.setForeground(Color.WHITE);
        botaoCancelar.setPreferredSize(new Dimension(220, 40));
        botaoCancelar.setBorder(BorderFactory.createLineBorder(vermelho, 1, true));

        painelBotoes.add(botaoCancelar);
        JPanel painelComEspaco = new JPanel(new BorderLayout());
        painelComEspaco.setBackground(Color.WHITE);
        painelComEspaco.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        painelComEspaco.add(painelBotoes, BorderLayout.CENTER);
        add(painelComEspaco, BorderLayout.SOUTH);

        botaoCancelar.addActionListener(e -> cancelarAgendamentoSelecionado());

        listarAgendamentosAtivos();
    }

    private void listarAgendamentosAtivos() {
        try {
            List<Agendamento> lista = controller.listarAgendamentos();
            preencherTabela(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, MSG_ERRO_CARREGAMENTO + e.getMessage());
        }
    }

    private void preencherTabela(List<Agendamento> lista) {
        modeloTabela.setRowCount(0);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, MSG_TABELA_VAZIA, "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Agendamento a : lista) {
            Cliente c = a.getCliente();
            Funcionario f = a.getFuncionario();
            modeloTabela.addRow(new Object[]{
                    a.getId(),
                    c.getNome(),
                    f.getNome(),
                    a.getServico(),
                    DataUtils.formatarData(a.getData()),
                    a.getHora()
            });
        }
    }

    private void cancelarAgendamentoSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada != -1) {
            int id = (int) tabela.getValueAt(linhaSelecionada, 0);
            int opcao = JOptionPane.showConfirmDialog(this, MSG_CONFIRMACAO, "Confirmação", JOptionPane.YES_NO_OPTION);
            if (opcao == JOptionPane.YES_OPTION) {
                try {
                    controller.excluirAgendamento(id);
                    modeloTabela.removeRow(linhaSelecionada);
                    JOptionPane.showMessageDialog(this, MSG_SUCESSO_CANCELAMENTO);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, MSG_ERRO_CANCELAMENTO + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, MSG_NENHUMA_SELECAO);
        }
    }
}
