import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class RegistroSimplificado {
    private ArrayList<NaoConformidadeSimplificada> registros;
    private final String ARQUIVO_DADOS = "registros.dat";
    private int proximoId;

    public RegistroSimplificado() {
        registros = new ArrayList<>();
        carregarRegistros();
        proximoId = calcularProximoId();
    }

    private int calcularProximoId() {
        return registros.stream()
                .mapToInt(NaoConformidadeSimplificada::getId)
                .max()
                .orElse(0) + 1;
    }

    public void adicionar(String descricao, String responsavel, String prioridade, String status) {
        NaoConformidadeSimplificada nc = new NaoConformidadeSimplificada(proximoId, descricao, responsavel, prioridade, status);
        registros.add(nc);
        proximoId++;
        salvarRegistros();
    }

    public void atualizar(int id, String descricao, String responsavel, String prioridade, String status) {
        registros.stream()
                .filter(nc -> nc.getId() == id)
                .findFirst()
                .ifPresent(nc -> {
                    nc.setDescricao(descricao);
                    nc.setResponsavel(responsavel);
                    nc.setPrioridade(prioridade);
                    nc.setStatus(status);
                    salvarRegistros();
                });
    }

    public void deletar(int id) {
        registros.removeIf(nc -> nc.getId() == id);
        salvarRegistros();
    }

    public ArrayList<NaoConformidadeSimplificada> listarTodos() {
        return new ArrayList<>(registros);
    }

    public ArrayList<NaoConformidadeSimplificada> listarPorStatus(String status) {
        if (status.equals("Todos")) {
            return new ArrayList<>(registros);
        }
        return registros.stream()
                .filter(nc -> nc.getStatus().equals(status))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void carregarRegistros() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                registros = (ArrayList<NaoConformidadeSimplificada>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao carregar registros: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                registros = new ArrayList<>();
            }
        }
    }

    private void salvarRegistros() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(registros);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao salvar registros: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}