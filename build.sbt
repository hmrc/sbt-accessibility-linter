lazy val root = Project("sbt-accessibility-linter", file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    version := "0.1.0",
    isPublicArtefact := true,
    scalaVersion := "2.12.10",
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.pegdown" % "pegdown" % "1.6.0" % "test"
    )
  )

