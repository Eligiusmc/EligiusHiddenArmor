# Base de Datos

**EligiusHiddenArmor** guarda las preferencias de visibilidad de los jugadores en una base de datos para que persistan a través de reinicios o cambios de servidor.

El plugin utiliza **HikariCP** para manejar conexiones asíncronas, garantizando **cero lag** en el hilo principal del servidor.

## SQLite (Recomendado para un solo servidor)

Si solo tienes un servidor de supervivencia, **SQLite** es la opción ideal y viene configurada por defecto. Guarda los datos en un archivo local llamado `database.db`.

```yaml
database:
  type: "sqlite"
```

## MySQL (Recomendado para Redes Proxy)

Si tu servidor está conectado a una red **BungeeCord** o **Velocity** y quieres que las preferencias de armadura se compartan entre distintas modalidades (ej. Lobby, Survival, Skyblock), debes usar **MySQL**.

```yaml
database:
  type: "mysql"
  host: "127.0.0.1"
  port: "3306"
  name: "eligiushiddenarmor"
  username: "root"
  password: "password"
```

### Parámetros MySQL
- **host**: La IP de tu servidor MySQL.
- **port**: El puerto de conexión (usualmente 3306).
- **name**: El nombre de la base de datos (debe existir previamente).
- **username / password**: Credenciales de acceso.
