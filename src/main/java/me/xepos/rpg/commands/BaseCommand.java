package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public abstract class BaseCommand implements TabExecutor {
    private final String permissionBase;

    public BaseCommand(String permissionBase){
        this.permissionBase = XRPG.permissionPrefix + permissionBase + ".";
    }

    protected boolean checkPermissions(CommandSender sender, String childPermission) {
        final String globalWildcard = XRPG.permissionPrefix + "*";
        final String wildcard = permissionBase + "*";
        if (StringUtils.isBlank(childPermission))
            return sender.hasPermission(wildcard) || sender.hasPermission(globalWildcard);
        else
            return sender.isOp() || sender.hasPermission(globalWildcard) || sender.hasPermission(wildcard) || sender.hasPermission(permissionBase + "*") || sender.hasPermission(permissionBase + childPermission);
    }
}
