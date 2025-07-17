# AzPvpToggle

AzPvpToggle is a lightweight Minecraft plugin that allows players to toggle their PvP mode on or off. Designed to provide control over player combat and improve server experience, this plugin ensures fair play while maintaining player freedom.

## ✅ Features

- Toggle PvP with `/pvp on` and `/pvp off`
- Tab completion for `/pvp` command
- Notifies attacker when the target has PvP disabled
- Fully customizable messages via `config.yml`
- Lightweight and dependency-free
- Supports Minecraft 1.20+ (tested on 1.21.4)

## 💬 Commands

| Command    | Description            | Permission         |
|------------|------------------------|--------------------|
| `/pvp on`  | Enable your PvP mode   | `azpvptoggle.use`  |
| `/pvp off` | Disable your PvP mode  | `azpvptoggle.use`  |

## 🛡️ Permissions

- `azpvptoggle.use` – Allows player to toggle PvP mode (default: true)
- `azpvptoggle.bypass` – Allows bypassing PvP restriction (e.g., staff)

## ⚙️ Configuration

You can customize all messages in the `config.yml`:

```yaml
messages:
  pvp-enabled: "&aYou have enabled PvP mode."
  pvp-disabled: "&cYou have disabled PvP mode."
  pvp-not-allowed: "&cThat player has PvP disabled!"
  already-enabled: "&ePvP is already enabled."
  already-disabled: "&ePvP is already disabled."
