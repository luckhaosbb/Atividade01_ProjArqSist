package missao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroPartida {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String piloto;
    private final int pontuacao;
    private final String dataHora;
    private final int passageirosResgatados;
    private final int totalPassageiros;
    private final Dificuldade dificuldade;
    private final long duracaoSegundos;
    private final int movimentos;
    private final boolean vitoria;

    public RegistroPartida(String piloto, int pontuacao, int passageirosResgatados,
                           int totalPassageiros, Dificuldade dificuldade,
                           long duracaoSegundos, int movimentos, boolean vitoria) {
        this(piloto, pontuacao, LocalDateTime.now().format(FORMATO), passageirosResgatados,
                totalPassageiros, dificuldade, duracaoSegundos, movimentos, vitoria);
    }

    public RegistroPartida(String piloto, int pontuacao, String dataHora, int passageirosResgatados,
                           int totalPassageiros, Dificuldade dificuldade,
                           long duracaoSegundos, int movimentos, boolean vitoria) {
        this.piloto = piloto;
        this.pontuacao = pontuacao;
        this.dataHora = dataHora;
        this.passageirosResgatados = passageirosResgatados;
        this.totalPassageiros = totalPassageiros;
        this.dificuldade = dificuldade;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
        this.vitoria = vitoria;
    }

    public String getPiloto() { return piloto; }
    public int getPontuacao() { return pontuacao; }
    public String getDataHora() { return dataHora; }
    public int getPassageirosResgatados() { return passageirosResgatados; }
    public int getTotalPassageiros() { return totalPassageiros; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public long getDuracaoSegundos() { return duracaoSegundos; }
    public int getMovimentos() { return movimentos; }
    public boolean isVitoria() { return vitoria; }

    public String paraTexto() {
        return String.format("%s - %d pontos | %s | %d/%d passageiros | %s | %ds | %d movimentos",
                piloto, pontuacao, dataHora, passageirosResgatados, totalPassageiros,
                dificuldade.getRotulo(), duracaoSegundos, movimentos);
    }

    public String paraJson() {
        return "{"
                + "\"piloto\":\"" + escapar(piloto) + "\","
                + "\"pontuacao\":" + pontuacao + ","
                + "\"dataHora\":\"" + escapar(dataHora) + "\","
                + "\"passageirosResgatados\":" + passageirosResgatados + ","
                + "\"totalPassageiros\":" + totalPassageiros + ","
                + "\"dificuldade\":\"" + dificuldade.name() + "\","
                + "\"duracaoSegundos\":" + duracaoSegundos + ","
                + "\"movimentos\":" + movimentos + ","
                + "\"vitoria\":" + vitoria
                + "}";
    }

    private static String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
