import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class InterfaceSimplificada extends JFrame {
    private RegistroSimplificado registro;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JTextField descricaoField;
    private JComboBox<String> statusComboBox;
    private JTextField idField;
    private JTextField responsavelField;
    private JComboBox<String> prioridadeComboBox;
    private JLabel indicadorConformidade;

    public InterfaceSimplificada() {
        registro = new RegistroSimplificado();
        configurarJanela();
        inicializarComponentes();
        atualizarTabela();
    }

    private void configurarJanela() {
        setTitle("Gestão de Não Conformidades - Versão Simplificada");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel painelVisualizacao = criarPainelVisualizacao();
        tabbedPane.addTab("Visualização", painelVisualizacao);
        JPanel painelCadastro = criarPainelCadastro();
        tabbedPane.addTab("Cadastro/Edição", painelCadastro);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarPainelVisualizacao() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));

        // Painel superior com filtros e indicador de conformidade
        JPanel painelSuperior = new JPanel(new BorderLayout(10, 10));

        // Painel de filtros
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> filtroStatus = new JComboBox<>(new String[]{"Todos", "Não conforme", "Conforme", "Não se aplica"});

        filtroStatus.addActionListener(e -> atualizarTabela(filtroStatus.getSelectedItem().toString()));

        painelFiltros.add(new JLabel("Filtrar por status:"));
        painelFiltros.add(filtroStatus);

        // Painel do indicador de conformidade
        JPanel painelIndicador = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        indicadorConformidade = new JLabel();
        painelIndicador.add(indicadorConformidade);

        // Adiciona os painéis ao painel superior
        painelSuperior.add(painelFiltros, BorderLayout.WEST);
        painelSuperior.add(painelIndicador, BorderLayout.EAST);

        // Tabela
        String[] colunas = {"ID", "Descrição", "Responsável", "Prioridade", "Status", "Data Criação"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        // Adicionar listener de clique duplo na tabela
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row != -1) {
                        idField.setText(tabela.getValueAt(row, 0).toString());
                        descricaoField.setText(tabela.getValueAt(row, 1).toString());
                        responsavelField.setText(tabela.getValueAt(row, 2).toString());
                        prioridadeComboBox.setSelectedItem(tabela.getValueAt(row, 3).toString());
                        statusComboBox.setSelectedItem(tabela.getValueAt(row, 4).toString());

                        JTabbedPane tabbedPane = (JTabbedPane) InterfaceSimplificada.this.getContentPane().getComponent(0);
                        tabbedPane.setSelectedIndex(1);
                    }
                }
            }
        });

        painel.add(painelSuperior, BorderLayout.NORTH);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de entrada
        idField = new JTextField(5);
        descricaoField = new JTextField(30);
        responsavelField = new JTextField(20);
        statusComboBox = new JComboBox<>(new String[]{"Não conforme", "Conforme", "Não se aplica"});
        prioridadeComboBox = new JComboBox<>(new String[]{"Alta", "Média", "Baixa"});

        // Layout dos campos
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        painel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        painel.add(descricaoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        painel.add(new JLabel("Responsável:"), gbc);
        gbc.gridx = 1;
        painel.add(responsavelField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(new JLabel("Prioridade:"), gbc);
        gbc.gridx = 1;
        painel.add(prioridadeComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        painel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        painel.add(statusComboBox, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnDeletar = new JButton("Deletar");
        JButton btnLimpar = new JButton("Limpar");

        btnAdicionar.addActionListener(e -> adicionarRegistro());
        btnAtualizar.addActionListener(e -> atualizarRegistro());
        btnDeletar.addActionListener(e -> deletarRegistro());
        btnLimpar.addActionListener(e -> limparCampos());

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        painel.add(painelBotoes, gbc);

        return painel;
    }

    private void adicionarRegistro() {
        String descricao = descricaoField.getText().trim();
        String responsavel = responsavelField.getText().trim();
        String prioridade = (String) prioridadeComboBox.getSelectedItem();
        String status = (String) statusComboBox.getSelectedItem();

        if (descricao.isEmpty() || responsavel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        registro.adicionar(descricao, responsavel, prioridade, status);
        limparCampos();
        atualizarTabela();
    }

    private void atualizarRegistro() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String descricao = descricaoField.getText().trim();
            String responsavel = responsavelField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();
            String prioridade = (String) prioridadeComboBox.getSelectedItem();

            if (descricao.isEmpty() || responsavel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos obrigatórios.");
                return;
            }

            registro.atualizar(id, descricao, responsavel, prioridade, status);
            limparCampos();
            atualizarTabela();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.");
        }
    }

    private void deletarRegistro() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            int confirmacao = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja deletar o registro #" + id + "?",
                    "Confirmar exclusão",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                registro.deletar(id);
                limparCampos();
                atualizarTabela();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.");
        }
    }

    private void limparCampos() {
        idField.setText("");
        descricaoField.setText("");
        responsavelField.setText("");
        statusComboBox.setSelectedIndex(0);
        prioridadeComboBox.setSelectedIndex(0);
    }

    private void atualizarTabela() {
        atualizarTabela("Todos");
    }

    private void atualizarTabela(String filtroStatus) {
        modeloTabela.setRowCount(0);
        ArrayList<NaoConformidadeSimplificada> lista = registro.listarPorStatus(filtroStatus);

        for (NaoConformidadeSimplificada nc : lista) {
            modeloTabela.addRow(new Object[]{
                    nc.getId(),
                    nc.getDescricao(),
                    nc.getResponsavel(),
                    nc.getPrioridade(),
                    nc.getStatus(),
                    nc.getDataCriacao(),
                    nc.getDataFechamento()
            });
        }

        atualizarIndicadorConformidade();
    }

    private void atualizarIndicadorConformidade() {
        ArrayList<NaoConformidadeSimplificada> todasNCs = registro.listarTodos();
        long totalRelevante = todasNCs.stream()
                .filter(nc -> !nc.getStatus().equals("Não se aplica"))
                .count();

        if (totalRelevante > 0) {
            long totalConformes = todasNCs.stream()
                    .filter(nc -> nc.getStatus().equals("Conforme"))
                    .count();

            double percentualConformidade = (totalConformes * 100.0) / totalRelevante;
            indicadorConformidade.setText(String.format("Porcentagem de Aderência: %.1f%%", percentualConformidade));
        } else {
            indicadorConformidade.setText("Porcentagem de Aderência: N/A");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfaceSimplificada().setVisible(true);
        });
    }
}