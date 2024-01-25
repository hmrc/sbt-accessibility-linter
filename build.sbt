import sbt._
import sbt.Keys._
import scala.sys.process._

ThisBuild / majorVersion := 1
ThisBuild / isPublicArtefact := true

val scala2_12 = "2.12.13"
val scala2_13 = "2.13.12"

val libName = "sbt-accessibility-linter"
val npmTest = TaskKey[Unit]("npm-test")

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    "com.vladsch.flexmark" % "flexmark-all" % "0.35.10" % "test"
  ),
  scriptedLaunchOpts := {
    val homeDir = sys.props.get("jenkins.home")
      .orElse(sys.props.get("user.home"))
      .getOrElse("")
    scriptedLaunchOpts.value ++
      Seq(
        "-Xmx1024M",
        "-Dplugin.version=" + version.value,
        s"-Dsbt.override.build.repos=${sys.props.getOrElse("sbt.override.build.repos", "false")}",
        // s"-Dsbt.global.base=$sbtHome/.sbt",
        // Global base is overwritten with <tmp scripted>/global and can not be reconfigured
        // We have to explicitly set all the params that rely on base
        s"-Dsbt.boot.directory=${file(homeDir) / ".sbt" / "boot"}",
        s"-Dsbt.repository.config=${file(homeDir) / ".sbt" / "repositories"}"
      )
  },
  scriptedBufferLog := false,
  npmTest := {
    val exitCode = ("npm install" #&& "npm test").!
    if (exitCode != 0) {
      throw new MessageOnlyException("npm install and test failed")
    }
  },
  (Test / test) := (Test / test).dependsOn(npmTest).value,
  Compile / resourceGenerators += Def.task {
    val rootDirectory = baseDirectory.value
    val destination: File = (Compile / resourceManaged).value / "js"

    IO.copyDirectory(rootDirectory / "js" / "src", destination)
    IO.copyFile(rootDirectory / "package.json", destination / "package.json")
    IO.copyFile(rootDirectory / "package-lock.json", destination / "package-lock.json")

    Path.allSubpaths(destination)
      .collect { case (f, _) if !f.isDirectory => f }
      .toSeq
  }.taskValue
)

lazy val play28Plugin = project
  .in(file("play28"))
  .enablePlugins(SbtPlugin)
  .settings(
    scalaVersion := scala2_12,
    crossScalaVersions := Seq(scala2_12, scala2_13),
  )
  .settings(
    commonSettings,
    sbtPlugin := true,
    name := s"$libName-play28",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % "2.8.20"
    )
  )

lazy val sbtPluginDependencies = project
  .in(file("sbt-plugin-deps"))
  .settings(
    scalaVersion := scala2_12,
    sbtPlugin := true,
    libraryDependencies += "org.scala-sbt" % "scripted-sbt_2.12" % "1.9.7",
    dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"
  )

lazy val play29Plugin = project
  .in(file("play29"))
  .dependsOn(sbtPluginDependencies)
  .enablePlugins(SbtPlugin)
  .settings(scalaVersion := scala2_13)
  .settings(
    commonSettings,
    sbtPlugin := true,
    name := s"$libName-play29",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % "2.9.0"
    )
  )

lazy val play30Plugin = project
  .in(file("play30"))
  .dependsOn(sbtPluginDependencies)
  .enablePlugins(SbtPlugin)
  .settings(scalaVersion := scala2_13)
  .settings(
    commonSettings,
    sbtPlugin := true,
    name := s"$libName-play30",
    libraryDependencies ++= Seq(
      "org.playframework" %% "play" % "3.0.0"
    )
  )

lazy val plugin = (project in file("."))
  .aggregate(
    sys.env.get("PLAY_VERSION") match {
      case Some("2.8") => play28Plugin
      case Some("2.9") => play29Plugin
      case _ => play30Plugin
    }
  )