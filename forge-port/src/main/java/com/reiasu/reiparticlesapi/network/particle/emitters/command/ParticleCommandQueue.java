// SPDX-License-Identifier: LGPL-3.0-only
// Copyright (C) 2025 Reiasu
package com.reiasu.reiparticlesapi.network.particle.emitters.command;

import com.mojang.logging.LogUtils;
import com.reiasu.reiparticlesapi.network.particle.emitters.ControllableParticleData;
import com.reiasu.reiparticlesapi.particles.ControllableParticle;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public final class ParticleCommandQueue {
    private static final Logger LOGGER = LogUtils.getLogger();

        @FunctionalInterface
    public interface CommandPredicate {
        boolean test(ParticleCommand command, ControllableParticleData data, ControllableParticle particle);
    }

    private static final CommandPredicate ALWAYS_TRUE = (cmd, data, particle) -> true;

    private final Deque<Entry> commands = new ArrayDeque<>();

    public Deque<Entry> getCommands() {
        return commands;
    }

        public void applyVelocity(ControllableParticleData data, ControllableParticle particle) {
        for (Entry entry : commands) {
            if (entry.predicate.test(entry.command, data, particle)) {
                entry.command.execute(data, particle);
            }
        }
    }

        public <T extends ParticleCommand> ParticleCommandQueue updateWith(int index, Consumer<T> applier) {
        int i = 0;
        for (Entry entry : commands) {
            if (i == index) {
                try {
                    @SuppressWarnings("unchecked")
                    T typed = (T) entry.command;
                    applier.accept(typed);
                } catch (ClassCastException e) {
                    LOGGER.debug("Command type mismatch at index {}: {}", index, e.getMessage());
                }
                break;
            }
            i++;
        }
        return this;
    }

        public <T extends ParticleCommand> ParticleCommandQueue updateWithTypes(Class<T> type, Consumer<T> applier) {
        for (Entry entry : commands) {
            if (type.isInstance(entry.command)) {
                applier.accept(type.cast(entry.command));
            }
        }
        return this;
    }

        public ParticleCommandQueue add(ParticleCommand command) {
        commands.add(new Entry(command, ALWAYS_TRUE));
        return this;
    }

        public ParticleCommandQueue add(ParticleCommand command, CommandPredicate predicate) {
        commands.add(new Entry(command, predicate));
        return this;
    }

        public static final class Entry {
        public final ParticleCommand command;
        public final CommandPredicate predicate;

        public Entry(ParticleCommand command, CommandPredicate predicate) {
            this.command = command;
            this.predicate = predicate;
        }
    }
}
