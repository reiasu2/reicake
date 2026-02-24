# ReiParticleSkill NeoForge (1.21.1)

[![License: LGPL-3.0-only](https://img.shields.io/badge/License-LGPL--3.0--only-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

A Minecraft NeoForge 1.21.1 particle effects mod featuring custom Ender Dragon respawn animations and visual effects.

## Modules

| Module | Description | Output Jar |
|--------|-------------|------------|
| `forge-port/` | ReiParticleSkill — main mod | `reiparticleskill-1.0-SNAPSHOT-forge-port.jar` |
| `forge-port-api/` | ReiParticlesAPI — runtime library | `reiparticlesapi-1.0-SNAPSHOT-forge-port.jar` |

Both jars must be placed in the `mods/` folder.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.173
- Java 21

## Building

```bash
# API module
cd forge-port-api
.\gradlew build

# Main mod
cd forge-port
.\gradlew build
```

Jars are output to `build/libs/` in each module.

## Development

```bash
cd forge-port
.\gradlew runClient
```

## License

This project is licensed under **LGPL-3.0-only**. See `LICENSE`, `ATTRIBUTION.md`, and the `LICENSES/` directory for details.

### For Modpack Authors / Players

- **Modpack distribution is allowed** — just keep the original `LICENSE` and `NOTICE` files
  bundled with the mod jar intact. Do not prevent users from replacing or updating this mod.
- If you redistribute the jar, also provide a link to the corresponding source code
  (repository URL + version tag or commit hash).
- You do **not** need to open-source your modpack or other mods just because you include this one.

### For Mod Developers

- You may depend on `ReiParticlesAPI` without your mod becoming LGPL, as long as users can
  replace the API jar independently (which is the default in Forge's `mods/` folder).
- If you **modify and redistribute** this mod's source code, you must make your changes
  available under LGPL-3.0 and clearly mark what you changed.

*The above is a simplified summary, not legal advice. See the full license texts for authoritative terms.*
