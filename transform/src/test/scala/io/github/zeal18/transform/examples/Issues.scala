package io.github.zeal18.transform.examples

object foo {
  import io.github.zeal18.transform.dsl.*

  sealed trait A  extends Product with Serializable
  sealed trait AA extends A
  case object A1  extends AA

  object into {
    sealed trait A  extends Product with Serializable
    sealed trait AA extends A
    case object A1  extends AA
  }

  def convert(a: A): into.A = a.transformInto[into.A]
}
