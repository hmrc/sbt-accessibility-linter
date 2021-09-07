import org.scalatest._
import matchers.should._
import uk.gov.hmrc.scalatestaccessibilitylinter.AccessibilityMatchers
import uk.gov.hmrc.scalatestaccessibilitylinter.domain._
import uk.gov.hmrc.scalatestaccessibilitylinter.config.defaultVnuLinter

class ExampleSpec extends flatspec.AnyFlatSpec with Matchers with AccessibilityMatchers {
  override val accessibilityLinters: Seq[AccessibilityLinter.Service] = Seq(defaultVnuLinter)

  "Page with input with duplicate attribute" should "fail vnu checks" in {
    s"""
      <html lang="en">
        <head><title>Example</title></head>
        <body>
          <main>
            <h1>Example</h1>
            <label>Example <input type="text" type="text"></label>
          </main>
        </body>
      </html>
    """ should not (passAccessibilityChecks)
  }
  
  "Page with input with no label" should "pass vnu checks" in {
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
    """ should passAccessibilityChecks
  }
  
  "Well structured page" should "pass vnu checks" in {
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
}
