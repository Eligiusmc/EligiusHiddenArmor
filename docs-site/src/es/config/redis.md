# Sincronización con Redis

Si estás ejecutando una red de servidores (Proxy) y usas **MySQL**, es vital que habilites **Redis** para mantener los datos en caché sincronizados instantáneamente.

## ¿Por qué Redis?
Cuando un jugador desactiva su armadura en el Servidor A, esa información se guarda en MySQL. Sin embargo, el Servidor B no sabrá que la información cambió hasta que el jugador se una a él. Si el jugador se teletransporta, podría haber un desajuste visual temporal.

**Redis resuelve esto:**
1. El Servidor A envía un mensaje a Redis: *"El jugador X ocultó su armadura"*.
2. El Servidor B recibe el mensaje instantáneamente.
3. El Servidor B actualiza su caché asíncronamente desde MySQL.
4. Las armaduras se mantienen consistentes sin ningún lag.

## Configuración

```yaml
redis:
  enabled: true
  host: "127.0.0.1"
  port: 6379
  password: ""
  channel: "eligiushiddenarmor:sync"
```

> [!WARNING] Cuidado con el Canal
> El `channel` debe ser **EXACTAMENTE EL MISMO** en todos tus servidores para que puedan comunicarse entre sí. Si cambias el canal en el Lobby, debes cambiarlo en el Survival.
