package io.scalaland.chimney.utils

import _root_.cats.InvariantMonoidal
import _root_.cats.data.NonEmptyChain
import _root_.cats.data.NonEmptyList
import _root_.cats.data.Validated
import _root_.cats.data.ValidatedNec
import _root_.cats.data.ValidatedNel

object ValidatedUtils {

  implicit class OptionOps[T](val opt: Option[T]) extends AnyVal {

    def toValidated[EE[_]: InvariantMonoidal](err: => String): Validated[EE[String], T] =
      opt match {
        case Some(value) => Validated.Valid(value)
        case None        => Validated.Invalid(InvariantMonoidal[EE].point(err))
      }

    def toValidatedNec(err: => String): ValidatedNec[String, T] =
      toValidated[NonEmptyChain](err)(implicitly)

    def toValidatedNel(err: => String): ValidatedNel[String, T] =
      toValidated[NonEmptyList](err)(implicitly)
  }

  implicit class ValidatedOps[+E, +A](val validated: Validated[E, A]) extends AnyVal {

    def getValid: A =
      validated.valueOr(_ => throw new NoSuchElementException)
  }
}
