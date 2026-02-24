package com.reiasu.reiparticlesapi.commands;

import com.reiasu.reiparticlesapi.display.DisplayEntityManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public final class APICommand {
    public static final APICommand INSTANCE = new APICommand();

    private APICommand() {
    }

        public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("cleanapi")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("display")
                                .executes(ctx -> {
                                    DisplayEntityManager.INSTANCE.clearClient();
                                    return 1;
                                }))
        );
    }
}
