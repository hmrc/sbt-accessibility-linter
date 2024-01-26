import sbt._
import sbt.Keys._

object Generators {
  // Generates a scala file that contains the play version for use at runtime.
  def SbtAccessibilityLinter(
      playVersion: String,
      dir: File
  ): Seq[File] = {
    val file = dir / "SbtAccessibilityLinter.scala"
    val scalaSource =
      s"""|package uk.gov.hmrc
          |
          |object SbtAccessibilityLinter {
          |  val playVersion = "$playVersion"
          |}
          |""".stripMargin

    if (!file.exists() || IO.read(file) != scalaSource) {
      IO.write(file, scalaSource)
    }

    Seq(file)
  }
}
