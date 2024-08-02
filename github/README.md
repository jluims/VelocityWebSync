# VelocityWebSync

Velocity plugin that sends Velocity server data to your very own webserver. You can use this plugin to create server status pages
and potentially detect when your server is down and monitor players across your entire velocity proxy.

# Building

Clone the repository and run `./gradlew build`. The IDE used for this project is IntelliJ IDEA.

# Usage

Move the plugin .jar file into your Velocity server's `plugins` folder. Please note that your webserver must respond
with a successful status code (200-299) or the plugin will print errors in your logs.

The plugin sends a `POST` request with a JSON payload that looks like this:
```json
{
  "servers": [
    {
      "name": "lobby",
      "address": "127.0.0.1",
      "ping": 1,
      "online": true,
      "players": [
        {
          "username": "playerusername",
          "uuid": "509da9b0-c769-4f1a-99ef-c7b3de099573"
        }
      ]
    }
  ]
}
```


# Configuring

After your server runs for the first time after installing the plugin, a `config.yml` file will be automatically created
inside the `plugins` folder. You will then have a few options you can configure.

# Demonstration

https://github.com/jluims/VelocityWebSync/raw/main/github/demo.webm.mp4