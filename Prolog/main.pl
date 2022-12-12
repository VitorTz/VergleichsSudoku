% Vitor Fernando da Silva (20201566)

:-  use_module(library(clpfd)), initialization(main).



% Entrada do Puzzle número 11 
% https://www.janko.at/Raetsel/Sudoku/Vergleich/011.a.htm
matrizComparacoes(C) :-
    C = [[['.', '.', '<', '>'], ['>', '.', '>', '<'], ['<', '.', '.', '<'], ['.', '.', '<', '>'], ['>', '.', '<', '>'], ['>', '.', '.', '>'], ['.', '.', '>', '<'], ['<', '.', '>', '>'], ['<', '.', '.', '<']],
    [['.', '<', '<', '<'], ['>', '>', '<', '<'], ['>', '>', '.', '<'], ['.', '<', '<', '<'], ['>', '<', '>', '>'], ['<', '<', '.', '>'], ['.', '>', '>', '>'], ['<', '<', '<', '>'], ['>', '>', '.', '>']],
    [['.', '>', '<', '.'], ['>', '>', '<', '.'], ['>', '>', '.', '.'], ['.', '>', '<', '.'], ['>', '<', '>', '.'], ['<', '<', '.', '.'], ['.', '<', '>', '.'], ['<', '<', '<', '.'], ['>', '<', '.', '.']],
    [['.', '.', '>', '>'], ['<', '.', '>', '<'], ['<', '.', '.', '<'], ['.', '.', '>', '>'], ['<', '.', '<', '>'], ['>', '.', '.', '>'], ['.', '.', '<', '<'], ['>', '.', '>', '>'], ['<', '.', '.', '<']],
    [['.', '<', '>', '>'], ['<', '>', '<', '<'], ['>', '>', '.', '<'], ['.', '<', '>', '<'], ['<', '<', '<', '<'], ['>', '<', '.', '>'], ['.', '>', '>', '>'], ['<', '<', '>', '>'], ['<', '>', '.', '>']],
    [['.', '<', '<', '.'], ['>', '>', '>', '.'], ['<', '>', '.', '.'], ['.', '>', '<', '.'], ['>', '>', '>', '.'], ['<', '<', '.', '.'], ['.', '<', '>', '.'], ['<', '<', '<', '.'], ['>', '<', '.', '.']],
    [['.', '.', '<', '>'], ['>', '.', '>', '>'], ['<', '.', '.', '>'], ['.', '.', '>', '<'], ['<', '.', '>', '>'], ['<', '.', '.', '>'], ['.', '.', '<', '<'], ['>', '.', '>', '<'], ['<', '.', '.', '<']],
    [['.', '<', '>', '<'], ['<', '<', '<', '<'], ['>', '<', '.', '>'], ['.', '>', '>', '>'], ['<', '<', '<', '>'], ['>', '<', '.', '<'], ['.', '>', '<', '>'], ['>', '>', '<', '<'], ['>', '>', '.', '>']],
    [['.', '>', '<', '.'], ['>', '>', '>', '.'], ['<', '<', '.', '.'], ['.', '<', '<', '.'], ['>', '<', '<', '.'], ['>', '>', '.', '.'], ['.', '<', '<', '.'], ['>', '>', '>', '.'], ['<', '<', '.', '.']]].



% Acessa a posição dentro de uma lista normal
enesimoLista(0, [H|_], H).
enesimoLista(I, [_|T], X) :- I2 is I - 1, enesimoLista(I2, T, X).
% Acessa a posição dentro de uma matriz
enesimoMatriz(I, J, Lista, X) :- enesimoLista(I, Lista, R), enesimoLista(J, R, X).


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


main :- matrizComparacoes(Mcomparacoes),
    vergleichssudoku(P, Mcomparacoes),
    maplist(label, P), maplist(portray_clause, P),
    halt.
