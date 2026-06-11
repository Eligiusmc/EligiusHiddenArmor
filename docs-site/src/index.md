---
layout: home
title: "EligiusHiddenArmor - High-efficiency armor hider"
description: "The ultimate, ultra-efficient visual armor hider for Bukkit, Spigot, Paper, Purpur and Folia 1.21 - 26.1.2+."

hero:
  name: "EligiusHiddenArmor"
  text: "High-efficiency armor hider"
  tagline: "Hide armors with zero server lag via packets. Folia support and cross-server Redis synchronization."
  image:
    src: /assets/angry.png
    alt: Eligius Logo
  actions:
    - theme: brand
      text: Installation
      link: /installation
    - theme: alt
      text: Download Release
      link: https://github.com/Eligiusmc/EligiusHiddenArmor/releases/latest

features:
  - title: Zero Lag (Native API)
    details: Hides armors purely on network level using PacketEvents without creating garbage in physical memory.
  - title: Folia & Scalability
    details: Asynchronous architecture ready for distributed networks using HikariCP for concurrent MySQL/SQLite connections.
  - title: Redis Synchronization
    details: Instant real-time synchronization across proxy networks via Redis Pub/Sub.
  - title: Smart Elytra Exception
    details: Elytras are never hidden automatically, ensuring players can naturally show off their flight without visual conflicts.
  - title: Anonymous Metrics
    details: Uses bStats to collect anonymous server statistics (like database and platform usage) to help us improve.
---
