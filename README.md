# Transform

The library is a fork of the [Chimney](https://github.com/scalalandio/chimney) aiming development continuation and Scala 3 support.

![CI](https://github.com/zeal18/transform/actions/workflows/ci.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.zeal18/transform_2.13.svg)](https://search.maven.org/search?q=g:io.github.zeal18%20AND%20a:transform*)
[![Javadocs](https://www.javadoc.io/badge/io.github.zeal18/transform_2.13.svg?color=red&label=scaladoc)](https://www.javadoc.io/doc/io.github.zeal18/transform_2.13/latest/io/github/zeal18/transform/index.html)
[![License](http://img.shields.io/:license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.0.0.svg)](https://www.scala-js.org)

Battle tested Scala library for boilerplate-free data transformations.

In the daily life of a strongly-typed language's programmer sometimes it
happens we need to transform an object of one type to another object which
contains a number of the same or similar fields in their definitions.

```scala
case class MakeCoffee(id: Int, kind: String, addict: String)
case class CoffeeMade(id: Int, kind: String, forAddict: String, at: ZonedDateTime)
```
Usual approach is to just rewrite fields one by one
```scala
val command = MakeCoffee(id = Random.nextInt,
                         kind = "Espresso",
                         addict = "Piotr")
val event = CoffeeMade(id = command.id,
                       kind = command.kind,
                       forAddict = command.addict,
                       at = ZonedDateTime.now)
```

While the example stays lean, in real-life code we usually end up with tons
of such boilerplate, especially when:
- we maintain typed schema and want to migrate between multiple schema versions
- we apply practices like DDD (Domain-Driven-Design) where suggested
  approach is to separate model schemas of different bounded contexts
- we use code-generation tools like Protocol Buffers that generate primitive
  types like `Int` or `String`, while you'd prefer to
  use value objects in you domain-level code to improve type-safety
  and readability  


Transform provides a compact DSL with which you can define transformation
rules and transform your objects with as little boilerplate as possible.

```scala
import io.github.zeal18.transform.dsl._

val event = command.into[CoffeeMade]
  .withFieldComputed(_.at, _ => ZonedDateTime.now)
  .withFieldRenamed(_.addict, _.forAddict)
  .transform
```

Underneath it uses Scala macros to give you:
- type-safety at compile-time
- fast generated code, almost equivalent to hand-written version
- excellent error messages
- minimal overhead on compilation time

## Getting started

To include Transform to your SBT project, add the following line to your `build.sbt`:

```scala
libraryDependencies += "io.github.zeal18" %% "transform" % "<version>"
```

Library is released for Scala 2.12.x and 2.13.x, Scala 3 support is under development.

If you want to use it with Scala.js, you need to replace `%%` with `%%%`.
Due to some [compiler bugs](https://issues.scala-lang.org/browse/SI-7046),
it's recommended to use at least Scala 2.12.1.