import scala.io.Source
import scala.annotation.tailrec
import scala.util.control.Breaks.{break, breakable}


class Puzzle(
              val puzzle: Array[Array[Int]],
              val comparacoes: Array[Array[String]],
              val correctPuzzle: Array[Array[Int]],
              val ordem: Int
            )


object LoadPuzzle {

  private def readFile(fName: String): Array[String] = {
    val file = Source.fromFile(fName)
    val arr = file.getLines().toArray
    file.close()
    arr
  }

  def load(fName: String, ordem: Int): Puzzle = {
    Puzzle(
      puzzle = List.fill(ordem)(List.fill(ordem)(0).toArray).toArray,
      comparacoes = readFile(s"puzzles/$fName.txt").map( n => n.split(",")),
      correctPuzzle = readFile(s"puzzles/$fName-solved.txt").map(
        n => n.toArray.map(_.asDigit)
      ),
      ordem
    )
  }

}


object TestSolution {

  class TestResult(val i: Int, val j: Int)

  @tailrec
  private def printPuzzle(puzzle: Array[Array[Int]], i: Int = 0, ordem: Int): Unit = {
    if (i < ordem) {
      println(puzzle(i).mkString(", "))
      printPuzzle(puzzle, i + 1, ordem)
    }
  }

  @tailrec
  private def isEqual(puzzle: Puzzle, i: Int = 0, j: Int = 0): TestResult = {
    if (i >= puzzle.ordem) TestResult(-1, -1)
    else if (j >= puzzle.ordem) isEqual(puzzle, i + 1)
    else if (puzzle.puzzle(i)(j) != puzzle.correctPuzzle(i)(j)) TestResult(i, j)
    else isEqual(puzzle, i, j + 1)
  }

  private def showAnwser(res: TestResult): Unit = {
    if (res.i == -1)
      println("Reposta correta!")
    else
      println(s"Resposta incorreta!\nErro encontrado -> (${res.i}, ${res.j})")
  }

  def test(puzzle: Puzzle): Unit = {
    printPuzzle(puzzle.puzzle, ordem = puzzle.ordem)
    showAnwser(isEqual(puzzle))
  }

}


object Solution {

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

  private def checkRegra(puzzle: Puzzle, i: Int, j: Int, num: Int): Boolean = {
    val comp = puzzle.comparacoes(i)(j)
    checkRegraHelper(puzzle, i, j - 1, num, comp(0)) &&
      checkRegraHelper(puzzle, i - 1, j, num, comp(1)) &&
      checkRegraHelper(puzzle, i, j + 1, num, comp(2)) &&
      checkRegraHelper(puzzle, i + 1, j, num, comp(3))
  }


  private def isValidNum(puzzle: Puzzle, i: Int, j: Int, num: Int): Boolean = {
    // Verifica se o número num pode ser alocado na posição (j, i)
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
            break
          }
        }
      }
    }

    box && checkRegra(puzzle, i, j, num)
  }

  def solve(puzzle: Puzzle, i: Int = 0, j: Int = 0): Unit = {
    if (i >= puzzle.ordem) TestSolution.test(puzzle)
    else if (j >= puzzle.ordem) solve(puzzle, i + 1)
    else if (puzzle.puzzle(i)(j) != 0) solve(puzzle, i, j + 1)
    else {
      (1 to 9).filter(
        x => isValidNum(puzzle, i, j, x)
      ).foreach(
        n => {
          puzzle.puzzle(i).update(j, n)
          solve(puzzle, i, j + 1)
          puzzle.puzzle(i).update(j, 0)
        }
      )
    }
  }

}


object Main {

  def main(args: Array[String]): Unit = {
    val fName = "n-11"
    val ordem = 9
    val puzzle = LoadPuzzle.load(fName, ordem)
    Solution.solve(puzzle)
  }

}