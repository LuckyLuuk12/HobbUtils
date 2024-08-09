# Hobb-Utils

Hobb-Utils is a utility plugin for Minecraft plugin developers, providing various enhancements and tools to improve the development experience. The plugin is designed to be lightweight and easy to use, with a focus on simplifying common tasks and providing additional functionality that is not available in the Bukkit API.

## Features

- **Storage**: Store any kind of value using the `TypedKeyValue<T>` class, either in `H2` or `YML`.
- **Enhanced Logging**: Custom log levels and color formatting for better log readability.
- **Easy Commands**: Simply extend `HobbCommand` and setup commands with ease.
- **Smaller Utils**: Additional utility functions to assist plugin developers in all other areas.

## Installation
**Note**: I still haven't set up a maven repository, so you will have to download the JAR file from the releases page.
1. Download the latest release of Hobb-Utils from the [releases page](https://github.com/LuckyLuuk12/Hobb-Utils/releases).
2. Place the downloaded JAR file into your server's `plugins` directory.
3. Include Hobb-Utils as a dependency in your plugin's `plugin.yml` file:
```xml
<repository>
    <id>hobb-public</id>
    <url>https://repo.hobbnetwork.net/repository/maven-public/</url>
</repository>
```
```xml
<dependencies>
    <dependency>
        <groupId>net.hobbnetwork</groupId>
        <artifactId>Hobb-Utils</artifactId>
        <version>1.0.0</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.220</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```
4. Hook into *Hobb-Utils*:
```java
public final class YourPlugin extends JavaPlugin {
  @Getter private static HookManager hookManager;
    @Override
    public void onEnable() {
      // HookManager(this, "debug", "prefix-hex", "log-to-file");
      hookManager = new HookManager(this, "false", "a2b2c3", "false");
      // The rest of your plugin's initialization code
    }
    // The rest of your plugin's code
}
```
5. Restart your server and try out all the cool utilities of Hobb-Utils!
 
## Discord
Join the [Hobb-Network Discord](https://dc.hobbnetwork.net/) for support, suggestions, and more!
Also, very handy if you want to contribute to the project.

## Contributing
If you would like to contribute to the project, please follow the steps below:
1. Fork the repository.
2. Create a new branch with a descriptive name.
3. Make your changes and commit them.
4. Push your changes to your fork.
5. Create a pull request to the `main` branch of the original repository.
6. Wait for the pull request to be reviewed.
7. Possibly make changes if requested.
8. Once the pull request is approved, it will be merged into the `main` branch.

## Examples
I intend to set up some wiki pages for this, but for now, here are some examples on how to use the plugin.
### Command Setup
```java
import java.util.ArrayList;

public class YourCommand extends HobbCommand {
  YourCommand() {
    this.subLevel = 1;
    this.name = "label";
    this.description = "Some description";
    this.permission = "your.permission.node";
    this.canRegister = true; // Ensure you set this to true to register the command
    this.getSubCommands().add(new YourSubCommand());
  }

  private static class YourSubCommand extends HobbCommand {
    public YourSubCommand() {
      this.subLevel = 2;
      this.name = "sub-label";
      this.description = "Some description";
      this.permission = "your.permission.node.sub";
    }
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
      // Your code here
    }
    @Override
    public List<String> completes(CommandSender sender, Command command, String alias, String[] args) {
      return new ArrayList<>(); // Change the possible tab completions here
    }
  }
}
```
And then register using the `HookManager`:
```java
@Override
public void onEnable() {
  // HookManager(this, "debug", "prefix-hex", "log-to-file");
  this.hookManager = new HookManager(this, "false", "a2b2c3", "false");
  new YourCommand().register(hookManager);
}
```

### Storage

```java
import net.hobbnetwork.storage.HobbStorage;

@Override
public void onEnable() {
  // HookManager(this, "debug", "prefix-hex", "log-to-file");
  this.hookManager = new HookManager(this, "false", "a2b2c3", "false");
  // Make an H2 database, you can also use HobbStorage.StorageType.YML
  HobbStorage storage = new HobbStorage(this, HobbStorage.StorageType.H2);
}
```
Read the JavaDocs for more information on how to use the `HobbStorage` class, such that you can set and get values.
Ensure that your values are serializable, or you will encounter errors.
