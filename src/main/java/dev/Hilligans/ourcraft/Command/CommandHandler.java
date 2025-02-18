package dev.Hilligans.ourcraft.Command;

import dev.Hilligans.ourcraft.Command.CommandExecutors.CommandExecutor;
import dev.Hilligans.ourcraft.Entity.Entity;
import dev.Hilligans.ourcraft.Entity.LivingEntities.PlayerEntity;
import dev.Hilligans.ourcraft.GameInstance;
import dev.Hilligans.ourcraft.ModHandler.Mod;
import dev.Hilligans.ourcraft.ModHandler.ModID;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class CommandHandler {

    public GameInstance gameInstance;
    public ArrayList<String> aliases;
    public String command;
    public ModID mod;

    public CommandHandler(String command) {
        this.command = command;
        Commands.commands.put("/" + command,this);
    }

    public CommandHandler addAlias(String alias) {
        Commands.commands.put("/" + alias,this);
        return this;
    }

    public String getRegistryName() {
        return mod.getNamed(command);
    }

    public abstract Object handle(CommandExecutor executor, String[] args);

    public static boolean isNumber(String arg) {
        try {
            Float.parseFloat(arg);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public ArrayList<Entity> processSelector(String selector) {

        return null;
    }

    public Entity processSelectorSingle(String selector) {
        return null;
    }
}
