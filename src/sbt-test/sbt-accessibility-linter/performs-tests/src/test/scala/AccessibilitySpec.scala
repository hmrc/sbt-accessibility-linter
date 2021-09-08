import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.File

class AccessibilitySpec extends AnyWordSpec with Matchers {
  "a11yTest" should {
    "Test stuff" in {
      new File("target/proof-tests-ran.txt").createNewFile()

      "x" mustBe "x"
    }
  }
}
