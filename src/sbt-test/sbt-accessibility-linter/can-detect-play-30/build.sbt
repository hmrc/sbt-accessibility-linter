lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.12",
    resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2"),
    TaskKey[Unit]("check") := {

      val lib = libraryDependencies.value.find(_.name startsWith "scalatest-accessibility-linter-play-30")

      if (lib.isEmpty) {
        sys.error("No scalatest-accessibility-linter-play-30 library found")
      }
    }
  )
