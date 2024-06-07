![Essence](https://cdn.modrinth.com/data/cached_images/1a9959eb6d98d5e50f561c021de086a8212cc7cc.png)

- 💾 **Download Essence** - https://lewmc.net/plugin/essence
- 🔧 **View the JavaDocs** - https://lewmc.github.io/Essence
- ⭐ Enjoying Essence? We'd love to hear your feedback on Spigot. Leave us a review [here](https://www.spigotmc.org/resources/essence.114553/). 

## Creating a local copy.

### Clone the repository.

Use the Git CLI to clone the repository from GitHub.

```sh
git clone https://github.com/lewmilburn/Essence
```

### Install Maven.

You will need Maven to build the project to `./target`, you can install it at https://maven.apache.org/download.cgi.

### Set Java 17 as your `JAVA_HOME` variable.

You can validate this by running the command below and ensuring it returns Java 17.

```sh
java -version
```

### Build the project.

Navigate to the cloned repository and run:

```sh
mvn clean install
```

This will compile the project and package it into a JAR file in the `./target` directory.

## Contributing

We welcome contributions from the community. Please fork the repository, make your changes, and submit a pull request.

## Licensing

Essence is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for more information.
