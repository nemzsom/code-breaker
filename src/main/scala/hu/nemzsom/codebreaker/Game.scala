package hu.nemzsom.codebreaker

import scala.util.Random
import Config._
import org.jline.terminal.TerminalBuilder

import scala.annotation.tailrec

class Game(val code: List[Color]) {

  @tailrec
  final def play(round: Int = 1): Unit = {
    print(f"$round%3d - ")
    val guess = new Guess().ask()
    val result = Result(guess, code)
    Guess.printResult(result)
    if (result.solved) {
      println("Szép volt! Tényleg erre gondoltam:")
      print("    ")
      code.foreach(c => print(s"  $c"))
      println()
    } else {
      play(round + 1)
    }
  }

}

object Game {

  def init(withConfiguration: Boolean): Game = {
    if (withConfiguration) {
      colorsConfiguration.configure()
      codeLengthConfiguration.configure()
    }
    println(config)
    println("A használt színek:")
    config.currentPalette.foreach(c => println(s"${c.desc} - $c"))
    println("Tippeléshez nyomd meg a zárójelben lévő betűt")
    println("Javításhoz a visszatörlés gombot, kilépéshez pedig az esc gombot")
    println("---------------------------------------------")
    println()
    new Game(shuffle())
  }

  def shuffle(): List[Color] = {
    val palette = Config.config.currentPalette
    Range(0, Config.config.codeLength).map(_ =>
      palette(Random.between(0, palette.length))
    ).toList
  }
}

class Guess {

  private val terminal = TerminalBuilder.terminal()
  terminal.enterRawMode()
  private val reader = terminal.reader()

  @tailrec
  final def ask(guessed: List[Color] = List()): List[Color] =
    if (guessed.size == config.codeLength) {
      terminal.close()
      guessed.reverse
    } else {
      val key = reader.read()
      val added = Color.resolve(key.toChar)
        .filter(config.currentPalette.contains(_))
        .map { color =>
          print(s"$color  ")
          color :: guessed
        }.getOrElse(key match {
          case KeyCodes.`esc` =>
            println("\nViszlát!")
            sys.exit(127)
            guessed
          case KeyCodes.`backspace` if guessed.nonEmpty =>
            print(Guess.deleteLastColor)
            guessed.tail
          case _ => guessed
        })
      ask(added)
    }
}

object Guess {

  import hu.nemzsom.codebreaker.Cli.esc
  import hu.nemzsom.codebreaker.Color.reset

  val deleteLastColor = s"${Cli.backspace}${Cli.backspace}${Cli.backspace} ${Cli.backspace}"
  val exactMatch = s"$esc[38;5;15m●$reset"
  val colorMatch = s"$esc[38;5;15m⊙$reset"

  def printResult(res: Result): Unit = {
    print("-")
    Range(0, res.exactMatch).foreach(_ => print(s" $exactMatch"))
    Range(0, res.colorMatch).foreach(_ => print(s" $colorMatch"))
    println()
  }
}

case class Result(exactMatch: Int, colorMatch: Int, solved: Boolean)

object Result {

  def apply(guess: List[Color], code: List[Color]): Result = {
    val zipped = guess.zip(code)
    val (exactMatches, nonExact) = zipped.partition(z => z._1 == z._2)
    val (guessColors, codeColors) = nonExact.unzip
    val (_, colorMatch) = guessColors.foldLeft((codeColors, 0)){ case ((codeColors, m), guessColor) =>
      val i = codeColors.indexOf(guessColor)
      if (i >= 0) {
        (codeColors.take(i) ++ codeColors.drop(i + 1), m + 1)
      } else {
        (codeColors, m)
      }
    }
    val solved = exactMatches.size == code.size
    new Result(exactMatches.size, colorMatch, solved)
  }
}

