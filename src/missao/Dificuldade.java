package missao;

public enum Dificuldade {
    FACIL(100, 500, 5),
    MEDIO(50, 200, 10),
    DIFICIL(20, 20, 20);

    private final int recursosIniciais;
    private final int pontuacaoInicial;
    private final int quantidadeObstaculos;

    Dificuldade(int recursos, int pontuacao, int obstaculos) {
        this.recursosIniciais = recursos;
        this.pontuacaoInicial = pontuacao;
        this.quantidadeObstaculos = obstaculos;
    }

    public int getRecursosIniciais() { return recursosIniciais; }
    public int getPontuacaoInicial() { return pontuacaoInicial; }
    public int getQuantidadeObstaculos() { return quantidadeObstaculos; }
}