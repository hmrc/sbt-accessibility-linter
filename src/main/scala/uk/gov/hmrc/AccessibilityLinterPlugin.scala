package uk.gov.hmrc

import sbt.Keys.streams
import sbt.{AutoPlugin, Setting, settingKey, taskKey}

object AccessibilityLinterPlugin extends AutoPlugin {
  override def trigger = allRequirements

  val helloGreeting = settingKey[String]("This is a key for a hello message")
  val hello = taskKey[Unit]("This is a key to task to say hello")

  // This adds a value for the settingKey
  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    helloGreeting := "Hello World",
  )

  // This adds an implementation for the taskKey
  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    hello := {
      val s = streams.value
      val g = helloGreeting.value
      s.log.info(g)
    }
  )
}
