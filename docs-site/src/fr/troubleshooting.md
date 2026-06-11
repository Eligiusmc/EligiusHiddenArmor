# Troubleshooting & Errors

## Common Errors

### `java.sql.SQLException: Access denied for user`
Your MySQL credentials in `config.yml` are incorrect. Please verify your `username`, `password`, and ensure the database `name` exists in your MySQL server.

### `redis.clients.jedis.exceptions.JedisConnectionException`
The plugin failed to connect to the Redis server. Make sure the Redis service is running, the port (usually 6379) is open, and the password is correct in `config.yml`.

### Command does nothing or says "No permission"
Ensure that the player has the `eligiushiddenarmor.toggle` permission. Alternatively, set `default-permissions.toggle: true` in `config.yml`.
