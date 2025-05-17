![Essence](https://cdn.modrinth.com/data/cached_images/1a9959eb6d98d5e50f561c021de086a8212cc7cc.png)

- 💾 **Download Essence** - https://lewmc.net/plugin/essence
- 🔧 **View the JavaDocs** - https://lewmc.github.io/Essence
- 🌐 **Help Translate Essence** - https://crowdin.com/project/lewmc-essence
- 📊 **Code Analysis** - https://sonarcloud.io/project/overview?id=LewMC_Essence
- ⭐ Enjoying Essence? We'd love to hear your feedback on Spigot. Leave us a review [here](https://www.spigotmc.org/resources/essence.114553/).

[![Crowdin](https://badges.crowdin.net/lewmc-essence/localized.svg)](https://crowdin.com/project/lewmc-essence) [![Maven Build](https://github.com/LewMC/Essence/actions/workflows/maven.yml/badge.svg)](https://github.com/LewMC/Essence/actions/workflows/maven.yml)

# Contributing
We welcome contributions from the community. Please fork the repository, make your changes, and submit a pull request.

Please read [our contributor guide](CONTRIBUTING.md) before submitting any changes, thank you!

Please merge any changes into the `next-update` branch, not the `main` branch.
This helps us to ensure that our snapshot builds are labelled as snapshot so that it is clear to users download them that they are still in development, and that any changes being made will work with future versions of Essence.

## Build Process

Install JDK 21 before continuing. Click [here](https://docs.oracle.com/en/java/javase/21/install/index.html) for documentation.

- You will also need Maven for the `mvn` command, which can be installed [here](https://maven.apache.org/download.cgi).
- Make sure that your version of JDK 21 includes JavaDoc.
  - For example, Eclipse Temurin JDK with Hotspot 21 includes this executable.

```sh
# Clone the repository and move into it.
git clone https://github.com/lewmc/essence && cd essence

# Perform a clean build (optional if you're rebuilding).
mvn clean package -Dmaven.test.skip=true

# Build the package with an explicit version target of 21.
mvn -B package --file pom.xml -Dmaven.compiler.source=21 -Dmaven.compiler.target=21
```

# Licensing

Essence is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for more information.
