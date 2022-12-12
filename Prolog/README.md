# VergleichsSudoku

VergleichsSudoku é um quebra-cabeça lógico semelhante ao Sudoku. Para além de todas as regras do jogo Sudoku, o jogador deve inserir um número em cada campo, para que todas as condições de comparação sejam atendidas.

---

# Regras

1. Escreva números de 1 a N nas células da grade de tamanho NxN, para que cada número ocorra exatamente uma vez em cada linha, em cada coluna e em cada região.
2. Os sinais ">" entre duas células indicam que o número na célula que aponta é maior que o número na célula apontada.

---

# Solução

A solução foi desenvolvida com base na linguagem de programação Prolog. Tanto a abordagem declarativa da linguagem quanto o conceito de programação de restrições auxiliaram no desenvolvimento de uma solução rápida e simples.

Ao tratar do problema, vemos claramente que para cada posição no tabuleiro existe um conjunto finito de números inteiros que podem ser escolhidos para a posição em questão. Acontece que dentro deste conjunto existe uma grande parcela de números que, são escolhidos, resultam em uma solução incorreta. Deste modo, testar todos os possíveis números se mostra uma solução custosa e demorada.

Partindo desta observação, a programação de restrições tem como objetivo reduzir ao máximo o número de possíveis números dentro deste conjunto de soluções através da definição de critérios e restrições. Sendo assim, após sucessivas restrições diminuímos o domínio das possíveis soluções e chegamos ao conjunto que contém apenas as soluções válidas.

---

# Restrições

Predicado principal que define um tabuleiro válido:

```prolog
/*
 Definição o que é um tabuleiro válido de vergleichssudoku.
 Um possível tabuleiro válido de vergleichssudoku é uma matriz de
 ordem 9 onde cada linha, coluna e região contem todos os números 
 de 1 a 9 sem repetições. Além disso, cada posição deve satisfazer
 certas comparações (maior ou menor) com seus vizinhos de região.
 */
vergleichssudoku(Puzzle, Mcomparacoes) :-
    length(Puzzle, 9), % O tabuleiro tem 9 linhas
    maplist(same_length(Puzzle), Puzzle), % Cada linha 9 colunas
    append(Puzzle, TodosOsNumeros), % Concatena todos os números do tabuleiro em TodosOsNumeros
    TodosOsNumeros ins 1..9, % O tabuleiro tem apenas números entre 1 e 9
    regra_puzzle(Puzzle, Mcomparacoes, 0, 0), % O tabuleiro segue a regra do Puzzle vergleichs
    maplist(all_distinct, Puzzle), % Todas as linhas possuem números entre 1 e 9 sem repetições
    transpose(Puzzle, Columns), % Inverte as linhas e colunas
    maplist(all_distinct, Columns),  % Todas as colunas possuem números entre 1 e 9 sem repetições
    Puzzle = [As,Bs,Cs,Ds,Es,Fs,Gs,Hs,Is], % Nomeia todas as linhas da Matriz
    % A partir das linhas nomeadas, divide em 3 blocos de 3 linhas cada
    % Define que cada bloco contem 3 regiões que devem conter todos os números entre 1 e 9 sem repetições
    blocks(As, Bs, Cs), % Linhas (0-2)
    blocks(Ds, Es, Fs), % Linhas (3-5)
    blocks(Gs, Hs, Is). % Linhas (6-8)

```

Dentro deste trecho, podemos perceber as restrições aplicadas ao problema.

Primeiramente, foram descartados todos os números inteiros fora do intervalo fechado [1, 9]. A restrição pode ser encontrada no seguinte trecho:

```prolog
append(Puzzle, TodosOsNumeros), % Concatena todos os números do tabuleiro em TodosOsNumeros
TodosOsNumeros ins 1..9, % O tabuleiro tem apenas números entre 1 e 9
```

Depois, restringimos ainda mais o domínio para que cada linha e coluna possuam números entre 1 e 9 sem repetições.

```prolog
maplist(all_distinct, Puzzle), % Todas as linhas possuem números entre 1 e 9 sem repetições
transpose(Puzzle, Columns), % Inverte as linhas e colunas
maplist(all_distinct, Columns),  % Todas as colunas possuem números entre 1 e 9 sem repetições
```

Agora, aplicamos a restrição correspondente a regra especifica do Puzzle. Deste modo restringimos ainda mais o domínio das possíveis soluções de cada posição no tabuleiro para apenas os números que satisfazem as comparações corretas.

```prolog
regra_puzzle(Puzzle, Mcomparacoes, 0, 0), % O tabuleiro segue a regra do Puzzle vergleichs
```

```prolog
/* 
    Percorre todas posições da matriz e define as comparações referentes a 
    cada posição.
*/
regra_puzzle(_, _, 9, _).
regra_puzzle(Puzzle, Mcomparacoes, I, 9) :- N is I + 1, regra_puzzle(Puzzle, Mcomparacoes, N, 0).
regra_puzzle(Puzzle, Mcomparacoes, I, J) :- 
    enesimoMatriz(I, J, Puzzle, Num),
    define_numero_valido(Num, I, J, Puzzle, Mcomparacoes),
    N is J + 1,
    regra_puzzle(Puzzle, Mcomparacoes, I, N).
```

```prolog
/*Define a comparação a ser feita a cada posição dentro do tabuleiro*/
define_comparacao(_, _, _, _, '.').
define_comparacao(Num1, Puzzle, I, J, '>') :- enesimoMatriz(I, J, Puzzle, Num2), Num1 #> Num2.
define_comparacao(Num1, Puzzle, I, J, '<') :- enesimoMatriz(I, J, Puzzle, Num2), Num1 #< Num2.


/* Define o que é um número válido para a posição (I, J)*/
define_numero_valido(Num, I, J, Puzzle, Comparacoes) :- 
    % Pega a string que define as comparações entre os vizinhos de (I, J)
    enesimoMatriz(I, J, Comparacoes, String),
    % Pega as comparações das 4 direções (left, top, right, bottom)
    enesimoLista(0, String, C1),
    enesimoLista(1, String, C2),
    enesimoLista(2, String, C3),
    enesimoLista(3, String, C4),
    % Define onde estão os vizinhos dentro da matriz
    Left is J - 1,
    Top is I - 1,
    Right is J + 1,
    Bottom is I + 1,
    % Define as comparações a serem feitas em cada direção
    define_comparacao(Num, Puzzle, I, Left, C1),
    define_comparacao(Num, Puzzle, Top, J, C2),
    define_comparacao(Num, Puzzle, I, Right, C3),
    define_comparacao(Num, Puzzle, Bottom, J, C4).
```

Por fim, restringimos mais uma vez o domínio da solução para desconsiderar os números que se repetem na mesma região

```prolog
Puzzle = [As,Bs,Cs,Ds,Es,Fs,Gs,Hs,Is], % Nomeia todas as linhas da Matriz
    % A partir das linhas nomeadas, divide em 3 blocos de 3 linhas cada
    % Define que cada bloco contem 3 regiões que devem conter todos os números entre 1 e 9 sem repetições
    blocks(As, Bs, Cs), % Linhas (0-2)
    blocks(Ds, Es, Fs), % Linhas (3-5)
    blocks(Gs, Hs, Is). % Linhas (6-8)
```

```prolog
/*
    Recebe 3 linhas, cada uma com tamanhos iguais entre si que podem ser 9, 6, 3 ou 0
    Vai andando em blocos de 3 por essas linhas até chegar ao final.
    Pega os 3 primeiros itens de cada linha e diz que todos os 9 itens precisam ser diferentes.
    Ao final repete o processo para a calda ou o que sobrou da lista.
*/
blocks([], [], []).
blocks([N1,N2,N3|Ns1], [N4,N5,N6|Ns2], [N7,N8,N9|Ns3]) :-
    all_distinct([N1,N2,N3,N4,N5,N6,N7,N8,N9]),
    blocks(Ns1, Ns2, Ns3).
```

---

# Vantagens e Desvantagens Programação de Restrições

A programação de restrições se mostrou bastante eficaz ao resolver o problema, a partir dela se pode criar uma solução simples, curta e legível para o puzzle. A maior vantagem se dá justamente em se preocupar em declarar o que é o problema, se preocupando apenas com a lógica simples e bem estabelecida do problema e não com a implementação. 

Para este Puzzle em específico não se encontrou nenhum desvantagem em utilizar prolog e programação de restrições.

---

# Entrada e Saída

A entrada é informada dentro do código da solução. Se trata de uma matriz com as comparações a serem feitas. A saída se dá pelo próprio terminal.

---

# Vantagens e Desvantagens entre Funcional e Lógico

Um Puzzle como VergleichsSudoku possui regras e características bem estabelecidas que podem ser expressas como um conjunto de axiomas ou declarações lógicas. Deste modo, o paradigma lógico se mostra perfeito para a resolução do problema já que a solução pode ser expressa como resultado de proposições lógicas e não de cálculos matemáticos como é visto no paradigma funcional.
