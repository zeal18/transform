Quickstart
==========

Using SBT
---------

To include the Transform to your SBT project, add the following line
to your ``build.sbt``:

.. parsed-literal::
  libraryDependencies += "io.github.zeal18" %% "transform" % "|version|"


Library is released for Scala 2.12.x and 2.13.x. If you want to
use it with Scala.js, you need to replace ``%%`` with ``%%%``.

.. warning:: Due to some `compiler bugs <https://issues.scala-lang.org/browse/SI-7046>`_,
  it's recommended to use at least Scala 2.12.1.
