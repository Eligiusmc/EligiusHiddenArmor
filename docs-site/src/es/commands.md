# Comandos y Permisos

## Tabla de Comandos y Permisos

| Comando | Descripción | Permiso |
| :--- | :--- | :--- |
| `/eha toggle` | Alterna la visibilidad de toda tu armadura. | `eligiushiddenarmor.toggle` |
| `/eha toggle <pieza>` | Alterna una pieza de armadura específica (helmet, chestplate, leggings, boots). | `eligiushiddenarmor.toggle` |
| `/eha toggle <pieza> <jugador>` | Alterna la armadura de otro jugador. | `eligiushiddenarmor.toggle.other` |
| `/eha hide [jugador]` | Oculta la armadura explícitamente. | `eligiushiddenarmor.toggle` / `.other` |
| `/eha show [jugador]` | Muestra la armadura explícitamente. | `eligiushiddenarmor.toggle` / `.other` |
| `/eha reload` | Recarga los archivos de configuración. | `eligiushiddenarmor.reload` |

## Detalles Adicionales de Permisos

Por defecto, el archivo `config.yml` tiene una sección `default-permissions` que otorga automáticamente el permiso de `toggle` a todos los jugadores sin necesidad de un gestor de permisos como LuckPerms. Puedes cambiar este comportamiento en la configuración.
