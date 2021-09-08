lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.12",
    resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
  )
