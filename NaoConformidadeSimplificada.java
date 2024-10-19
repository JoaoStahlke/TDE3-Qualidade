import java.io.Serializable;
import java.time.LocalDate;

public class NaoConformidadeSimplificada implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String descricao;
    private String responsavel;
    private String prioridade;
    private String status;
    private LocalDate dataCriacao;
    private LocalDate dataFechamento;
    private int tempoCorrecao;

    public NaoConformidadeSimplificada(int id, String descricao, String responsavel, String prioridade, String status, int tempoCorrecao) {
        this.id = id;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.prioridade = prioridade;
        this.status = status;
        this.dataCriacao = LocalDate.now();
        this.tempoCorrecao = tempoCorrecao;
        if (status.equals("conforme")) {
            this.dataFechamento = LocalDate.now();
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public LocalDate getDataFechamento() { return dataFechamento; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public int getTempoCorrecao() { return tempoCorrecao; }
    public void setTempoCorrecao(int tempoCorrecao) { this.tempoCorrecao = tempoCorrecao; }

    @Override
    public String toString() {
        String info = String.format("ID: %d | Descrição: %s | Responsável: %s | Prioridade: %s | Status: %s | Criada em: %s | Tempo para correção: %d dias",
                id, descricao, responsavel, prioridade, status, dataCriacao, tempoCorrecao);
        if (dataFechamento != null) {
            info += String.format(" | Fechada em: %s", dataFechamento);
        }
        return info;
    }
}