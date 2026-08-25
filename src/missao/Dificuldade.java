package missao;

public enum Dificuldade {
    FACIL  ("FÁCIL",   5, 60, 5,  3, 1),
    MEDIO  ("MÉDIO",   3, 45, 8,  4, 2),
    DIFICIL("DIFÍCIL", 2, 35, 12, 5, 3);

    private final String rotulo;
    private final int recursosIniciais;
    private final int pontuacaoInicial;
    private final int quantidadeObstaculos;
    private final int quantidadePassageiros;
    private final int quantidadeInimigos;

    Dificuldade(String rotulo, int recursos, int pontuacao, int obstaculos,
                int passageiros, int inimigos) {
        this.rotulo = rotulo;
        this.recursosIniciais = recursos;
        this.pontuacaoInicial = pontuacao;
        this.quantidadeObstaculos = obstaculos;
        this.quantidadePassageiros = passageiros;
        this.quantidadeInimigos = inimigos;
    }

    public String getRotulo() { return rotulo; }

    /** Os "recursos" do enunciado são as vidas iniciais da nave. */
    public int getRecursosIniciais() { return recursosIniciais; }

    public int getPontuacaoInicial() { return pontuacaoInicial; }
    public int getQuantidadeObstaculos() { return quantidadeObstaculos; }
    public int getQuantidadePassageiros() { return quantidadePassageiros; }
    public int getQuantidadeInimigos() { return quantidadeInimigos; }

    public static Dificuldade porNome(String nome) {
        if (nome != null) {
            for (Dificuldade d : values()) {
                if (d.name().equalsIgnoreCase(nome.trim())) {
                    return d;
                }
            }
        }
        return MEDIO;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
