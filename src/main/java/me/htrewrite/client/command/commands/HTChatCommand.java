package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.client.util.ConfigUtils;
import me.pk2.chatserver.ChatAPI;
import me.pk2.chatserver.clientside.objects.KeepAliveResponse;
import me.pk2.chatserver.message.Message;
import net.minecraft.util.text.TextComponentString;

public class HTChatCommand extends Command {
    ConfigUtils authConfigUtils;
    String username;
    Thread thread;

    public HTChatCommand() {
        super("ht-chat", "<message>", "Chat for people with access to HT+Rewrite.");
        authConfigUtils = new ConfigUtils("auth", "");
        username = (String)authConfigUtils.get("u");
        HTRewrite.executorService.submit(() -> ChatAPI.handshake(username));
        thread = new Thread(()->{
            while (true) {
                KeepAliveResponse aliveResponse = ChatAPI.keepAlive();
                for (Message msg : aliveResponse.queued_messages)
                    if (mc.world != null && mc.player != null)
                        mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&d" + msg.user.username + " &7&l-> &r" + msg.message)));
                try { Thread.sleep(5000); } catch (Exception exception) {}
            }
        });
        thread.start();
    }

    @Override
    public void call(String[] args) {
        if(args.length < 1) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&c"+formatCmd(getAlias() + " " + getUsage()))));
            return;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++)
            builder.append(args[i]+(i==args.length-1?"":" "));
        final String message = builder.toString();
        HTRewrite.executorService.submit(() -> {
            ChatAPI.sendMessage(message);
            KeepAliveResponse aliveResponse = ChatAPI.keepAlive();
            for(Message msg : aliveResponse.queued_messages)
                if(mc.world != null && mc.player != null)
                    mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&d" + msg.user.username + " &7&l-> &r" + msg.message)));
        });
    }
}