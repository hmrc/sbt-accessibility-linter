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

import sbt.Keys.{streams, target}
import sbt.librarymanagement.LibraryManagementSyntax
import sbt.{AutoPlugin, Def, Setting, settingKey, taskKey}

object AccessibilityLinterPlugin extends AutoPlugin with LibraryManagementSyntax {
  override def trigger = allRequirements

  // When an auto plugin provides a stable field such as val or object named autoImport, the contents of the field are
  // wildcard imported in set, eval, and .sbt files. https://www.scala-sbt.org/1.x/docs/Plugins.html
  object autoImport {
    val helloGreeting = settingKey[String]("This is a key for a hello message")
    val hello = taskKey[Unit]("This is a key to task to say hello")

    val helloTwo = taskKey[Unit]("This a key to a second task to say hello")
    val helloWriterTask = Def.task {
      HelloWriter(target.value)
    }
  }

  import autoImport._

  // This adds a value for the settingKey
  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    helloGreeting := "Hello World"
  )

  // This adds implementation for the taskKeys
  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    hello := {
      val s = streams.value
      val g = helloGreeting.value
      s.log.info(g)
    },
    helloTwo := helloWriterTask.value
  )
}
