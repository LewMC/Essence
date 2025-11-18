![Essence](https://cdn.modrinth.com/data/cached_images/1a9959eb6d98d5e50f561c021de086a8212cc7cc.png)

- 💾 **Download Essence** - https://lewmc.net/plugin/essence
- 🌐 **Help Translate Essence** - https://crowdin.com/project/lewmc-essence
- ⭐ Enjoying Essence? We'd love to hear your feedback on Spigot. Leave us a review [here](https://www.spigotmc.org/resources/essence.114553/).

[Maven Repository](https://repo.lewmc.net) - [Documentation](https://wiki.lewmc.net/foundry.html) - [Javadocs](https://lewmc.github.io/Essence) - [Code Analysis](https://sonarcloud.io/project/overview?id=LewMC_Essence)

[![Crowdin](https://badges.crowdin.net/lewmc-essence/localized.svg)](https://crowdin.com/project/lewmc-essence)

# Contributing
We welcome contributions from the community. Please fork the repository, make your changes, and submit a pull request.

Please read [our contributor guide](CONTRIBUTING.md) before submitting any changes, thank you!

Please merge any changes into the `next-update` branch, not the `main` branch.
This helps us to ensure that our snapshot builds are labelled as snapshot so that it is clear to users downloading them that they are still in development, and that any changes being made will work with future versions of Essence.

## Build Process

Install JDK 21 before continuing. Click [here](https://docs.oracle.com/en/java/javase/21/install/index.html) for documentation.

- You will also need Maven for the `mvn` command, which can be installed [here](https://maven.apache.org/download.cgi).
- Make sure that your version of JDK 21 includes Javadoc.
  - For example, Eclipse Temurin JDK with Hotspot 21 includes this executable.

```sh
# Clone the repository and move into it.
git clone https://github.com/lewmc/essence && cd essence

# Perform a build
mvn clean package
```

## Handling Player Data
As of version 1.11.0, player data is no longer to be handled via files. Player data is now stored in-memory as a cache
which is automatically read from and written to file when the user joins and leaves the game. This reduces the I/O load
on larger servers and allows for faster operations plugin-wide.

For more information pleases see [the Javadoc](https://lewmc.github.io/Essence/net/lewmc/essence/core/UtilPlayer.html).
Please feel free to message Lew or email dev@lewmc.net for assistance.

Use function getPlayer(UUID, Key) to get a value from the player's data, and setPlayer(UUID, Key, Value) to set a value
in the player's data.

All new features accessing player data must use this system. Some limited older systems may still use files.

# Licensing

Essence is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for more information.
