<div align="center">
  <img src="docs-site/src/public/assets/readme.png" alt="EligiusHiddenArmor Logo" width="100%" />

  # EligiusHiddenArmor

  *The ultimate, highly-optimized armor hider plugin for Bukkit, Spigot, Paper, Purpur, and Folia.*

  [![Paper & Spigot API](https://img.shields.io/badge/Bukkit_|_Spigot_|_Paper_|_Purpur-1.21+-333333?style=flat-square&logo=paper)](https://papermc.io/)
  [![Folia Compatible](https://img.shields.io/badge/Folia-Compatible-ff5e00?style=flat-square&logo=fastapi)](https://papermc.io/software/folia)
  [![Java 21](https://img.shields.io/badge/Java-21_LTS-007396?style=flat-square&logo=openjdk)](https://adoptium.net/)
  [![License](https://img.shields.io/github/license/Eligiusmc/EligiusHiddenArmor?style=flat-square&color=blue)](LICENSE)
  [![Release](https://img.shields.io/github/v/release/Eligiusmc/EligiusHiddenArmor?style=flat-square&color=success)](https://github.com/Eligiusmc/EligiusHiddenArmor/releases)

  [🐛 **Report an Issue**](https://github.com/Eligiusmc/EligiusHiddenArmor/issues)

</div>

---

## 🌟 Overview

**EligiusHiddenArmor** is a highly optimized plugin that allows players to visually hide individual armor pieces (helmet, chestplate, leggings, boots) without losing their damage attributes or enchantments. Designed using **Hexagonal Architecture** and the power of `PacketEvents`.

### ✨ Key Features

- 📉 **Zero Server Lag:** Native armor hiding via packet interception using `PacketEvents`. The server continues calculating damage normally, but visually the armor disappears for the player and the rest of the server without physical block updates.
- 🚀 **Native Folia & Paper Support:** Fully compatible and optimized to run in Multi-Threaded environments like Folia.
- 💾 **Smart Persistence:** SQLite and MySQL database support powered by **HikariCP** for high performance and seamless network support.
- 🔴 **Redis Synchronization (New):** Experimental Pub/Sub support to instantly update visual changes across an entire BungeeCord/Velocity network.
- 🦅 **Smart Elytra Exception:** Elytras are never automatically hidden, ensuring players can always show off their flight naturally without visual conflicts.
- 🎭 **Native Placeholders:** Visual items (placeholders) to keep the player's inventory view consistent.
- 🌐 **Internationalization (i18n):** Fully translatable with multiple built-in languages.
- 📊 **bStats Integration:** Anonymous metrics to help the plugin's growth.

---

## 🚀 Quick Installation

1. Download the latest `EligiusHiddenArmor-x.x.x.jar` from the [Releases page](https://github.com/Eligiusmc/EligiusHiddenArmor/releases).
2. Drop it into your server's `plugins/` folder.
3. Start your server! Supports versions 1.21 and above.
4. Adjust the configuration and permissions to your liking.

---

## 💻 Commands & Permissions

- `/eha toggle`: Hide or show all your armor.
- `/eha toggle <piece>`: Toggle a specific piece (`helmet`, `chestplate`, `leggings`, `boots`).
- `/eha reload`: Reload the configuration and languages.

**Core Permissions:**
- `eligiushiddenarmor.toggle` (default: true)
- `eligiushiddenarmor.toggle.other`
- `eligiushiddenarmor.reload`

---

## 👨‍💻 For Developers

We love Open Source code. If you wish to contribute:

1. The project requires **Java 21 LTS** to compile.
2. Clone the repository and build it with Gradle:

```bash
git clone https://github.com/Eligiusmc/EligiusHiddenArmor.git
cd EligiusHiddenArmor
./gradlew build shadowJar
```

### Conventions
- **Branching:** Work is done from `develop` towards `feature/<name>` branches.
- **Commits:** Use Conventional Commits (`feat:`, `fix:`, etc.).
- **Code Standards:** Maintain the Hexagonal Architecture pattern and ensure Javadocs for core services.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
