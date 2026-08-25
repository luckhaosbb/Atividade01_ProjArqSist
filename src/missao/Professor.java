package missao;

public class Professor extends Passageiro {

    public Professor(String nome, int x, int y) {
        super(nome, "Professor", x, y);
    }

    @Override
    public int calcularPontuacao() {
        return 10;
    }

    @Override
    public char getSimbolo() {
        return 'P';
    }
}
