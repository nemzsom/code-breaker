package hu.nemzsom.codebreaker

import scala.util.Try

case class Config(colors: Int, codeLength: Int) {
  def setColors(c: Int): Config = this.copy(colors = c)
  def setCodeLength(cl: Int): Config = this.copy(codeLength = cl)
  override def toString: String = s"A játék beállítása: $colors fajta szín és $codeLength hosszú kód"

  def currentPalette: List[Color] = Color.palette.take(colors)
}

object Config {
  var config: Config = Config(colors = 2, codeLength = 2)

  val colorsConfiguration = new Configuration[Int](
    s"Hány színt használjak (2-${Color.palette.size})",
    _.colors, input => {
      Try(input.toInt).fold(
        _ => Left(ValidationError("Ez nem szám, próbáld újra!")),
        colors =>
          if (colors < 2) Left(ValidationError("Legalább 2 szín kell"))
          else if (colors > Color.palette.size) Left(ValidationError(s"Maximum ${Color.palette.size} szín lehet"))
          else Right(colors)
      )
    },
    (config, colors) => config.setColors(colors)
  )

  val codeLengthConfiguration = new Configuration[Int](
    s"Milyen hosszú legyen a kód (1-16)",
    _.codeLength, input => {
      Try(input.toInt).fold(
        _ => Left(ValidationError("Ez nem szám, próbáld újra!")),
        length =>
          if (length < 1) Left(ValidationError("Legalább 1 kód elemnek kell lennie!"))
          else if (length > 16) Left(ValidationError(s"Maximum 16 elemből állhat a kód"))
          else Right(length)
      )
    },
    (config, length) => config.setCodeLength(length)
  )
}

case class ValidationError(desc: String)
case class Configuration[T](question: String, get: Config => T, parse: String => Either[ValidationError, T], set: (Config, T) => Config) {

  def configure(): Unit = {
    println(s"$question? [$actValue]")

    var inputNeeded = true
    val printError = (message: String) => {
      println(s"$message! Ha jó a mostani beállítás ($actValue) akkor csak nyomj Entert!")
      inputNeeded = true
    }
    while (inputNeeded) {
      val input = scala.io.StdIn.readLine()
      inputNeeded = false
      if (!input.isBlank) {
        parse(input) match {
          case Left(ValidationError(desc)) => printError(desc)
          case Right(in) => Config.config = set(Config.config, in)
        }
      }
      if (!inputNeeded) println(s"Ok! ($actValue)")
    }
  }

  def actValue: T = get(Config.config)
}
