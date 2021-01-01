package hu.nemzsom.codebreaker

import hu.nemzsom.codebreaker.Cli.esc

object Color {
  val reset = s"$esc[0m"
  val palette = List(Red, Yellow, Blue, Green, Pink, Purple)

  def resolve(char: Char): Option[Color] = char.toUpper match {
    case Red.`letter` => Some(Red)
    case Yellow.`letter` => Some(Yellow)
    case Blue.`letter` => Some(Blue)
    case Green.`letter` => Some(Green)
    case Pink.`letter` => Some(Pink)
    case Purple.`letter` => Some(Purple)
    case _ => None
  }
}

sealed case class Color(letter: Char, code: Int, desc: String) {
  import hu.nemzsom.codebreaker.Color._
  override def toString = s"$esc[38;5;${code}m⬤$reset"
}
object Red extends Color('P',196, "(P)iros")
object Yellow extends Color('S',226, "(S)árga")
object Blue extends Color('K',27, "(K)ék")
object Green extends Color('Z',46, "(Z)öld")
object Pink extends Color('R',207, "(R)ózsaszín")
object Purple extends Color('L',93, "(L)ila")



