package hu.nemzsom.codebreaker

import org.jline.terminal.{Terminal, TerminalBuilder}
import org.jline.utils.NonBlockingReader

import scala.annotation.tailrec

object Main {

  def main(args: Array[String]): Unit = {
    println(
      s"""Hulej!
        |
        |Én egy kódfeltörő játék vagyok. Elrejtek egy színkombinációt és neked ki kell találni.
        |Meg kell tippelned, hogy mit rejtettem el, én pedig megmondom hogy hágy színt találtál el és hány van jó helyen.
        |A színek száma változtatható és a kód hossza is.
        |
        |Ha jó szín jó helyen van, azt így jelölöm:        ${Guess.exactMatch}
        |Ha csak a szín jó, de nem jó helyen van, azt így: ${Guess.colorMatch}
        |
        |Kezdjük is el!
        |""".stripMargin)

    loop()
  }

  @tailrec
  def loop(withConfigure: Boolean = true): Unit = {
    Game.init(withConfigure).play()
    println("Játszunk még? (I)gen, (N)em, Új (B)eállítás [I]")

    @tailrec
    def askContinue(): String = {
      val line = scala.io.StdIn.readLine()
      line.strip().toUpperCase match {
        case "" => "I"
        case line if Set("I", "N", "B").contains(line) => line
        case _ =>
          println("I, N vagy B?")
          askContinue()
      }
    }
    askContinue() match {
      case "I" => loop(false)
      case "N" => println("Köszönöm a játékot, Viszlát!")
      case "B" => loop()
    }
  }



}
