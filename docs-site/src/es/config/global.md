# Configuración Global (config.yml)

Este es el archivo de configuración principal donde puedes personalizar la base de datos, Redis, permisos predeterminados y prefijos globales.

```yaml
config_version: 1

# Comprobar actualizaciones al iniciar vía Hangar/GitHub
check_updates: true

# Prefijo global para todos los mensajes (Soporta MiniMessage)
prefix: "<dark_gray>[<gradient:#9b59b6:#8e44ad>EHiddenArmor</gradient>]</dark_gray> "

# Archivo de idioma a usar (ej., 'es' para lang/es.yml, 'en' para lang/en.yml)
language: "es"

# Alias del comando (todos apuntan al mismo árbol del comando /eha)
command_aliases:
  - "eligiushiddenarmor"
  - "eha"
  - "hiddenarmor"
  - "ha"

# Comportamiento de permisos predeterminado (true significa otorgado a todos por defecto)
default-permissions:
  toggle: true
  toggle-other: false

# Configuración de Base de Datos (HikariCP)
database:
  type: "sqlite" # o "mysql"
  host: "127.0.0.1"
  port: "3306"
  name: "eligiushiddenarmor"
  username: "root"
  password: "password"

# Sincronización Redis para redes multi-servidor
redis:
  enabled: false
  host: "127.0.0.1"
  port: 6379
  password: ""
  channel: "eligiushiddenarmor:sync"
```
