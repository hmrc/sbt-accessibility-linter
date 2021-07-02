package uk.gov.hmrc

import org.scalatest.{MustMatchers, WordSpec}

import java.io.File

class HelloWriterSpec extends WordSpec with MustMatchers {

  "Given a Hello Writer, calling apply" should {
    "write expected text to the expected file" in {
      val expectedFile = new File("target/say-hello.txt")
      expectedFile.exists() mustBe false

      HelloWriter.apply()
      expectedFile.exists() mustBe true
    }
  }

}
