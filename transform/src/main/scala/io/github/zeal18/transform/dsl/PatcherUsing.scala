package io.github.zeal18.transform.dsl

import io.github.zeal18.transform.internal.PatcherCfg
import io.github.zeal18.transform.internal.PatcherCfg.IgnoreNoneInPatch
import io.github.zeal18.transform.internal.PatcherCfg.IgnoreRedundantPatcherFields
import io.github.zeal18.transform.internal.macros.dsl.PatcherBlackboxMacros

import scala.language.experimental.macros

/** Provides operations to customize patcher logic for specific
  * object value and patch value.
  *
  * @param obj object to patch
  * @param objPatch patch object
  * @tparam T type of object to apply patch to
  * @tparam P type of patch object
  * @tparam C type-level encoded configuration of patcher
  */
class PatcherUsing[T, P, C <: PatcherCfg](val obj: T, val objPatch: P) {

  /** In case when both object to patch and patch value contain field
    * of type `Option[T]`, this option allows to treat `None` value in
    * patch like the value was not provided.
    *
    * By default, when `None` is delivered in patch, the Transform clears
    * the value of such field on patching.
    *
    * @see [[https://scalalandio.github.io/chimney/patchers/options-handling.html]] for more details
    * @return [[io.github.zeal18.transform.dsl.PatcherUsing]]
    */
  def ignoreNoneInPatch: PatcherUsing[T, P, IgnoreNoneInPatch[C]] =
    this.asInstanceOf[PatcherUsing[T, P, IgnoreNoneInPatch[C]]]

  /** In case that patch object contains a redundant field (i.e. field that
    * is not present in patched object type), this option enables ignoring
    * value of such fields and generate patch successfully.
    *
    * By default, when the Transform detects a redundant field in patch object, it
    * fails the compilation in order to prevent silent oversight of field name
    * typos.
    *
    * @see [[https://scalalandio.github.io/chimney/patchers/redundant-fields.html]] for more details
    * @return [[io.github.zeal18.transform.dsl.PatcherUsing]]
    */
  def ignoreRedundantPatcherFields: PatcherUsing[T, P, IgnoreRedundantPatcherFields[C]] =
    this.asInstanceOf[PatcherUsing[T, P, IgnoreRedundantPatcherFields[C]]]

  /** Applies configured patching in-place
    *
    * @return patched value
    */
  def patch: T = macro PatcherBlackboxMacros.patchImpl[T, P, C]
}
