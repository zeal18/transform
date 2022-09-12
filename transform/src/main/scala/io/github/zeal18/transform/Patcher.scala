package io.github.zeal18.transform

import io.github.zeal18.transform.internal.macros.dsl.PatcherBlackboxMacros

import scala.language.experimental.macros

/** Type class definition that wraps patching behavior.
  *
  * @tparam T type of object to apply patch to
  * @tparam Patch type of patch object
  */
trait Patcher[T, Patch] {
  def patch(obj: T, patch: Patch): T
}

object Patcher {

  /** Provides implicit [[io.github.zeal18.transform.Patcher]] instance
    * for arbitrary types.
    *
    * @tparam T type of object to apply patch to
    * @tparam Patch type of patch object
    * @return [[io.github.zeal18.transform.Patcher]] type class instance
    */
  implicit def derive[T, Patch]: Patcher[T, Patch] =
    macro PatcherBlackboxMacros.derivePatcherImpl[T, Patch]
}
