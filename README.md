# Anti-Pickup Mod for Fabric 1.21.5+

A powerful Fabric mod that allows you to whitelist/blacklist items and blocks to control what you can pick up in Minecraft.

## Features

### Item Categories
- **Armor** - All armor pieces
- **Swords** - All sword types  
- **Tools** - Pickaxes, axes, shovels, hoes, shears
- **Totems** - Totem of Undying
- **End Crystals** - End crystals
- **Anchors** - Respawn Anchors
- **Glowstone** - Glowstone items
- **Obsidian** - Obsidian items

### Block Categories  
- **Glowstone Blocks** - Glowstone blocks
- **Respawn Anchors** - Respawn Anchor blocks
- **Obsidian Blocks** - Obsidian blocks

### Configuration
- **Whitelist Mode** - Only pick up items you explicitly allow
- **Blacklist Mode** - Pick up everything except blocked items
- **Custom Items** - Type in specific item IDs (e.g., `minecraft:diamond`)
- **Custom Blocks** - Type in specific block IDs (e.g., `minecraft:chest`)

## Installation

1. Download the latest JAR: `anti-pickup-1.0.0.jar`
2. Place in your `mods/` folder
3. Requires:
   - Fabric Loader 0.16.5+
   - Fabric API 0.110.0+1.21.5
   - ModMenu 9.2.3+

## Building from Source

### Requirements
- Java 21+
- Gradle (wrapper included)

### Build Steps

```bash
cd 40-ticks
./gradlew build
# JAR: build/libs/anti-pickup-1.0.0.jar
```

## Usage

1. Launch Minecraft with the mod
2. Go to Mods menu → Anti-Pickup → Config
3. Toggle item/block categories on or off
4. Add custom items by typing IDs (e.g., `minecraft:stone`)
5. Choose whitelist/blacklist mode
6. Settings autosave

## Configuration File

Config location: `~/.minecraft/config/anti_pickup.json`

Example (blacklist mode):
```json
{
  "enabled": true,
  "whitelistMode": false,
  "blockArmor": false,
  "blockSword": true,
  "blockTools": false,
  "blockTotems": false,
  "blockCrystals": true,
  "blockAnchors": false,
  "blockGlowstone": true,
  "blockObsidian": false,
  "blockBlocksGlowstone": false,
  "blockBlocksAnchors": false,
  "blockBlocksObsidian": false,
  "customItems": ["minecraft:diamond"],
  "customBlocks": ["minecraft:deepslate_diamond_ore"]
}
```

## Project Structure

```
src/main/java/com/jellomakker/antipickup/
├── AntiPickupMod.java           # Mod initializer & event setup
├── config/
│   └── AntiPickupConfig.java    # JSON config + serialization
├── event/
│   └── ItemPickupHandler.java   # Item pickup logic
├── screen/
│   ├── AntiPickupConfigScreen.java       # Config UI
│   └── AntiPickupModMenuIntegration.java # ModMenu integration
└── util/
    └── ItemCategories.java      # Item categorization helpers
```

## How It Works

1. Server-side event listener checks items every tick
2. Examines items near players
3. Compares against whitelist/blacklist + custom items
4. Removes blocked item entities before pickup
5. Config stored as JSON, auto-saved from Mod Menu

## Dependencies

- **Fabric API** - Core Fabric functionality
- **ModMenu** - In-game config screen
- **Fabric Loader** - Mod loading system

## Version Info

- Mod Version: 1.0.0
- Minecraft: 1.21.5+
- Fabric Loader: 0.16.5+
- Java: 21+
