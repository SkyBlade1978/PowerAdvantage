package com.mcmoddev.advantageworks;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class AdvantageWorksCommand extends CommandBase {
    @Override
    public String getName() {
        return "advworks";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/advworks <build|reset|start|stop|check|status|checkpoint|sample-worldgen> [target] [name]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException(getUsage(sender));
        }
        WorksController controller = WorksController.getInstance();
        controller.requireAuthorizedWorld(server);
        String operation = args[0].toLowerCase();
        String target = args.length > 1 ? args[1] : "all";
        String message;
        switch (operation) {
            case "build":
                message = controller.build(server, target);
                break;
            case "reset":
                message = controller.reset(server, target);
                break;
            case "start":
                message = controller.start(server, target);
                break;
            case "stop":
                message = controller.stop(server, target);
                break;
            case "check":
                message = controller.check(server, target);
                break;
            case "status":
                message = controller.status(target);
                break;
            case "checkpoint":
                if (args.length < 3) throw new CommandException("Checkpoint requires a station and name");
                message = controller.checkpoint(server, target, args[2]);
                break;
            case "sample-worldgen":
                if (args.length < 2) throw new CommandException("Worldgen sampling requires a chunk radius");
                int radius;
                try {
                    radius = Integer.parseInt(args[1]);
                } catch (NumberFormatException failure) {
                    throw new CommandException("Worldgen chunk radius must be a number");
                }
                message = controller.sampleWorldgen(server, radius);
                break;
            default:
                throw new CommandException("Unknown Advantage Works operation: " + operation);
        }
        sender.sendMessage(new TextComponentString(message));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
                                           String[] args, net.minecraft.util.math.BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args,
                    Arrays.asList("build", "reset", "start", "stop", "check", "status", "checkpoint", "sample-worldgen"));
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, WorksController.getInstance().stationIdsWithAll());
        }
        return Collections.emptyList();
    }
}
