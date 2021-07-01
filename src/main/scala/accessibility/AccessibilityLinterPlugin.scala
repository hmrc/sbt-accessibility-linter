package accessibility

import sbt._
import Keys._


object AccessibilityLinterPlugin extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    val helloGreeting = settingKey[String]("This is a greeting")
    val hello = taskKey[Unit]("This says hello")
  }

  import autoImport._

  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    helloGreeting := "Hello World",
  )

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    hello := {
      val s = streams.value
      val g = helloGreeting.value
      s.log.info(g)
    }
  )
}
