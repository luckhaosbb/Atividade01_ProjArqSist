package missao;

public class Passageiro {
    private String nome;
    private String tipo;
    private int x;
    private int y;

    public Passageiro(String nome, String tipo, int x, int y) {
        this.nome = nome;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
    }

    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public int getX() { return x; }
    public int getY() { return y; }

    public int calcularPontuacao() {
        return 5;
    }

    public char getSimbolo() {
        return 'T';
    }

    public boolean estaNaPosicao(int x, int y) {
        return this.x == x && this.y == y;
    }
}
