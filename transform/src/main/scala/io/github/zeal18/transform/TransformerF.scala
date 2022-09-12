package io.github.zeal18.transform

import io.github.zeal18.transform.dsl.TransformerFDefinition
import io.github.zeal18.transform.internal.TransformerCfg
import io.github.zeal18.transform.internal.TransformerFlags
import io.github.zeal18.transform.internal.macros.dsl.TransformerBlackboxMacros

import scala.language.experimental.macros

/** Type class expressing partial transformation between
  * source type `From` and target type `To`, wrapping
  * transformation result in type constructor `F`.
  *
  * Useful for validated transformations, where result
  * type is wrapped in Option, Either, Validated, etc...
  *
  * @see [[io.github.zeal18.transform.TransformerFSupport]]
  *
  * @tparam F    wrapper type constructor
  * @tparam From type of input value
  * @tparam To   type of output value
  */
trait TransformerF[F[+_], From, To] {
  def transform(src: From): F[To]
}

object TransformerF {

  /** Provides [[io.github.zeal18.transform.TransformerF]] derived with the default settings.
    *
    * When transformation can't be derived, it results with compilation error.
    *
    * @tparam F    wrapper type constructor
    * @tparam From type of input value
    * @tparam To   type of output value
    * @return [[io.github.zeal18.transform.TransformerF]] type class definition
    */
  implicit def derive[F[+_], From, To](implicit tfs: TransformerFSupport[F]): TransformerF[F, From, To] =
    macro TransformerBlackboxMacros.deriveTransformerFImpl[F, From, To]

  /** Creates an empty [[io.github.zeal18.transform.dsl.TransformerFDefinition]] that
    * you can customize to derive [[io.github.zeal18.transform.TransformerF]].
    *
    * @see [[io.github.zeal18.transform.dsl.TransformerFDefinition]] for available settings
    *
    * @tparam F    wrapper type constructor
    * @tparam From type of input value
    * @tparam To   type of output value
    * @return [[io.github.zeal18.transform.dsl.TransformerFDefinition]] with defaults
    */
  def define[F[+_], From, To]: TransformerFDefinition[F, From, To, TransformerCfg.WrapperType[
    F,
    TransformerCfg.Empty,
  ], TransformerFlags.Default] =
    new TransformerFDefinition(Map.empty, Map.empty)

}
