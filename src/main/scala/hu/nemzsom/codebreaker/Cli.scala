package hu.nemzsom.codebreaker

object Cli {

  val esc = '\u001B'
  val backspace = '\b'
  val carriageReturn = '\r'
  val moveUp = s"$esc[1A"
  val clearLine = s"$esc[2K"

}

object KeyCodes {
  val esc = 27
  val backspace = 127
}
