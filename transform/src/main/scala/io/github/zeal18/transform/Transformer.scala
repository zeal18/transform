package io.github.zeal18.transform

import io.github.zeal18.transform.internal.TransformerCfg
import io.github.zeal18.transform.internal.TransformerFlags
import io.github.zeal18.transform.dsl.TransformerDefinition
import io.github.zeal18.transform.dsl.TransformerFDefinition
import io.github.zeal18.transform.internal.macros.dsl.TransformerBlackboxMacros

import scala.language.experimental.macros

/** Type class expressing total transformation between
  * source type `From` and target type `To`.
  *
  * @tparam From type of input value
  * @tparam To   type of output value
  */
trait Transformer[From, To] {
  def transform(src: From): To
}

object Transformer {

  /** Provides [[io.github.zeal18.transform.Transformer]] derived with the default settings.
    *
    * When transformation can't be derived, it results with compilation error.
    *
    * @tparam From type of input value
    * @tparam To type of output value
    * @return [[io.github.zeal18.transform.Transformer]] type class definition
    */
  implicit def derive[From, To]: Transformer[From, To] =
    macro TransformerBlackboxMacros.deriveTransformerImpl[From, To]

  /** Creates an empty [[io.github.zeal18.transform.dsl.TransformerDefinition]] that
    * you can customize to derive [[io.github.zeal18.transform.Transformer]].
    *
    * @see [[io.github.zeal18.transform.dsl.TransformerDefinition]] for available settings
    *
    * @tparam From type of input value
    * @tparam To type of output value
    * @return [[io.github.zeal18.transform.dsl.TransformerDefinition]] with defaults
    */
  def define[From, To]: TransformerDefinition[From, To, TransformerCfg.Empty, TransformerFlags.Default] =
    new TransformerDefinition(Map.empty, Map.empty)

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
  def defineF[F[+_], From, To]: TransformerFDefinition[F, From, To, TransformerCfg.WrapperType[
    F,
    TransformerCfg.Empty,
  ], TransformerFlags.Default] =
    TransformerF.define[F, From, To]
}
