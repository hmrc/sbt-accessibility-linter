# Making the sbt-accessibility-plugin Compatible with various Play Framework versions

* Status: accepted
* Deciders: PlatUI
* Date: 29 Jan 2024

Technical Story: [PLATUI-2755](https://jira.tools.tax.service.gov.uk/browse/PLATUI-2755)

## Context and Problem Statement

How can we make the future versions of the sbt-accessibility-plugin compatible with different versions of the Play Framework? We need a solution that balances ease of implementation, maintainability, and compatibility.

## Decision Drivers

* Compatibility with multiple versions of Play Framework
* Maintainability of the plugin
* Ease of implementation
* Future-proofing against new Play Framework versions

## Considered Options

* Option 1: Build a specific version of the `sbt-accessibility-linter` plugin for each Play Framework version.
* Option 2: Use reflection in the `sbt-accessibility-linter` to determine the consuming project's Play version and dynamically include the correct `scalatest-accessibility-linter` library.
* Option 3: Employ macros to determine the Play version at compile time and include the appropriate `scalatest-accessibility-linter` library.
* Option 4: Decouple the `scalatest-accessibility-linter` library from the `sbt-accessibility-linter` plugin.
* Option 5: Only support one version of play at a time - so future updates for old play versions would have to be done as patch versions (changing artefact name when we change versions of play supported)

## Decision Outcome

Chosen option: "Option 2", because it streamlines the development process by removing the need for multiple plugin versions and is relatively easy to implement with existing test coverage. The risks associated with reflection are mitigated by its limited scope in testing environments.

### Positive Consequences

* Eliminates the need to maintain different plugin versions for each Play Framework version.
* Leveraging reflection for version detection simplifies the implementation process. Existing implementation with test coverage (contributed by @wolfendale) provides a strong starting point.
* The plugin's use of reflection is confined to test scopes, which allows teams to easily omit tests if they encounter problems, maintaining flexibility in their development process.

### Negative Consequences

* Using reflection can lead to unforeseen complications in the JVM environment. However, as this feature is only used in testing, the risks are relatively contained.
* The solution depends on the structure of Play, which is outside the project. While changes in Play are unlikely, any modifications could impact the plugin. Automated test coverage is crucial to identify and address these issues promptly.

## Pros and Cons of the Options

### Option 1: Create play-specific plugin versions - using code gen to inject the right scalatest library version

* Good, because it avoids the need for a multi-project build.
* Bad, because it requires separate builds for each new Play version, increasing maintenance.

### Option 2: Reflection to Determine Play Version

* Good, because it eliminates the need for different plugin versions.
* Good, because it's relatively easy to implement. We already have an implementation with some test coverage contributed from @wolfendale
* Bad, because reflection can lead to unexpected edge cases in the JVM environment. But because this is in test scope, and these tests aren't necessary to have they're just a nice to have, it's easy for teams to drop if there are any problems
* Bad, because we're depending on structure of something that's outside the project (in Play). But it's unlikely to change, and we can have automated test coverage to catch problems.

### Option 3: Create play-specific plugin versions - using macros to inject the right scalatest library versions

* Good, because it fits with the existing use of PLAY_VERSION to identify different play versions - rather than having multiple ways (codegen)
* Good, because we wouldn't risk any runtime problems like have a small risk of with reflection
* Bad, because macro usage can be complex and challenging to maintain.

### Option 4: Make users add the right scalatest library for their version of play themselves

* Good, because it removes the dependency on Play version within the plugin.
* Bad, because teams need to manage both the plugin and the specific scalatest library for their Play version.