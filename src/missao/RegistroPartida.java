package missao;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroPartida {
    private String dataHora;
    private int passageirosResgatados;
    private Dificuldade dificuldade;
    private int pontuacaoFinal;

    public RegistroPartida(int passageiros, Dificuldade dificuldade, int pontuacao) {
        this.passageirosResgatados = passageiros;
        this.dificuldade = dificuldade;
        this.pontuacaoFinal = pontuacao;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.dataHora = LocalDateTime.now().format(formatter);
    }

    public String paraTexto() {
        return String.format("[%s] Dificuldade: %s | Passageiros Resgatados: %d | Pontuação Final: %d",
                dataHora, dificuldade, passageirosResgatados, pontuacaoFinal);
    }
}