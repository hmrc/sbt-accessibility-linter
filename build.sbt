lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  isPublicArtefact := true,
  majorVersion := 0,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "org.pegdown" % "pegdown" % "1.6.0" % "test"
  ),
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    publish / skip := true
  )
  .aggregate(
    sbtAccessibilityLinter, scalatestAccessibilityLinter
  )

lazy val sbtAccessibilityLinter = Project("sbt-accessibility-linter", file("sbt-accessibility-linter"))
  .enablePlugins(SbtPlugin)
  .settings(
    commonSettings,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
  )

lazy val scalatestAccessibilityLinter =
  Project("scalatest-accessibility-linter", file("scalatest-accessibility-linter"))
    .settings(
      commonSettings
    )
