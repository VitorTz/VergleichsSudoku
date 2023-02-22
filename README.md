# Solução para o Puzzle [Vergleichssudoku](https://www.janko.at/Raetsel/Sudoku/Vergleich/index.htm)

---

> Trabalho feito para a disciplina de Paradigmas de Programação

    O VergleichsSudoku é uma variação do tradicional jogo de Sudoku, no qual, além das restrições tradicionais de que cada linha, coluna e bloco devem conter números únicos de 1 a 9, também são fornecidas algumas comparações adicionais entre as células do tabuleiro. Essas comparações são feitas utilizando símbolos matemáticos, como menor que (<), maior que (>), igual a (=) e diferentes de (≠), e devem ser respeitadas pelos números presentes nas células envolvidas.

    Para a implementação da solução em Scala, foi utilizado o algoritmo de backtracking, que é uma técnica de busca exaustiva utilizada para resolver problemas de decisão, como é o caso do VergleichsSudoku. O algoritmo consiste em construir iterativamente uma solução, testá-la e, caso seja inválida, retroceder para uma escolha anterior e fazer outra tentativa. Esse processo é repetido até que uma solução válida seja encontrada.

    Já para a implementação da solução em Prolog, foi utilizada a programação por restrições, que é uma técnica de programação declarativa utilizada para modelar problemas como um conjunto de restrições. O problema é então resolvido através da busca por uma solução que satisfaça todas as restrições impostas. No caso do VergleichsSudoku, as restrições são as regras do jogo, incluindo as comparações adicionais.

    Ambas as soluções apresentaram resultados satisfatórios. Vale ressaltar que a implementação em Prolog foi mais simples e sucinta do que a implementação em Scala, o que pode ser atribuído à natureza declarativa da programação por restrições.

    Em suma, o trabalho demonstrou a aplicação de duas técnicas de programação diferentes para a resolução de um mesmo problema, permitindo uma comparação entre elas e evidenciando as características de cada uma. Além disso, foi possível explorar as particularidades do puzzle VergleichsSudoku e sua resolução através da programação.

## Soluções

- [Scala](https://github.com/VitorTz/VergleichsSudoku/tree/main/Scala)

- [Prolog](https://github.com/VitorTz/VergleichsSudoku/tree/main/Prolog)
