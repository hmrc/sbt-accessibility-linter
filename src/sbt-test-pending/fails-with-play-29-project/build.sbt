lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.12",
    resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2"),
    TaskKey[Unit]("check") := {

      val lib = libraryDependencies.value.find(_.name == "scalatest-accessibility-linter-play-29")

      if (lib.isEmpty) {
        sys.error("No scalatest-accessibility-linter-29 library found")
      }
    }
  )
