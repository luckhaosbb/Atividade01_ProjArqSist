# Missão Marte Unifor

Jogo em console desenvolvido em Java para a disciplina de Projeto e Arquitetura de
Sistemas (Unifor). O projeto estende a base oficial "Missão Marte Unifor" com os
Exercícios Práticos dos Níveis 1 a 4.

## Equipe: Fobos e Deimos

| Integrante          | Matrícula |
| ------------------- | --------- |
| Lucas Gomes         | 2522675   |
| Welington Da Silva  | 2522697   |

## Repositório GitHub

- https://github.com/luckhaosbb/Atividade01_ProjArqSist

## Requisitos

- JDK 11 ou superior (o código não usa recursos além do Java 11 nem bibliotecas externas)

## Como compilar e executar

A partir da **raiz do projeto**:

```bash
# 1. Compilar (gera os .class em bin/)
javac -encoding UTF-8 -d bin $(find src -name "*.java")

# 2. Executar
java -Dfile.encoding=UTF-8 -cp bin missao.Main
```

No Windows (PowerShell):

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -Dfile.encoding=UTF-8 -cp bin missao.Main
```

> O mapa usa caracteres UTF-8 (`∆`, `§`). Se os símbolos aparecerem trocados no
> terminal, garanta que o console esteja em UTF-8 (`chcp 65001` no Windows).

## Como jogar

No menu principal escolha **1 - Iniciar Nova Missão**, informe o nome do piloto,
a dificuldade e o tamanho do mapa.

| Comando | Ação                              |
| ------- | --------------------------------- |
| `w`     | mover para cima                   |
| `s`     | mover para baixo                  |
| `a`     | mover para a esquerda             |
| `d`     | mover para a direita              |
| `c`     | embarcar o passageiro da casa     |
| `q`     | abandonar a missão                |

**Objetivo:** embarcar todos os passageiros e depois voltar à Plataforma de Pouso
`L` na coordenada `(0,0)`. Cada movimento custa 1 ponto; colidir com asteroide ou
com o alienígena custa 1 vida.

### Legenda do mapa

| Símbolo | Elemento                    |
| ------- | --------------------------- |
| `∆`     | Nave                        |
| `L`     | Plataforma de Pouso (0,0)   |
| `§`     | Inimigo (alienígena)        |
| `P`     | Professor (+10 pontos)      |
| `E`     | Engenheiro (+15 pontos)     |
| `A`     | Astronauta (+20 pontos)     |
| `#`     | Asteroide                   |
| `.`     | Espaço vazio                |

## Funcionalidades implementadas

### Nível 1 — Refatoração e Tipos

| # | Exercício          | Onde                                                   |
| - | ------------------ | ------------------------------------------------------ |
| 1 | Ajuste de capacidade | `Nave.CAPACIDADE_PADRAO = 5`                         |
| 2 | Subclasse `Astronauta` | `Astronauta.java`, criada em `Main.criarPassageiro` |
| 3 | Customização visual  | `Main.simboloDaCasa` (`∆` nave, `#` asteroide)       |

### Nível 2 — Mecânicas de Jogo e POO

| # | Exercício              | Onde                                                        |
| - | ---------------------- | ----------------------------------------------------------- |
| 4 | Pontuação polimórfica  | `calcularPontuacao()` sobrescrito: Professor +10, Engenheiro +15, Astronauta +20 |
| 5 | Sistema de vidas       | `Nave.vidas`, `perderVida()`, `estaDestruida()`              |
| 6 | Mapa configurável      | `Main.iniciarNovaMissao` pergunta largura e altura (5 a 30)  |

### Nível 3 — Comportamentos Avançados e Persistência

| # | Exercício              | Onde                                                        |
| - | ---------------------- | ----------------------------------------------------------- |
| 7 | Inimigos com IA simples | `Inimigo.mover()` + `Missao.moverInimigos()` a cada turno   |
| 8 | Enum `Dificuldade`      | `Dificuldade.java` ajusta vidas, pontos, asteroides, passageiros e inimigos |
| 9 | Persistência expandida  | `Ranking.java` + `RegistroPartida.paraJson()`               |

### Nível 4 — Desafio Final

| #  | Exercício                | Onde                                                       |
| -- | ------------------------ | ---------------------------------------------------------- |
| 10 | Plataforma de Pouso (0,0) | `Missao.missaoConcluida()` exige todos a bordo **e** nave em (0,0) |
| 10 | Menu principal e reset    | `Main.exibirMenuPrincipal()`, `Main.resetarRanking()`      |
| 10 | Estatísticas de fim       | `Main.exibirEstatisticas()`: duração, movimentos e recorde |

## Formato do `ranking.json`

O ranking guarda o Top 5 com os dados ampliados da partida (Exercício 9):

```json
[
  {
    "piloto": "Welington",
    "pontuacao": 77,
    "dataHora": "2026-08-24 21:01:31",
    "passageirosResgatados": 3,
    "totalPassageiros": 3,
    "dificuldade": "FACIL",
    "duracaoSegundos": 42,
    "movimentos": 28,
    "vitoria": true
  }
]
```

O arquivo é lido e gravado sem bibliotecas externas (parser manual em `Ranking.java`)
e continua aceitando o formato antigo (`{"name": ..., "score": ...}`).

## Estrutura do projeto

```
src/missao/
├── Main.java             # menu, loop da partida, renderização e estatísticas
├── Missao.java           # composição: nave, passageiros, asteroides, inimigos e plataforma
├── Nave.java             # posição, vidas, capacidade e movimentação com limites
├── Passageiro.java       # classe base
├── Professor.java        # +10 pontos
├── Engenheiro.java       # +15 pontos
├── Astronauta.java       # +20 pontos
├── Asteroide.java        # obstáculo estático
├── Inimigo.java          # movimentação aleatória a cada turno
├── Dificuldade.java      # enum FACIL / MEDIO / DIFICIL
├── RegistroPartida.java  # dados de uma partida + serialização JSON
└── Ranking.java          # leitura, gravação e reset do ranking.json
```
