# Database

**EligiusHiddenArmor** saves player visibility preferences in a database. It uses **HikariCP** to ensure zero lag on the main server thread.

## SQLite (Single Server)
The default option. Stores data in a local `database.db` file.
```yaml
database:
  type: "sqlite"
```

## MySQL (Network/Proxy)
Use MySQL if you want to share preferences across multiple servers in a BungeeCord/Velocity network.
```yaml
database:
  type: "mysql"
  host: "127.0.0.1"
  port: "3306"
  name: "eligiushiddenarmor"
  username: "root"
  password: "password"
```