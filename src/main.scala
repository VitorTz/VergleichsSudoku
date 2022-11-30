import scala.io.Source
import scala.annotation.tailrec
import scala.util.control.Breaks.{break, breakable}


/**
 * Lê fum arquivo txt
 * */
private def readFile(fName: String): Array[String] = {
  val file = Source.fromFile(fName)
  val arr = file.getLines().toArray
  file.close()
  arr
}

/**
 * Guarda as informações necessárias para resolver o puzzle
 * @param puzzle: Matriz que guarda o puzzle (inicialmente vazia, todas as posição com 0)
 * @param comparacoes Matriz de mesma ordem que puzzle e possui uma string de comparacoes associada a cada posição em puzzle
 * @param fName Nome do arquivo do puzzle dentro da pasta puzzles/
 * @param ordem Ordem das matrizes
 * @param isComplete Status da solução do puzzle (completo, incompleto)
 * */
class Puzzle(
              val puzzle: Array[Array[Int]],  // Matriz do puzzle
              // Matriz de comparações para cada posição da matriz do puzzle
              val comparacoes: Array[Array[String]],
              val fName: String,  // Nome do arquivo para carregar o puzzle
              val ordem: Int,  // Ordem das matriz
              var isComplete: Boolean = false
            )


/**
 * Permite carregar um puzzle que está dentro da pasta puzzles/
 * */
object LoadPuzzle {

  /**
   * @param fName Nome do arquivo dentro da pasta puzzles/
   * @param ordem Ordem das matrizes (puzzle, comparacoes)
   * */
  def load(fName: String, ordem: Int): Puzzle = {
    Puzzle(
      puzzle = List.fill(ordem)(List.fill(ordem)(0).toArray).toArray,
      comparacoes = readFile(s"puzzles/$fName.txt").map( n => n.split(",")),
      fName,
      ordem
    )
  }

}


/**
 * Testa a solução após finalizada.
 * */
object TestSolution {

  // i e j devem guardar a posição do primeiro erro encontrado
  // (i = -1 e j = -1) = Não foi encontrado nenhum erro
  private class TestResult(val i: Int, val j: Int)

  /**
   * Faz uma comparação de igualdade entre cada posição de cada matriz (puzzle, correctPuzzle)
   * @param puzzle Instãncia de Puzzle, guarda a matriz do puzzle preenchida pelo algoritmo
   * @param correctPuzzle Matriz que guarda a solução pronta para o puzzle
   * */
  @tailrec
  private def evaluate(puzzle: Puzzle, correctPuzzle: Array[Array[Int]], i: Int = 0, j: Int = 0): TestResult = {
    if (i >= puzzle.ordem) TestResult(-1, -1)
    else if (j >= puzzle.ordem) this.evaluate(puzzle, correctPuzzle, i + 1)
    else if (puzzle.puzzle(i)(j) == correctPuzzle(i)(j)) evaluate(puzzle, correctPuzzle, i, j+1)
    else TestResult(i, j)
  }

  private def printPuzzle(puzzle: Puzzle): Unit = {
    puzzle.puzzle.foreach(
      n => println(n.mkString(", "))
    )
  }

  /**
   * Retorna a solução do puzzle guardada dentro da pasta puzzles/
   * @param fName Identificação do puzzle
   * */
  private def getCorrectPuzzle(fName: String): Array[Array[Int]] = {
    readFile(s"puzzles/$fName-solved.txt").map(
      n => n.toArray.map(_.asDigit)
    )
  }

  private def showTestAnswer(result: TestResult): Unit = {
    if (result.i == -1) {
      println("Resposta correta!")
    } else {
      println(s"Resposta incorreta! Erro -> (${result.i}, ${result.j})")
    }
  }

  def test(puzzle: Puzzle): Unit = {
    this.printPuzzle(puzzle)
    this.showTestAnswer(this.evaluate(puzzle, this.getCorrectPuzzle(puzzle.fName)))
    puzzle.isComplete = true
  }

}

/**
 * Solução implementada para resolver o puzzle
 * */
object Solution {

  /**
   * Recebe a posição (i, j) que está a esquerda, direita, cima ou baixo do número num
   * @param num Número na posição original (i, j)
   * @param i Posição deslocada para cima ou para baixo ou sem deslocamento
   * @param j Posição deslocada para a esquerda ou direita ou sem deslocamento
   * @param comparacao Comparação . ou > ou < Para > compara se num é maior que a posição deslocada (i, j)
   *                   Para < compara se num é menor que a posição deslocada (i, j)
   * */
  private def checkRegraHelper(puzzle: Puzzle, i: Int, j: Int, num: Int, comparacao: Char): Boolean = {
    if (comparacao == '>') {
      val num2 = puzzle.puzzle(i)(j)
      return num2 == 0 || num > num2
    } else if (comparacao == '<') {
      val num2 = puzzle.puzzle(i)(j)
      return num2 == 0 || num < num2
    }
    true
  }

  /**
   * Verifica se a regra do puzzle Vergleichs foi respeitada
   * Uma Matriz (chamada comparacoes), com o mesmo tamanho que o puzzle, contem em cada posição uma string
   * como '..<>'. Deste modo, cada celula na matriz do puzzle contem uma string correspondente
   * na mesma posição na matriz das comparações.
   * A partir deste correspondente, as comparações de maior ou menor são obtidas.
   * Exemplo:
   *      Para a string ..<>, se obtem que a posição (i, j) deve ser menor que a posição (i, j+1) e maior que a posição (i+1, j).
   *      Isto acontece pois:
   *      ..<> -> index 0 da string = left (i, j-1), como o char é '.' então nenhuma comparação é feita entre (i, j) e (i, j-1)
   *      ..<> -> index 1 da string = top (i-1, j), como o char é '.' então nenhuma comparação é feita entre (i, j) e (i-1, j)
   *      ..<> -> index 2 da string = right (i, j+1), como o char é '<' então (i, j) deve ser menor que (i, j+1)
   *      ..<> -> index 3 da string = bottom (i+1, j), como o char é '>' então (i, j) deve ser maior que (i+1, j)
   *
   * @param puzzle Instãncia de Puzzle que guarda as inforamções necessárias para solucionar o puzzle
   * @param i Linha da matriz
   * @param j Coluna da matriz
   * @param num Número que deseja aplicar a regra do puzzle
   */
  private def checkRegra(puzzle: Puzzle, i: Int, j: Int, num: Int): Boolean = {
    val comp = puzzle.comparacoes(i)(j)  // Obtem a string das comparações
    checkRegraHelper(puzzle, i, j - 1, num, comp(0)) &&
      checkRegraHelper(puzzle, i - 1, j, num, comp(1)) &&
      checkRegraHelper(puzzle, i, j + 1, num, comp(2)) &&
      checkRegraHelper(puzzle, i + 1, j, num, comp(3))
  }

  /**
   * Valida o número como se fosse um puzzle de sudoku 9x9 normal.
   * No final, acrescenta a regra especifica do puzzle Vergleichs
   * */
  private def isValidNum(puzzle: Puzzle, i: Int, j: Int, num: Int): Boolean = {
    if (puzzle.puzzle(i).contains(num))
      return false

    if (puzzle.puzzle.map(n => n(j)).contains(num))
      return false

    val row = (i / 3) * 3
    val col = (j / 3) * 3
    var box = true

    breakable {
      for (n <- row until row + 3) {
        for (m <- col until col + 3) {
          if (puzzle.puzzle(n)(m) == num) {
            box = false
            break()
          }
        }
      }
    }

    box && checkRegra(puzzle, i, j, num)
  }

  /**
   * Resolve o Puzzle respeitando as regras
   * @param puzzle Instãncia de Puzzle que guarda as inforamções necessárias para solucionar o puzzle
   * @param i Linha da matriz
   * @param j Coluna da matriz
   * */
  def solve(puzzle: Puzzle, i: Int = 0, j: Int = 0): Unit = {
    // Puzzle completo
    if (puzzle.isComplete) return
    // Passou por todas as posições do puzzle
    else if (i >= puzzle.ordem) TestSolution.test(puzzle)
    // Chegou ao final de uma coluna, seguir para a linha seguinte
    else if (j >= puzzle.ordem) solve(puzzle, i + 1)
    // Encontrou uma posição preenchida, seguir para a próxima coluna
    else if (puzzle.puzzle(i)(j) != 0) solve(puzzle, i, j + 1)
    else {
      // Encontrou um lugar para preencher
      // Testar todos os números válidos entre 1 e a ordem do puzzle
      // Caso encontre um número válido, colocar o número na posição (i, j)
      // e passar para a próxima posição
      breakable {
       for (n <- 1 to puzzle.ordem) {
         if (isValidNum(puzzle, i, j, n)) {
           puzzle.puzzle(i).update(j, n)
           solve(puzzle, i, j + 1)
           puzzle.puzzle(i).update(j, 0)
         }
         // Evita continuar tentando após encontrada a solução
         if (puzzle.isComplete)
           break()
       }
      }
    }
  }
}


object Main {
  def main(args: Array[String]): Unit = {
    // Definição do puzzle e da ordem
    val puzzleName = "n-12"
    val ordem = 9
    // Carrega o puzzle da pasta puzzles/ a partir do nome puzzleName
    val puzzle = LoadPuzzle.load(puzzleName, ordem)
    Solution.solve(puzzle)
  }
}
