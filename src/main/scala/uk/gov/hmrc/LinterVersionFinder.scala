/*
 * Copyright 2024 HM Revenue & Customs
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

import sbt.ModuleID
import sbt._

object LinterVersionFinder {

  def apply(): ModuleID = {
    try {
      val rm = scala.reflect.runtime.universe
      val m = rm.runtimeMirror(getClass.getClassLoader)
      val playVersionModule = m.staticModule("play.core.PlayVersion")
      val currentField = playVersionModule.info.decl(rm.TermName("current")).asTerm
      val playVersion = m.reflect(m.reflectModule(playVersionModule).instance).reflectField(currentField).get.toString

      // Throughout this build there is scaffolding in place to support multiple versions of Play Framework
      // At the time of writing there is only support for Play 3.0 but this maybe extended if future versions of Play Framework
      // are released

      val playSuffix = playVersion match {
        case v if v.startsWith("3.") => "-play-30"
        case _ => throw new RuntimeException("Invalid Play framework version, only Play 3.0 supported")
      }

      "uk.gov.hmrc" %% s"scalatest-accessibility-linter$playSuffix" % "2.0.0" % Test
    } catch {
      case _: ScalaReflectionException =>
        throw new RuntimeException("No version of Play framework detected")
    }
  }
}
