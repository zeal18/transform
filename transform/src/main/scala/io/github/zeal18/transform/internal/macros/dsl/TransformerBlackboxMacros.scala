package io.github.zeal18.transform.internal.macros.dsl

import io.github.zeal18.transform
import io.github.zeal18.transform.internal.macros.TransformerMacros
import io.github.zeal18.transform.TransformerF
import io.github.zeal18.transform.TransformerFSupport

import scala.annotation.unused
import scala.reflect.macros.blackbox

class TransformerBlackboxMacros(val c: blackbox.Context) extends TransformerMacros {

  import c.universe.*

  def buildTransformerImpl[
    From: WeakTypeTag,
    To: WeakTypeTag,
    C: WeakTypeTag,
    Flags: WeakTypeTag,
    ScopeFlags: WeakTypeTag,
  ](@unused tc: c.Tree): c.Expr[transform.Transformer[From, To]] =
    c.Expr[transform.Transformer[From, To]](
      buildDefinedTransformer[From, To, C, Flags, ScopeFlags](DerivationTarget.TotalTransformer),
    )

  def buildTransformerFImpl[
    F[+_],
    From: WeakTypeTag,
    To: WeakTypeTag,
    C: WeakTypeTag,
    InstanceFlags: WeakTypeTag,
    ScopeFlags: WeakTypeTag,
  ](
    tfs: c.Expr[TransformerFSupport[F]],
    @unused tc: c.Tree,
  ): c.Expr[TransformerF[F, From, To]] = {
    val wrapperType = extractWrapperType(weakTypeOf[C])
    val derivationTarget =
      DerivationTarget.LiftedTransformer(wrapperType, tfs.tree, findTransformerErrorPathSupport(wrapperType))
    c.Expr[TransformerF[F, From, To]](
      buildDefinedTransformer[From, To, C, InstanceFlags, ScopeFlags](derivationTarget),
    )
  }

  def transformImpl[
    From: WeakTypeTag,
    To: WeakTypeTag,
    C: WeakTypeTag,
    InstanceFlags: WeakTypeTag,
    ScopeFlags: WeakTypeTag,
  ](@unused tc: c.Tree): c.Expr[To] =
    c.Expr[To](expandTransform[From, To, C, InstanceFlags, ScopeFlags](DerivationTarget.TotalTransformer))

  def transformFImpl[
    F[+_],
    From: WeakTypeTag,
    To: WeakTypeTag,
    C: WeakTypeTag,
    InstanceFlags: WeakTypeTag,
    ScopeFlags: WeakTypeTag,
  ](
    @unused tc: c.Tree,
    tfs: c.Expr[TransformerFSupport[F]],
  ): c.Expr[F[To]] = {
    val wrapperType = extractWrapperType(weakTypeOf[C])
    val derivationTarget =
      DerivationTarget.LiftedTransformer(wrapperType, tfs.tree, findTransformerErrorPathSupport(wrapperType))
    c.Expr[F[To]](expandTransform[From, To, C, InstanceFlags, ScopeFlags](derivationTarget))
  }

  def deriveTransformerImpl[From: WeakTypeTag, To: WeakTypeTag]: c.Expr[transform.Transformer[From, To]] = {
    val tcTree = findLocalTransformerConfigurationFlags
    val flags  = captureFromTransformerConfigurationTree(tcTree)

    val transformerTree = genTransformer[From, To](
      TransformerConfig(flags = flags).withDefinitionScope(weakTypeOf[From], weakTypeOf[To]),
    )

    c.Expr[transform.Transformer[From, To]] {
      q"""{
        val _ = $tcTree // hack to avoid unused warnings
        $transformerTree
      }"""
    }
  }

  def deriveTransformerFImpl[F[+_], From: WeakTypeTag, To: WeakTypeTag](
    tfs: c.Expr[TransformerFSupport[F]],
  )(implicit fwtt: WeakTypeTag[F[Nothing]]): c.Expr[TransformerF[F, From, To]] = {

    val tcTree      = findLocalTransformerConfigurationFlags
    val flags       = captureFromTransformerConfigurationTree(tcTree)
    val wrapperType = fwtt.tpe.typeConstructor

    val transformerTree = genTransformer[From, To](
      TransformerConfig(
        flags = flags,
        definitionScope = Some((weakTypeOf[From], weakTypeOf[To])),
      ).withDerivationTarget(
        DerivationTarget.LiftedTransformer(
          wrapperType = wrapperType,
          wrapperSupportInstance = tfs.tree,
          wrapperErrorPathSupportInstance = findTransformerErrorPathSupport(wrapperType),
        ),
      ),
    )

    c.Expr[transform.TransformerF[F, From, To]] {
      q"""{
        val _ = $tcTree // hack to avoid unused warnings
        $transformerTree
      }"""
    }
  }
}
