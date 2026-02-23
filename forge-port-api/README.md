# ReiParticlesAPI Forge Module

Minecraft Forge 1.20.1 particle API library providing runtime support for ReiParticleSkill.

## Features

- Controllable particle system (ControlableParticle)
- Particle group styles with network synchronization
- Custom particle rendering (additive blending, etc.)
- Server-side RenderEntity management
- Tick-driven scheduler with lag-resistant timing
- Event bus with annotation-based listener scanning

## Building

```bash
.\gradlew build
```

Output: `build/libs/reiparticlesapi-1.0-SNAPSHOT-forge-port.jar`

## Usage

Place alongside `forge-port` (ReiParticleSkill) in the `mods/` folder.

## API Quick Start

### 1. Creating a Custom Emitter

```java
public class MyEmitter extends AutoParticleEmitters {
    public static final ResourceLocation CODEC_ID =
            new ResourceLocation("mymod", "my_emitter");

    private double radius;

    public MyEmitter() {}

    public MyEmitter(double radius, int durationTicks) {
        this.radius = radius;
        setMaxTick(durationTicks);
    }

    public static MyEmitter decode(FriendlyByteBuf buf) {
        MyEmitter e = new MyEmitter();
        e.decodeFromBuffer(buf);
        return e;
    }

    @Override
    protected void emitTick() {
        if (level().isClientSide()) {
            // Spawn particles on the client
            double angle = getTick() * 0.1;
            double x = position().x + Math.cos(angle) * radius;
            double z = position().z + Math.sin(angle) * radius;
            level().addAlwaysVisibleParticle(
                    ParticleTypes.END_ROD, x, position().y, z, 0, 0.05, 0);
        }
    }

    @Override
    protected void writePayload(FriendlyByteBuf buf) {
        buf.writeDouble(radius);
    }

    @Override
    protected void readPayload(FriendlyByteBuf buf) {
        this.radius = buf.readDouble();
    }
}
```

### 2. Spawning an Emitter (Server Side)

```java
MyEmitter emitter = new MyEmitter(5.0, 200);
ParticleEmittersManager.spawnEmitters(emitter, serverLevel, x, y, z);
```

### 3. Registering Codecs

Emitters annotated with `@ReiAutoRegister` are registered automatically.
For manual registration:

```java
ParticleEmittersManager.registerCodec(MyEmitter.CODEC_ID, MyEmitter::decode);
```

### 4. Event Listeners

> **Note:** `scanListeners()` uses ClassGraph to discover `@EventListener` classes
> in packages registered via `ReiAPIScanner.registerPackage()`. If no packages are
> registered, it logs an info message and returns. You can also register listeners
> explicitly:

```java
// Option A: package scanning (discovered via ClassGraph when initEventListeners() is called)
ReiParticlesAPI.INSTANCE.appendEventListenerTarget("mymod", "com.example.mymod.listeners");
ReiParticlesAPI.INSTANCE.initEventListeners();

// Option B: direct instance registration
ReiParticlesAPI.INSTANCE.registerEventListener("mymod", new MyListener());
```

### 5. Scheduling Deferred Tasks

```java
// One-shot: run after 20 ticks (1 second)
ReiParticlesAPI.reiScheduler().runTask(20, () -> {
    // Your deferred logic here
});

// Repeating: run every 5 ticks
ReiParticlesAPI.reiScheduler().runTaskTimer(5, () -> {
    // Repeating logic here
});
```
