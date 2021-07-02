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
    helloGreeting := "Hello World",
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
