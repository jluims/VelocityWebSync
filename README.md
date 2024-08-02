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

https://github-production-user-asset-6210df.s3.amazonaws.com/72709760/354708154-f58f390f-f367-465e-93cf-de35f4bc14d1.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20240802%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240802T194706Z&X-Amz-Expires=300&X-Amz-Signature=5b41eeeb37f17056ea576a41dd9ef672a965a23e25fbb2e988ecdb8bd572de75&X-Amz-SignedHeaders=host&actor_id=72709760&key_id=0&repo_id=837355260