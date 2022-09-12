package io.github.zeal18.transform

import io.github.zeal18.transform.internal.PatcherCfg
import io.github.zeal18.transform.internal.TransformerCfg
import io.github.zeal18.transform.internal.TransformerFlags

/** Main object to import in order to use the Transform's features
  */
package object dsl {

  /** Provides transformer operations on values of any type.
    *
    * @param source wrapped source value
    * @tparam From type of source value
    */
  implicit class TransformerOps[From](private val source: From) extends AnyVal {

    /** Allows to customize transformer generation to your target type.
      *
      * @tparam To target type
      * @return [[io.github.zeal18.transform.dsl.TransformerInto]]
      */
    final def into[To]: TransformerInto[From, To, TransformerCfg.Empty, TransformerFlags.Default] =
      new TransformerInto(source, new TransformerDefinition(Map.empty, Map.empty))

    /** Performs in-place transformation of captured source value to target type.
      *
      * If you want to customize transformer behavior, consider using
      * [[io.github.zeal18.transform.dsl.TransformerOps#into]] method.
      *
      * @see [[io.github.zeal18.transform.Transformer#derive]] for default implicit instance
      * @param transformer implicit instance of [[io.github.zeal18.transform.Transformer]] type class
      * @tparam To target type
      * @return transformed value of target type `To`
      */
    final def transformInto[To](implicit transformer: Transformer[From, To]): To =
      transformer.transform(source)
  }

  implicit class TransformerFOps[From](private val source: From) extends AnyVal {

    /** Allows to customize wrapped transformer generation to your target type.
      *
      * @tparam F  wrapper type constructor
      * @tparam To target type
      * @return [[io.github.zeal18.transform.dsl.TransformerFInto]]
      */
    final def intoF[F[+_], To]
      : TransformerFInto[F, From, To, TransformerCfg.WrapperType[F, TransformerCfg.Empty], TransformerFlags.Default] =
      new TransformerFInto(source, new TransformerFDefinition(Map.empty, Map.empty))

    /** Performs in-place wrapped transformation of captured source value to target type.
      *
      * If you want to customize transformer behavior, consider using
      * [[io.github.zeal18.transform.dsl.TransformerFOps#intoF]] method.
      *
      * @see [[io.github.zeal18.transform.TransformerF#derive]] for default implicit instance
      * @param transformer implicit instance of [[io.github.zeal18.transform.TransformerF]] type class
      * @tparam To target type
      * @return transformed wrapped target value of type `F[To]`
      */
    final def transformIntoF[F[+_], To](implicit transformer: TransformerF[F, From, To]): F[To] =
      transformer.transform(source)
  }

  /** Provides patcher operations on values of any type
    *
    * @param obj wrapped object to patch
    * @tparam T type of object to patch
    */
  implicit class PatcherOps[T](private val obj: T) extends AnyVal {

    /** Allows to customize patcher generation
      *
      * @param patch patch object value
      * @tparam P type of patch object
      * @return [[io.github.zeal18.transform.dsl.PatcherUsing]]
      */
    final def using[P](patch: P): PatcherUsing[T, P, PatcherCfg.Empty] =
      new PatcherUsing[T, P, PatcherCfg.Empty](obj, patch)

    /** Performs in-place patching of wrapped object with provided value.
      *
      * If you want to customize patching behavior, consider using
      * [[io.github.zeal18.transform.dsl.PatcherOps#using using]] method.
      *
      * @see [[io.github.zeal18.transform.Patcher#derive]] for default implicit instance
      * @param patch patch object value
      * @param patcher implicit instance of [[io.github.zeal18.transform.Patcher]] type class
      * @tparam P type of patch object
      * @return patched value
      */
    final def patchUsing[P](patch: P)(implicit patcher: Patcher[T, P]): T =
      patcher.patch(obj, patch)

    /** Performs in-place patching of wrapped object with provided value.
      *
      * If you want to customize patching behavior, consider using
      * [[io.github.zeal18.transform.dsl.PatcherOps#using using]] method.
      *
      * @deprecated use [[io.github.zeal18.transform.dsl.PatcherOps#patchUsing patchUsing]] instead
      * @see [[io.github.zeal18.transform.Patcher#derive]] for default implicit instance
      * @param patch patch object value
      * @param patcher implicit instance of [[io.github.zeal18.transform.Patcher]] type class
      * @tparam P type of patch object
      * @return patched value
      */
    @deprecated("please use .patchUsing", "0.4.0")
    final def patchWith[P](patch: P)(implicit patcher: Patcher[T, P]): T =
      // $COVERAGE-OFF$
      obj.patchUsing(patch)
    // $COVERAGE-ON$
  }
}
