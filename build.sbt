import scala.sys.process._

val libName = "sbt-accessibility-linter"
val npmTest = TaskKey[Unit]("npm-test")

lazy val root = Project(libName, file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := libName,
    scalaVersion := "2.12.13",
    isPublicArtefact := true,
    majorVersion := 0,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.9" % "test",
      "com.vladsch.flexmark" %  "flexmark-all" % "0.35.10" % "test"
    ),
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

