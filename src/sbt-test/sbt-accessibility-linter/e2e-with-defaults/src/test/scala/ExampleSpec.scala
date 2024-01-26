import org.scalatest._
import matchers.should._
import uk.gov.hmrc.scalatestaccessibilitylinter.AccessibilityMatchers

class ExampleSpec extends flatspec.AnyFlatSpec with Matchers with AccessibilityMatchers {

  "Well structured page" should "pass all checks" in {
    s"""
      <!DOCTYPE html>
      <html lang="en">
        <head><title>Example</title></head>
        <body>
          <main>
            <h1>Example</h1>
            <label>Example <input type="text"></label>
          </main>
        </body>
      </html>
    """ should passAccessibilityChecks
  }

  "Page with input with no label" should "fail axe checks" in {
    s"""
      <!DOCTYPE html>
      <html lang="en">
        <head><title>Example</title></head>
        <body>
          <main>
            <h1>Example</h1>
            <input type="text">
          </main>
        </body>
      </html>
    """ should not (passAccessibilityChecks)
  }

}
