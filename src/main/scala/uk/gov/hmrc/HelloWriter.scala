package uk.gov.hmrc

import sbt._

import java.io.{File, PrintWriter}

object HelloWriter {

  def apply(destination: File) = {
    val printWriter = new PrintWriter(destination / "say-hello.txt")
    try {
      printWriter.println("Hello World")
    } finally {
      printWriter.close()
    }
  }
}
