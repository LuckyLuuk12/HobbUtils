# Hobb-Utils

Hobb-Utils is a utility plugin for Minecraft servers, providing various enhancements and tools to improve server management and gameplay experience.

## Features

- **Custom Location Serialization**: Serialize and deserialize custom locations using `HobbLocation` and `HobbWorld`.
- **Enhanced Logging**: Custom log levels and color formatting for better log readability.
- **Command Testing**: Easily register and test commands with `TestCommand`.

## Installation

1. Download the latest release of Hobb-Utils from the [releases page](https://github.com/LuckyLuuk12/Hobb-Utils/releases).
2. Place the downloaded JAR file into your server's `plugins` directory.
3. Start or restart your Minecraft server.

## Usage

### Custom Location Serialization

Hobb-Utils provides a custom `HobbLocation` class that extends Bukkit's `Location` class and supports serialization using `HobbWorld`.

### Enhanced Logging

The plugin includes custom log levels and a color formatter to improve log readability. Log levels include `WARN`, `ERROR`, and `CRASH`.

### Command Testing

Easily register and test commands using the `TestCommand` class.

## Example

Here is an example of how to use the `HobbLocation` class:

```java
import net.hobbnetwork.custom.HobbLocation;
import org.bukkit.World;

public class Example {
  public void createHobbLocation(World world) {
    HobbLocation loc = new HobbLocation(world, 100, 64, 200);
    // Use the HobbLocation instance as needed
  }
}
```
