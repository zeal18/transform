import sbtcrossproject.CrossPlugin.autoImport.crossProject

import sbtcrossproject.CrossPlugin.autoImport.CrossType

val versions = new {
  val scala212 = "2.12.16"
  val scala213 = "2.13.8"
}

val settings = Seq(
  scalaVersion       := versions.scala213,
  crossScalaVersions := Seq(versions.scala212, versions.scala213),
  scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-encoding",
    "UTF-8",
    "-unchecked",
    "-deprecation",
    "-explaintypes",
    "-feature",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xlint:adapted-args",
    "-Xlint:delayedinit-select",
    "-Xlint:doc-detached",
    "-Xlint:inaccessible",
    "-Xlint:infer-any",
    "-Xlint:nullary-unit",
    "-Xlint:option-implicit",
    "-Xlint:package-object-classes",
    "-Xlint:poly-implicit-overload",
    "-Xlint:private-shadow",
    "-Xlint:stars-align",
    "-Xlint:type-parameter-shadow",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:imports",
    "-Ywarn-macros:after",
    "-Xfatal-warnings",
    "-language:higherKinds",
    "-Xsource:3",
  ),
  scalacOptions ++= (
    if (scalaVersion.value >= "2.13")
      Seq("-Wunused:patvars")
    else
      Seq(
        "-Xfuture",
        "-Xexperimental",
        "-Yno-adapted-args",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit",
        "-Xlint:by-name-right-associative",
        "-Xlint:unsound-match",
        "-Xlint:nullary-override",
      )
  ),
  Compile / console / scalacOptions --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings"),
)

val dependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %%% "scala-collection-compat" % "2.8.0",
    "org.scala-lang"           % "scala-reflect"           % scalaVersion.value % "provided",
    "com.lihaoyi"            %%% "utest"                   % "0.8.0"            % "test",
  ),
)

lazy val root = project
  .in(file("."))
  .settings(settings*)
  .settings(noPublishSettings*)
  .aggregate(transformJVM, transformJS, transformCatsJVM, transformCatsJS)
  .dependsOn(transformJVM, transformJS, transformCatsJVM, transformCatsJS)

lazy val transform = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(protos % "test->test")
  .settings(
    moduleName  := "transform",
    name        := "transform",
    description := "Scala library for boilerplate free data rewriting",
    testFrameworks += new TestFramework("utest.runner.Framework"),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
  )
  .settings(settings*)
  .settings(publishSettings*)
  .settings(dependencies*)

lazy val transformJVM = transform.jvm
lazy val transformJS  = transform.js

lazy val transformCats = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(transform % "test->test;compile->compile")
  .settings(
    moduleName  := "transform-cats",
    name        := "transform-cats",
    description := "Transform module for validated transformers support",
    testFrameworks += new TestFramework("utest.runner.Framework"),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
  )
  .settings(settings*)
  .settings(publishSettings*)
  .settings(dependencies*)
  .settings(libraryDependencies += "org.typelevel" %%% "cats-core" % "2.8.0" % "provided")

lazy val transformCatsJVM = transformCats.jvm
lazy val transformCatsJS  = transformCats.js

lazy val protos = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    moduleName := "transform-protos",
    name       := "transform-protos",
  )
  .settings(settings*)
  .settings(noPublishSettings*)

lazy val protosJVM = protos.jvm
lazy val protosJS  = protos.js

lazy val publishSettings = Seq(
  organization           := "io.github.zeal18",
  homepage               := Some(url("https://github.com/zeal18/transform")),
  licenses               := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
  developers := List(
    Developer(
      "zeal18",
      "Aleksei Lezhoev",
      "lezhoev@gmail.com",
      url("https://github.com/zeal18"),
    ),
  ),
)

lazy val noPublishSettings =
  Seq(publish / skip := true, publishArtifact := false)

addCommandAlias("check", "all scalafmtSbtCheck; scalafmtCheck; Test / scalafmtCheck")
