/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.File
import java.nio.file.Files
import scala.io.Source
import sbt._

class DependencyInstallerSpec extends AnyWordSpec with Matchers {

  "Given a DependencyInstaller, calling apply" should {
    "write expected files to the provided location" in {
      val testJarPath = new File("lib/test-jar.jar").getPath

      val outputPath = Files.createTempDirectory("sbt-accessibility-linter").toFile
      outputPath.deleteOnExit()

      DependencyInstaller(testJarPath, "js", outputPath)

      val expectedFile = outputPath / "js" / "src" / "test.js"
      expectedFile must exist

      val expectedSource = Source.fromFile(expectedFile)
      expectedSource.mkString mustBe "/* this is a comment */"
    }
  }
}
