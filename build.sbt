import scala.sys.process._

val scala2_12 = "2.12.13"
val scala2_13 = "2.13.12"

val libName = "sbt-accessibility-linter"
val npmTest = TaskKey[Unit]("npm-test")

lazy val root = Project(libName, file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := libName,
    scalaVersion := scala2_12,
    crossScalaVersions := Seq(scala2_12, scala2_13),
    isPublicArtefact := true,
    majorVersion := 0,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.9" % "test",
      "com.vladsch.flexmark" %  "flexmark-all" % "0.35.10" % "test"
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
          s"-Dsbt.boot.directory=${file(homeDir)          / ".sbt" / "boot"}",
          s"-Dsbt.repository.config=${file(homeDir)       / ".sbt" / "repositories"}"
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

