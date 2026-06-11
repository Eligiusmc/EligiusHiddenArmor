# Development Guide

This document outlines the architecture and design decisions made for EligiusHiddenArmor.

## Architecture

EligiusHiddenArmor is built using **Hexagonal Architecture** (Ports and Adapters). This ensures that the core domain logic is decoupled from external frameworks like databases, APIs, and networking libraries.

### Domain
The `domain` package contains the core business logic.
- `model`: Contains core entities like `ArmorPiece`.
- `service`: Contains the `ArmorHideService` which orchestrates toggling armor states.
- `port`: Interfaces that define contracts for the adapters (e.g. `DatabasePort`).

### Adapters
The `adapter` package implements the ports defined in the domain layer.
- `database`: Implements `DatabasePort` using `HikariCP` for MySQL and SQLite logic.
- `network`: PacketEvents listeners (`EntityEquipmentPacketListener`, `SetSlotPacketListener`) that intercept packets to visually hide armor without modifying the server's state.
- `platform`: Adaptations for specific server implementations (e.g., Folia vs Spigot).

## Networking (PacketEvents)
To make armor invisible without losing attributes, we intercept:
1. `WrapperPlayServerEntityEquipment`: Mutes armor pieces in the packet for the viewer.
2. `WrapperPlayServerSetSlot` and `WrapperPlayServerWindowItems`: Handles the inventory GUI to show placeholder items instead of the real armor for the client themselves, if desired, to ensure no client desync occurs.

## Database
We use JDBC directly with HikariCP for connection pooling.
All database queries should be executed asynchronously to avoid blocking the main server thread. We maintain an in-memory cache inside `ArmorHideService` to ensure O(1) reads for packet interception.
