package uk.gov.hmrc

import java.io.{FileWriter, PrintWriter}

object HelloWriter {

  def apply() = {
    val printWriter = new PrintWriter("target/say-hello.txt")
    try {
      printWriter.println("Hello World")
    } finally {
      printWriter.close()
    }
  }
}
