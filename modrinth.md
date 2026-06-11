<div align="center">
  <img src="https://i.imgur.com/RsffJBV.png" alt="EligiusHiddenArmor Logo" width="100%" />

  # 🛡️ EligiusHiddenArmor
  **The ultimate, zero-lag visual armor hider plugin for modern servers.**

  [![Paper & Spigot API](https://img.shields.io/badge/Bukkit_|_Spigot_|_Paper_|_Purpur-1.21+-333333?style=flat-square&logo=paper)](https://papermc.io/)
  [![Folia Compatible](https://img.shields.io/badge/Folia-Compatible-ff5e00?style=flat-square&logo=fastapi)](https://papermc.io/software/folia)
  [![Java 21](https://img.shields.io/badge/Java-21_LTS-007396?style=flat-square&logo=openjdk)](https://adoptium.net/)
</div>

---

## 🛑 Why another armor hider plugin?
Most armor hider plugins intercept physical inventory clicks or cancel equipment events. This traditional approach is notorious for causing **item duplication glitches**, breaking server logic, and lagging the main thread.

**EligiusHiddenArmor** takes a revolutionary approach: It operates purely at the network level using `PacketEvents`.

**What does this mean for you?**
* 📉 **Zero Server Lag:** The server continues treating the armor normally (calculating damage, durability, and enchantments). We just intercept the packets sent to the clients, telling them the armor is "invisible".
* 🛡️ **Zero Duplication Glitches:** Since we never touch the physical inventory block logic, duping items is impossible.
* 🚀 **Future-Proof & Universal:** Runs natively on **Bukkit, Spigot, Paper, Purpur, and Folia 1.21+** out of the box.

---

## ✨ Features that stand out

### 🎭 Immersive Roleplay Experience
* **Seamless Protection:** Hide your helmet, chestplate, leggings, or boots visually to show off your skin, while retaining 100% of your Netherite protection.
* **Smart Elytra Exception:** We know players love flying. Elytras are explicitly ignored by the hider engine, preventing awkward visual glitches while gliding.
* **Native Placeholders:** When players open their inventory, their hidden armor is represented by visually translated placeholder items, keeping the UI consistent.

### 👑 Player Control & i18n
* **Granular Toggling:** Players can use `/eha toggle <piece>` to hide specific parts, or just `/eha toggle` for their full set.
* **Admin Control:** Staff can toggle the armor of other users remotely using `/eha toggle <piece> <player>`.
* **Global i18n:** In-game messages are completely customizable via language files with hot-swapping support.
* **bStats Integration:** Anonymous usage metrics help guide the plugin's development. Opt-out anytime in your global bStats config.

### ⚡ Enterprise Scalability
* **Folia Ready:** Built completely asynchronous and Multi-Threaded.
* **Multi-Server Database:** Connect multiple servers using our highly optimized **HikariCP MySQL/MariaDB** adapter, or keep it lightweight with SQLite.
* **Redis Pub/Sub Synchronization (NEW):** Instant, zero-polling memory synchronization across your entire BungeeCord or Velocity proxy network. Toggle your armor in the Hub, and it stays hidden when you join Survival!

---

## 📚 Multi-Language Wiki
We believe in accessible documentation. Our Official Wiki is completely available so your entire administration team can configure the plugin without friction.

<div align="center">

[![Read the Wiki](https://img.shields.io/badge/Read_The_Official_Wiki-blueviolet?style=for-the-badge&logo=gitbook)](https://eligiusmc.github.io/EligiusHiddenArmor/)
[![Report an Issue](https://img.shields.io/badge/Report_An_Issue-black?style=for-the-badge&logo=github)](https://github.com/Eligiusmc/EligiusHiddenArmor/issues)
[![Join Discord](https://img.shields.io/badge/Join_Our_Discord-5865F2?style=for-the-badge&logo=discord)](https://discord.gg/8NAW2M7KGq)

</div>

---
**Requirements:** Bukkit, Spigot, Paper, Purpur or Folia (1.21+) running Java 21 LTS. This plugin requires **PacketEvents** to be installed on your server!
