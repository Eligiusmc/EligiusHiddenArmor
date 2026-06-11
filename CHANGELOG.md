# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.0.0 (2026-06-11)


### Miscellaneous Chores

* force release please to generate v1.0.0 release PR ([df21d16](https://github.com/Eligiusmc/EligiusHiddenArmor/commit/df21d169d40ecbf13588a472c39c247ca0a118bb))

## [Unreleased] - 2026-06-10

### Added
- New database system (`SQLite` / `MySQL`) using `HikariCP` for better performance on production servers and cross-network synchronization, dropping native `YAML` persistence.
- New "Individual Pieces" feature (Helmet, Chestplate, Leggings, Boots). Players can choose which specific armor piece to hide or show using the command `/eha toggle <piece>`.
- New compatibility flags in listeners to handle independent armor pieces.
- Initial support for standardized technical documentation (README, CHANGELOG, CONTRIBUTING) implementing Hexagonal Architecture.

### Changed
- Restructured data persistence (removed `enabled-players.yml`).
- Massive code cleanup (refactored Handlers, Listeners, and optimized the use of the PacketEvents API).
- License changed from GNU GPLv3 to MIT to integrate with the *Eligiusmc* standards.
- Updated Gradle Toolchain to natively compile with Java 21, resolving inconsistencies with older JREs.
- Serialized messaging using the native Legacy API for full cross-platform compatibility (Paper and pure Spigot).

### Fixed
- Missing cases in the `CommandStatus` enumeration responses (`ERROR`, `SUCCESS`) that caused IDE warnings.
- Multiple references to deprecated methods (`bStats(true)`, `ItemMeta.getLore()`, `ChatColor`).
- Redundant imports and unused assigned variables.
