package io.github.zeal18.transform.internal.macros.dsl

import io.github.zeal18.transform.Patcher
import io.github.zeal18.transform.internal.macros.PatcherMacros
import io.github.zeal18.transform.internal.macros.TransformerMacros

import scala.reflect.macros.blackbox

class PatcherBlackboxMacros(val c: blackbox.Context) extends PatcherMacros with TransformerMacros {

  import c.universe.*

  def patchImpl[T: WeakTypeTag, Patch: WeakTypeTag, C: WeakTypeTag]: c.Expr[T] =
    c.Expr[T](expandPatch[T, Patch, C])

  def derivePatcherImpl[T: WeakTypeTag, Patch: WeakTypeTag]: c.Expr[Patcher[T, Patch]] =
    genPatcher[T, Patch](PatcherConfig())
}
