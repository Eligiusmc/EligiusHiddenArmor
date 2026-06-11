# Solución de Problemas y Errores

## Errores Comunes

### `java.sql.SQLException: Access denied for user`
Tus credenciales de MySQL en `config.yml` son incorrectas. Por favor, verifica tu `username`, `password`, y asegúrate de que el nombre de la base de datos (`name`) existe en tu servidor MySQL.

### `redis.clients.jedis.exceptions.JedisConnectionException`
El plugin no pudo conectarse al servidor Redis. Asegúrate de que el servicio Redis esté ejecutándose, el puerto (generalmente 6379) esté abierto, y la contraseña sea correcta en `config.yml`.

### El comando no hace nada o dice "No permission"
Asegúrate de que el jugador tenga el permiso `eligiushiddenarmor.toggle`. Alternativamente, establece `default-permissions.toggle: true` en `config.yml`.
