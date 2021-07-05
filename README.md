
# sbt-accessibility-linter

This is a placeholder for an `sbt` plugin to identify accessibility issues in HTML markup.

## Quick start

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

Once you have added the library, you can now run the following tasks:

1. `sbt hello` - This will print `Hello World` to your `sbt` cnsole
1. `sbt helloTwo` - This will create a file `say-hello.txt` in your `/target/` folder. The contents of the
   `say-hello.txt` file will be `Hello World`.

## Testing

This plugin contains both unit tests using [Scalatest](https://www.scalatest.org/), and plugin tests using the 
[scripted](https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html) framework.

To run the unit tests, run the command `sbt test`.

To run the plugin tests, run the command `sbt scripted`.

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
