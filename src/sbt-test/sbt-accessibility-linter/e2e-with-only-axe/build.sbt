import scala.sys.process._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2"),
    scalaVersion := "2.13.12",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.8" % Test
  )
