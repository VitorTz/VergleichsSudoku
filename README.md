# VergleichsSudoku

---

    [VergleichsSudoku](https://www.janko.at/Raetsel/Sudoku/Vergleich/index.htm) é um quebra-cabeça lógico semelhante ao Sudoku. Para além de todas as regras do jogo Sudoku, o jogador deve inserir um número em cada campo, para que todas as condições de comparação sejam atendidas.

---

# Regras
- Escreva números de 1 a N nas células da grade de tamanho NxN, para que cada número ocorra exatamente uma vez em cada linha, em cada coluna e em cada região.
- Os sinais ">" entre duas células indicam que o número na célula que aponta é maior que o número na célula apontada.

---

# Exemplo (9x9)

## Vazio
<div>
    <img  src="images/exemplo-vazio.png" ></img>
</div>

## Completo

<div><img  src="images/exemplo-completo.png" ></img></div>

---

# Solução

Esta solução, escrita na linguagem Scala, foi desenvolvida para o trabalho II da disciplina Paradigmas de Programação. Para tal, foi se adaptado o algoritmo backtracking que soluciona um tabuleiro qualquer de Sudoku.