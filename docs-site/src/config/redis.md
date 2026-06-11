# Redis Synchronization

If you run a proxy network with MySQL, you should enable **Redis** to keep caches synchronized instantly.

## Why Redis?
When a player toggles their armor on Server A, Server B needs to know immediately to update visual packets. Redis Pub/Sub broadcasts these updates.

## Configuration
```yaml
redis:
  enabled: true
  host: "127.0.0.1"
  port: 6379
  password: ""
  channel: "eligiushiddenarmor:sync"
```

> [!WARNING]
> The `channel` must be identical across all servers in your network.