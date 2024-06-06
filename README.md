
# sbt-accessibility-linter

This is an `sbt` plugin that can be used to identify accessibility issues in your HTML markup.

It's inspired by [jest-axe](https://github.com/nickcolley/jest-axe) from the Node.js ecosystem, but designed to
work with Scala frontend microservices.

> :warning: **Important**
> This tool does not guarantee what you build is accessible.

`sbt-accessibility-linter` is a linter, it can identify common issues but cannot guarantee that your service will work
for all users.

That said, adding these checks early in the development process, for example, as part of a test-driven development
cycle, may help reduce the cost of re-work identified late in the development process following a full accessibility audit.

It's highly recommended that you familiarise yourself with the following guidance:
* [Testing for accessibility, GOV.UK](https://www.gov.uk/service-manual/helping-people-to-use-your-service/testing-for-accessibility)
* [How to test for accessibility, HMRC](https://github.com/hmrc/accessibility/blob/master/docs/how-to-test-for-accessibility.md)

## Quick start

`sbt-accessibility-linter` is designed to work with Scala frontend microservices build using Play 3.0 and Scalatest 3.2
or above. 

If you are still using Play 2.9 or below, complete your upgrade to Play 3.0 before attempting to use this plugin.

You will need [Node.js](https://nodejs.org/en/) installed locally.
sbt-accessibility-linter is designed to work with Node v12 or above.

To use this plugin in your project, add the following line to your `plugins.sbt` in your `project` folder:

```scala
addSbtPlugin("uk.gov.hmrc" % "sbt-accessibility-linter" % "x.x.x")
```

You can find the latest version [here](https://github.com/hmrc/sbt-accessibility-linter/tags).

You will need to have the resolvers for HMRC open artefacts to use this plugin. If you do not already, you will need to
add these lines to the top of your `plugins.sbt`:

```scala
resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)
```

## Plugin Setup

There are two different approaches to writing accessibility tests, you may choose the approach that
suits your team best.

## How to setup automatic accessibility testing

Automatic accessibility testing allows you to write tests via a single spec, it can do this by
automatically scanning through your projects views for page templates.

### High level of how it works
* using reflection to discover page templates
* using the Play app injector to get an instance of each template
* using scalacheck/magnolia to derive arbitrary values for any template parameters
* using these arbitrary values to instantiate each page and test it for accessibility

### Which testing approach is best for your team
#### Manual accessibility testing
* Teams might already have the linter and some coverage via their view unit tests, and may want to continue down that route.
#### Automatic accessibility testing
* Teams adopting the linter for the first time might find it easier to go with the automated route.

### Setup

The accessibility test by default will live under the `a11y` folder in the project root directory, you may need to create
this folder if it does not exist already.

```
example-project
  ├─ src
  ├─ test
  ├─ a11y   <--- folder for accessibility tests
```

If you wish to override the base path of the `a11y` test folder you can apply the following settings with
a custom path. This will also allow you to change the folder `a11y` name if you wish to.

```scala
.settings(
    A11yTest / unmanagedSourceDirectories += (baseDirectory.value / "test" / "a11y")
 )
```

1. Copy the template spec below into your configured a11y folder

    ```scala
    import org.scalacheck.Arbitrary
    import play.api.data.Form
    import play.twirl.api.Html
    import uk.gov.hmrc.scalatestaccessibilitylinter.views.AutomaticAccessibilitySpec
    import views.html._
    import scala.collection.mutable.ListBuffer

    class FrontendAccessibilitySpec
      extends AutomaticAccessibilitySpec {

      // If you wish to override the GuiceApplicationBuilder to provide additional
      // config for your service, you can do that by overriding fakeApplication
      /** example
          override def fakeApplication(): Application =
             new GuiceApplicationBuilder()
                .configure()
                .build()
      */

      // Some view template parameters can't be completely arbitrary,
      // but need to have sane values for pages to render properly.
      // eg. if there is validation or conditional logic in the twirl template.
      // These can be provided by calling `fixed()` to wrap an existing concrete value.
      /** example
          val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
          implicit val arbConfig: Arbitrary[AppConfig] = fixed(appConfig)
      */

      // Another limitation of the framework is that it can generate Arbitrary[T] but not Arbitrary[T[_]],
      // so any nested types (like a Play `Form[]`) must similarly be provided by wrapping
      // a concrete value using `fixed()`.  Usually, you'll have a value you can use somewhere else
      // in your codebase - either in your production code or another test.
      // Note - these values are declared as `implicit` to simplify calls to `render()` below
      // e.g implicit val arbReportProblemPage: Arbitrary[Form[ReportProblemForm]] = fixed(reportProblemForm)

      // This is the package where the page templates are located in your service
      val viewPackageName = "views.html"

      // This is the layout class or classes which are injected into all full pages in your service.
      // This might be `HmrcLayout` or some custom class(es) that your service uses as base page templates.
      val layoutClasses = Seq(classOf[views.html.components.Layout])

      // this partial function wires up the generic render() functions with arbitrary instances of the correct types.
      // Important: there's a known issue with intellij incorrectly displaying warnings here, you should be able to ignore these for now.
      /** example
          override def renderViewByClass: PartialFunction[Any, Html] = {
             case reportProblemPage: ReportProblemPage => render(reportProblemPage)
          }
      */

      runAccessibilityTests()
    }
    ```

2. Run your tests

    The below command will both install npm dependencies and run the accessibility tests
    ```text
    sbt clean A11y/test
    ```
3. Your test output will describe what you need to add to your spec to enable testing the page templates. All tests should be marked as `(pending)`
   which will allow teams to progressively cover and fix all the pages in their service over time.
    ```sbt
    [info] FrontendAccessibilitySpec:
    [info] views.html.ContactHmrcConfirmationPage
    [info] - should be accessible (pending)
    [info]   + Missing wiring - add the following to your renderViewByClass function:
    [info]   + case contactHmrcConfirmationPage: ContactHmrcConfirmationPage => render(contactHmrcConfirmationPage)
    [info] views.html.ContactHmrcPage
    [info] - should be accessible (pending)
    [info]   + Missing wiring - add the following to your renderViewByClass function:
    [info]   + case contactHmrcPage: ContactHmrcPage => render(contactHmrcPage)
    [info] views.html.ErrorPage
    [info] - should be accessible (pending)
    [info]   + Missing wiring - add the following to your renderViewByClass function:
    [info]   + case errorPage: ErrorPage => render(errorPage)
    [info] views.html.FeedbackConfirmationPage
    [info] - should be accessible (pending)
    [info]   + Missing wiring - add the following to your renderViewByClass function:
    [info]   + case feedbackConfirmationPage: FeedbackConfirmationPage => render(feedbackConfirmationPage)
    [info] views.html.FeedbackPage
    [info] - should be accessible (pending)
    [info]   + Missing wiring - add the following to your renderViewByClass function:
    [info]   + case feedbackPage: FeedbackPage => render(feedbackPage)
    ```

    If you wish to run the spec via IntelliJ IDE test runner you will need to add a `program argument`
    to your tests Run/Debug configuration as seen below to get the `case code` output as above.

    ```text
    -C uk.gov.hmrc.scalatestaccessibilitylinter.reporters.AutomaticAccessibilityReporter
    ```

    One advantage of using the IntelliJ IDE test runner, is that it will give you the full code for all pending tests as seen below.

    ```scala
    override def renderViewByClass: PartialFunction[Any, Html] = {
      case accessibilityProblemConfirmationPage: AccessibilityProblemConfirmationPage => render(accessibilityProblemConfirmationPage)
      case contactHmrcConfirmationPage: ContactHmrcConfirmationPage => render(contactHmrcConfirmationPage)
      case contactHmrcPage: ContactHmrcPage => render(contactHmrcPage)
      case errorPage: ErrorPage => render(errorPage)
      case feedbackConfirmationPage: FeedbackConfirmationPage => render(feedbackConfirmationPage)
      case feedbackPage: FeedbackPage => render(feedbackPage)
    }
    ```

    NOTE: This feature is an MVP currently, and we would like to expand functionality as we receive feedback from
    teams using it. If you come across any issues please raise a support query with PlatUI or alternativley share your feedback in the
    `team-plat-ui` Slack channel.

## How to setup manual accessibility testing

### Setting up the accessibility test folder

All accessibility tests by default will live under the `a11y` folder in the project root directory, you may need to create
this folder if it does not exist already.

```
example-project
  ├─ src
  ├─ test
  ├─ a11y   <--- folder for accessibility tests
```

If you wish to override the base path of the `a11y` test folder you can apply the following settings with
a custom path. This will also allow you to change the folder `a11y` name if you wish to.

```scala
.settings(
    A11yTest / unmanagedSourceDirectories += (baseDirectory.value / "test" / "a11y")
 )
```

The example settings above would place the `a11y` folder under the test directory.

```
example-project
  ├─ src
  ├─ test
     ├─ a11y
```

## Running accessibility checks

The simplest way to introduce accessibility testing is to add additional assertions
into your existing view or controller unit tests. For example,

```scala
package views

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.scalatestaccessibilitylinter.AccessibilityMatchers
import uk.gov.hmrc.anyservice.views.html.AnyPage

class AnyPageSpec
    extends AnyWordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with AccessibilityMatchers {

  "the page" must {
    val anyPage = app.injector.instanceOf[AnyPage]
    val content = anyPage()

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }

    /* other view checks */
  }
}
```

To run the tests you can do,

```shell script
sbt A11y/test
```

The above command is a swap-in replacement for `sbt test`, but also installs the npm dependencies needed by the linter.

### Running accessibility checks in Jenkins

It is possible to run your accessibility linter checks in Jenkins, as part of your CI build. You will need to make the following changes to your
service’s Groovy file in the [build-jobs](https://github.com/hmrc/build-jobs) repository.

1. Navigate to your team or service’s `.groovy` file. For example, this is the [platui.groovy](https://github.com/hmrc/build-jobs/blob/main/jobs/live/platui.groovy) file.
1. Within your service’s `SbtMicroserviceJobBuilder` section, within the `.withTests(...)` section, add in `A11y/test` to the list of tests (see [this example](https://github.com/hmrc/build-jobs/blob/2fe69ac6/jobs/live/platui.groovy#L455) from PlatUI’s `accessiblity-statement-frontend` microservice).
1. If you have not already added as part of your build, you will need to add NodeJS to your build pipeline by adding the call `.withNodeJs(NODE_LTS)` to your JobBuilder (see [this example](https://github.com/hmrc/build-jobs/blob/2fe69ac6/jobs/live/platui.groovy#L456)).

Adding the above to your build job will ensure that the accessibility linter tests run as part of your Jenkins build.

## Interpreting test failures

The accessibility linter will only fail a test if it finds something we expect you to be able to
do something about. Known accessibility issues in the underlying `play-frontend-hmrc`, `hmrc-frontend` or
`govuk-frontend` libraries will not fail `passAccessibilityChecks`.

In the case of failures you will see errors like this in your test results:

```text
- should pass accessibility checks *** FAILED ***
[info]   Accessibility violations were present. (FeedbackPageSpec.scala:86)
[info]   + axe found 1 potential problem(s):
[info]   + {"level":"ERROR","description":"Fix any of the following:\n  aria-label attribute
does not exist or is empty\n  aria-labelledby attribute does not exist, references elements
that do not exist or references elements that are empty\n  Form element does not have an
implicit (wrapped) <label>\n  Form element does not have an explicit <label>\n  Element has no
title attribute\n  Element has no placeholder attribute\n  Element's default semantics were not
overridden with role=\"none\" or role=\"presentation\"","snippet":"<input type=\"text\">",
"helpUrl":"https://dequeuniversity.com/rules/axe/4.1/label?application=axeAPI","furtherInfo":""}
```

The above error was caused by deliberately adding an unlabeled lone `<input type="text" />` into a page.

## Setting the output format
Initial feedback from some users was that the output from the tool was sometimes difficult to read,
due to sheer volume of output in JSON-lines format.  We have added the ability for teams to choose from
two different output formats - "verbose" (the default) and "concise", which shows just
the error description and, for axe accessibility errors, a CSS selector giving its location, plus a URL to
the relevant guidance on the Deque University website. For example,
```text
[info]   - passes accessibility checks *** FAILED ***
[info]     Accessibility violations were present. (ExamplePagesFromRealServicesSpec.scala:13)
[info]     + axe found 4 potential problem(s):
[info]     + - Ensures every id attribute value is unique
[info]     +   (.cash-account > .available-account-balance.custom-card__balance.govuk-body)
[info]     +   https://dequeuniversity.com/rules/axe/4.1/duplicate-id?application=axeAPI
[info]     + - Ensures every id attribute value is unique
[info]     +   (.duty-deferment-account.custom-card.govuk-\!-margin-bottom-7:nth-child(3) > .card-main > .available-account-balance.custom-card__balance.govuk-body)
[info]     +   https://dequeuniversity.com/rules/axe/4.1/duplicate-id?application=axeAPI
[info]     + - Ensures links have discernible text
[info]     +   (.custom-card__footer > .govuk-link:nth-child(3))
[info]     +   https://dequeuniversity.com/rules/axe/4.1/link-name?application=axeAPI
[info]     + - Ensures all page content is contained by landmarks
[info]     +   (input)
[info]     +   https://dequeuniversity.com/rules/axe/4.1/region?application=axeAPI
```

You can override the default output format by adding the following configuration to your `application.conf`:
```hocon
sbt-accessibility-linter {
  output-format = "concise"
}
```
You can also override it on a per-test level (for example, when debugging a new failure),
by passing the desired output format as a parameter to the matcher:
```scala
  "the page" must {
    val anyPage = app.injector.instanceOf[AnyPage]
    val content = anyPage()

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks(OutputFormat.Verbose)
    }
  }
```

## What to do if you encounter an unknown issue in play-frontend-hmrc

If you encounter an unknown issue in `play-frontend-hmrc`, please let us know
as soon as possible at #team-plat-ui and we will work to add it to the list of known issues and release a
new version of `sbt-accessibility-linter` as soon as possible.

## Plugin tests (for maintainers only)

This plugin contains both unit tests using [Scalatest](https://www.scalatest.org/), and plugin tests using the
[scripted](https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html) framework.

To run the unit tests, run the command `sbt test`.

To run the plugin tests, run the command `sbt scripted`.

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
