# Global Configuration (config.yml)

This is the main configuration file where you can customize the database, Redis, default permissions, and global prefixes.

```yaml
config_version: 1

# Check for updates on startup via Hangar/GitHub
check_updates: true

# Global plugin prefix for all messages (MiniMessage supported)
prefix: "<dark_gray>[<gradient:#9b59b6:#8e44ad>EHiddenArmor</gradient>]</dark_gray> "

# Language file to use (e.g., 'es' for lang/es.yml, 'en' for lang/en.yml)
language: "en"

# Command aliases (all point to the same /eha command tree)
command_aliases:
  - "eligiushiddenarmor"
  - "eha"
  - "hiddenarmor"
  - "ha"

# Default permissions behavior (true means granted to everyone by default)
default-permissions:
  toggle: true
  toggle-other: false

# Database configuration (HikariCP)
database:
  type: "sqlite" # or "mysql"
  host: "127.0.0.1"
  port: "3306"
  name: "eligiushiddenarmor"
  username: "root"
  password: "password"

# Redis synchronization for multi-server networks
redis:
  enabled: false
  host: "127.0.0.1"
  port: 6379
  password: ""
  channel: "eligiushiddenarmor:sync"
```
