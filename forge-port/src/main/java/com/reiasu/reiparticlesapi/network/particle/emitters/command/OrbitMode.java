package com.reiasu.reiparticlesapi.network.particle.emitters.command;

public enum OrbitMode {
    /** Full Newtonian force-based orbit (gravitational pull + velocity) */
    PHYSICAL,
    /** Spring-like elasticity toward the orbit center */
    SPRING,
    /** Instantly snap to the computed orbit position */
    SNAP
}
