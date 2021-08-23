/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc

import sbt.Keys.{libraryDependencies, sourceGenerators, sourceManaged, target}
import sbt.librarymanagement.LibraryManagementSyntax
import sbt._

import java.io.File
import scala.sys.process.Process

object AccessibilityLinterPlugin extends AutoPlugin with LibraryManagementSyntax {
  override def trigger = allRequirements

  // When an auto plugin provides a stable field such as val or object named autoImport, the contents of the field are
  // wildcard imported in set, eval, and .sbt files. https://www.scala-sbt.org/1.x/docs/Plugins.html
  object autoImport {
    val a11yRoot = taskKey[File]("Builds the destination path for the a11y linter")
    val a11yRootTask = Def.task {
      target.value / "sbtaccessibilitylinter"
    }
    val a11yExtract = taskKey[Unit]("Extract the a11y linter assets")
    val a11yExtractTask = Def.task {
      val jarPath: String = getClass
        .getProtectionDomain
        .getCodeSource
        .getLocation
        .toURI
        .getPath
      DependencyInstaller(jarPath, "js", a11yRoot.value)
    }
    val a11yInstall = taskKey[Unit]("Performs an npm install on the a11y linter assets")
    val a11yInstallTask = Def.task {
      a11yExtract.value
      npmProcess("npm install failed for a11y linter")(a11yRoot.value / "js", "install")
    }
  }

  import autoImport._

  // This adds a value for the settingKey
  override lazy val globalSettings: Seq[Setting[_]] = Seq.empty

  // This adds implementation for the taskKeys and additional library dependencies
  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    a11yRoot := a11yRootTask.value,
    a11yExtract := a11yExtractTask.value,
    a11yInstall := a11yInstallTask.value,
    libraryDependencies ++= Seq(
      "uk.gov.hmrc" %% "scalatest-accessibility-linter" % "0.4.0-SNAPSHOT" % Test
    ),
    Test / sourceGenerators += Def.task {
      val file = (Test / sourceManaged).value / "sbtaccessibilitylinter.scala"
      IO.write(file, """package uk.gov.hmrc.sbtaccessibilitylinter
                       |
                       |import java.io.File
                       |import org.scalatest.{Informing, Alerting}
                       |import scala.sys.process.Process
                       |import uk.gov.hmrc.scalatestaccessibilitylinter.{AccessibilityLinterMatchers, AccessibilityLinter, KnownIssues}
                       |import uk.gov.hmrc.scalatestaccessibilitylinter.linters.{AxeAccessibilityLinter, SystemSubprocess}
                       |
                       |trait AccessibilityLintersAndMatchers extends AccessibilityLinterMatchers { this: Informing with Alerting =>
                       |  private val knownIssues: KnownIssues = KnownIssues.loadFromApplicationConfig
                       |
                       |  protected val accessibilityLinters: Seq[AccessibilityLinter] = Seq(
                       |    AxeAccessibilityLinter(
                       |      SystemSubprocess(Process("node axe", new File("target/sbtaccessibilitylinter/js"))),
                       |      knownIssues
                       |    )
                       |  )
                       |}""".stripMargin)
      Seq(file)
    }.taskValue
  )

  private def npmProcess(failureMessage: String)(base: File, args: String*): Int = {
    val processBuilder = Process("npm" :: args.toList, base)
    val exitValue      = processBuilder.run().exitValue()
    if (exitValue != 0) {
      throw new Exception(failureMessage)
    } else exitValue
  }
}
