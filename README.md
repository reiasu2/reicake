# ReiParticleSkill

Particle effects mod for NeoForge 1.21.1. Custom dragon respawn animations, skill VFX, etc.

## Structure

- `forge-port/` — single module containing both ReiParticleSkill and ReiParticlesAPI

One jar goes in `mods/`.

## Build

```bash
cd forge-port
.\gradlew build
```

Requires Java 21 + NeoForge 21.1.173.

## License

LGPL-3.0-only. Modpack redistribution is fine, just keep the license files and link back to the source.
If you depend on the API, your mod doesn't need to be LGPL.
