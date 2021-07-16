import scala.sys.process._

lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  isPublicArtefact := true,
  majorVersion := 0,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "org.pegdown" % "pegdown" % "1.6.0" % "test"
  ),
)

lazy val root: Project = (project in file("."))
  .settings(
    commonSettings,
    publish / skip := true
  )
  .aggregate(
    sbtAccessibilityLinter, scalatestAccessibilityLinter
  )

val npmTest = TaskKey[Unit]("npm-test")

lazy val sbtAccessibilityLinter: Project = Project("sbt-accessibility-linter", file("sbt-accessibility-linter"))
  .enablePlugins(SbtPlugin)
  .settings(
    commonSettings,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    npmTest := {
      val exitCode = ("npm install" #&& "npm test").!
      if (exitCode != 0) {
        throw new MessageOnlyException("npm install and test failed")
      }
    },
    (test in Test) := (test in Test).dependsOn(npmTest).value,
    Compile / resourceGenerators += Def.task {
      val rootDirectory = baseDirectory.value / ".."
      val destination: File = (Compile / resourceManaged).value / "js"

      IO.copyDirectory(rootDirectory / "js" / "src", destination)
      IO.copyFile(rootDirectory / "package.json", destination / "package.json")
      IO.copyFile(rootDirectory / "package-lock.json", destination / "package-lock.json")

      Path.allSubpaths(destination)
        .collect { case (f, _) if !f.isDirectory => f }
        .toSeq
    }.taskValue
  )

lazy val scalatestAccessibilityLinter: Project =
  Project("scalatest-accessibility-linter", file("scalatest-accessibility-linter"))
    .settings(
      commonSettings
    )
