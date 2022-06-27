
# sbt-accessibility-linter

This is an `sbt` plugin that can be used to identify accessibility issues in your HTML markup.

It's inspired by [jest-axe](https://github.com/nickcolley/jest-axe) from the Node.js ecosystem, but designed to
work with Scala frontend microservices. It also combines Axe checks with markup validation checks from VNU.

:warning: **Important**: This tool does not guarantee what you build is accessible.

`sbt-accessibility-linter` is a linter, it can identify common issues but cannot guarantee that your service will work 
for all users.

That said, adding these checks early in the development process, for example, as part of a test-driven development
cycle, may help reduce the cost of re-work identified late in the development process following a full accessibility audit.

It's highly recommended that you familiarise yourself with the following guidance:
* [Testing for accessibility, GOV.UK](https://www.gov.uk/service-manual/helping-people-to-use-your-service/testing-for-accessibility)
* [How to test for accessibility, HMRC](https://github.com/hmrc/accessibility/blob/master/docs/how-to-test-for-accessibility.md) 

## Quick start

`sbt-accessibility-linter` is designed to work with Scala frontend microservices build using Play 2.8 and Scalatest 3.2
or above. If you are currently using Scalatest 3.0 or below, you will need to update references to *Spec and Matchers
traits as per the Scalatest 3.1 [release notes](https://www.scalatest.org/release_notes/3.1.0) because this library
will evict previous versions of Scalatest.

If you are still using Play 2.7 or below, complete your upgrade to Play 2.8 before attempting to use this plugin.

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

## Setting up the accessibility test folder

All accessibility tests by default will live under the ``a11y`` folder in the project root directory, you may need to create
this folder if it does not exist already.

```
example-project
  ├─ src
  ├─ test
  ├─ a11y   <--- folder for accessibility tests
```

If you wish to override the base path of the ``a11y`` test folder you can apply the following settings with
a custom path. This will also allow you to change the folder ``a11y`` name if you wish to.

```scala
.settings(
    A11yTest / unmanagedSourceDirectories += (baseDirectory.value / "test" / "a11y")
 )
```

The example settings above would place the ``a11y`` folder under the test directory.

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
sbt a11y:test
```

The above command is a swap-in replacement for `sbt test`, but also installs the npm dependencies needed by the linter.

### Running accessibility checks in Jenkins

It is possible to run your accessibility linter checks in Jenkins, as part of your CI build. You will need to make the following changes to your 
service’s Groovy file in the [build-jobs](https://github.com/hmrc/build-jobs) repository.

1. Navigate to your team or service’s `.groovy` file. For example, this is the [platui.groovy](https://github.com/hmrc/build-jobs/blob/main/jobs/live/platui.groovy) file.
1. Within your service’s `SbtMicroserviceJobBuilder` section, within the `.withTests(...)` section, add in `a11y:test` to the list of tests (see [this example](https://github.com/hmrc/build-jobs/blob/2fe69ac6/jobs/live/platui.groovy#L455) from PlatUI’s `accessiblity-statement-frontend` microservice).
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
[info]     + vnu found 2 potential problem(s): 
[info]     + - Element “h3” not allowed as child of element “strong” in this context. (Suppressing further errors from this subtree.) 
[info]     + - Duplicate ID “duty-deferment-balance”. 
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
