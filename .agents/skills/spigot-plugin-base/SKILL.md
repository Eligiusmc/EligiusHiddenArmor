---
name: spigot-plugin-base
description: "Generates a standardized base for Spigot plugins with Hexagonal Architecture, multi-platform compatibility (1.21-26.1.2), Folia support, and documentation."
---

# Spigot Plugin Base Skill

This skill provides a comprehensive baseline for creating new Spigot plugins based on the established architecture of `EligiusNametag`. It is entirely agnostic to the plugin's final functionality, providing only the foundational structure, patterns, and documentation templates.

## How to use this skill

When the user asks you to create a new plugin (e.g., "Create an armor hider plugin using the spigot-plugin-base skill"), follow these exact steps:

1. **Understand the Request:** Identify the plugin's name, group ID (e.g., `com.myorg`), and core functionality requested by the user.
2. **Setup the Project Root:** Create a new folder for the plugin if one doesn't exist.
3. **Apply the Build Template:** Use `templates/build.gradle.kts.template`. Replace `{group_id}` and `{plugin_name}` with the correct values. This file sets up Java 21, multi-platform support (Bukkit, Spigot, Paper, Purpur, Folia), version compatibility (1.21 to 26.1.2), and essential dependencies (Adventure, HikariCP, Jedis, bStats, MockBukkit).
4. **Create the Architecture (Ports & Adapters):**
   - Create `src/main/java/{group_id}/{plugin_name}/domain/port`
   - Create `src/main/java/{group_id}/{plugin_name}/domain/service`
   - Create `src/main/java/{group_id}/{plugin_name}/adapter/config`
   - Create `src/main/java/{group_id}/{plugin_name}/adapter/database`
   - Create `src/main/java/{group_id}/{plugin_name}/adapter/network`
   - Create `src/main/java/{group_id}/{plugin_name}/adapter/platform`
5. **Apply the Main Class Template:** Use `templates/MainPlugin.java.template`. Replace `{package}` and `{MainClass}`. This template includes Folia detection and service initialization.
6. **Generate Documentation (Wiki):**
   - Create a `docs/` or `wiki/` directory in the root.
   - Use `templates/README_WIKI.md.template` to create the initial `README.md` and `docs/Home.md`. 
   - Write out specific pages based on the user's requested functionality: `Installation.md`, `Configuration.md`, `Commands-and-Permissions.md`.

## Key Architectural Principles to Enforce
- **Domain-Driven Design:** The `domain` package must contain the business logic. It cannot depend on Bukkit/Spigot APIs directly.
- **Ports & Adapters:** Any external interaction (database, Redis, Bukkit API, NMS) must be defined as an interface (`Port`) in the `domain` package and implemented as a class in the `adapter` package.
- **Folia Compatibility:** Do not use `Bukkit.getScheduler()` directly in the domain. Use a `PlatformPort` or Folia-specific scheduler adapters to schedule tasks, detecting `io.papermc.paper.threadedregions.RegionizedServer`.
- **Adventure API:** All text processing, chat messages, and action bars must use MiniMessage. Avoid legacy `§` or `&` color codes.
- **Testing:** Generate a `src/test` directory and implement an E2E test using `MockBukkit`.

## Documentation & Wiki Requirements
Every plugin generated with this skill must have a well-structured Wiki.
The Wiki must include:
- A clear description of the plugin.
- Supported versions (1.21 - 26.1.2) and platforms.
- Configuration explanations (default `config.yml` with comments).
- Commands and Permissions tables.

**When you finish generating the plugin, present the generated structure and a brief summary of the Wiki to the user.**
