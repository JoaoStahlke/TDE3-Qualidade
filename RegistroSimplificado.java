import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistroSimplificado {
    private ArrayList<NaoConformidadeSimplificada> registros;
    private final String ARQUIVO_DADOS = "registros.dat";
    private int proximoId;
    private Properties smtpProperties;

    public RegistroSimplificado() {
        registros = new ArrayList<>();
        carregarRegistros();
        proximoId = calcularProximoId();
        configurarSMTP();
    }

    private void configurarSMTP() {
        smtpProperties = new Properties();

        // Configurações para o Gmail
        smtpProperties.put("mail.smtp.host", "smtp.gmail.com");
        smtpProperties.put("mail.smtp.port", "587");
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");

        // Verificar se as classes necessárias estão disponíveis
        try {
            Class.forName("javax.mail.internet.MimeMessage");
            Class.forName("javax.activation.DataHandler");
            System.out.println("JavaMail e JAF estão corretamente configurados.");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: JavaMail ou JAF não estão corretamente configurados.");
            System.err.println("Por favor, verifique se os JARs necessários estão no classpath.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro na configuração do e-mail. Verifique o console para mais detalhes.",
                    "Erro de Configuração",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calcularProximoId() {
        return registros.stream()
                .mapToInt(NaoConformidadeSimplificada::getId)
                .max()
                .orElse(0) + 1;
    }

    public void adicionar(String descricao, String responsavel, String prioridade, String status) {
        int tempoCorrecao = calcularTempoCorrecao(prioridade);
        LocalDate dataPrevisaoCorrecao = LocalDate.now().plusDays(tempoCorrecao);
        NaoConformidadeSimplificada nc = new NaoConformidadeSimplificada(proximoId, descricao, responsavel, prioridade, status, tempoCorrecao, dataPrevisaoCorrecao);
        registros.add(nc);
        proximoId++;
        salvarRegistros();
        if (status.equals("Não conforme")) {
            enviarEmailNotificacao(nc);
        }
    }

    private int calcularTempoCorrecao(String prioridade) {
        switch (prioridade) {
            case "Alta":
                return 2;
            case "Média":
                return 5;
            case "Baixa":
                return 10;
            default:
                return 0;
        }
    }

    private void enviarEmailNotificacao(NaoConformidadeSimplificada nc) {
        String to = nc.getResponsavel() + "@gmail.com"; // Altere para o domínio de e-mail apropriado
        String from = "alexandretestespuc@gmail.com"; // Seu endereço de e-mail
        final String username = "alexandretestespuc@gmail.com"; // Seu endereço de e-mail
        final String password = "ohrz blcx xvis zltp"; // Sua senha de aplicativo

        Session session = Session.getInstance(smtpProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Nova Não Conformidade Registrada - ID: " + nc.getId());
            String htmlContent = String.format(
                    "<html>" +
                            "<head>" +
                            "<style>" +
                            "body { font-family: Arial, sans-serif; }" +
                            "h1 { color: #003366; }" +
                            "table { border-collapse: collapse; width: 100%%; }" + // Note o %% aqui
                            "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                            "th { background-color: #f2f2f2; color: #003366; }" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<h1>Nova Não Conformidade Registrada</h1>" +
                            "<p>Uma nova Não Conformidade foi registrada no sistema. Detalhes abaixo:</p>" +
                            "<table>" +
                            "<tr><th>ID</th><td>%d</td></tr>" +
                            "<tr><th>Descrição</th><td>%s</td></tr>" +
                            "<tr><th>Prioridade</th><td>%s</td></tr>" +
                            "<tr><th>Tempo para correção</th><td>%d dias</td></tr>" +
                            "<tr><th>Data de criação</th><td>%s</td></tr>" +
                            "<tr><th>Data prevista para correção</th><td>%s</td></tr>" +
                            "</table>" +
                            "<p>Por favor, tome as medidas necessárias para corrigir esta não conformidade dentro do prazo estabelecido.</p>" +
                            "<p>Este é um e-mail automático. Não responda a esta mensagem.</p>" +
                            "</body>" +
                            "</html>",
                    nc.getId(),
                    nc.getDescricao(),
                    nc.getPrioridade(),
                    nc.getTempoCorrecao(),
                    nc.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    nc.getDataPrevisaoCorrecao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );

            // Configurando o conteúdo da mensagem como HTML
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("E-mail enviado com sucesso para " + to);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erro ao enviar e-mail: " + mex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

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
                    nc.setTempoCorrecao(calcularTempoCorrecao(prioridade));
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