package missao;

public class Engenheiro extends Passageiro {

    public Engenheiro(String nome, int x, int y) {
        super(nome, "Engenheiro", x, y);
    }

    @Override
    public int calcularPontuacao() {
        return 15;
    }

    @Override
    public char getSimbolo() {
        return 'E';
    }
}
