package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.TickedTimer;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Random;

public class SpammerModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("LIST", "RANDOM"));
    public static final StringSetting spammerFile = new StringSetting("File", null, "spammer.txt");
    public static final ToggleableSetting usePackets = new ToggleableSetting("UsePackets", null, false);
    public static final ValueSetting<Double> delay = new ValueSetting<>("Delay", null, 5d, 1d, 60d);

    private final String directory = "htRewrite\\modules\\Miscellaneous\\Spammer\\";
    private File currentFile = null;
    private String[] lines = null;
    private TickedTimer tickedTimer;
    public SpammerModule() {
        super("Spammer", "Spams stuff on chat.", ModuleType.Miscellaneous, 0);
        addOption(mode);
        addOption(spammerFile);
        addOption(usePackets);
        addOption(delay);
        endOption();

        File file = new File(directory+"spammer.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("Join the best anarchy client discord! https://discord.gg/2b2thack");
                fileWriter.close();
            } catch (Exception exception) { exception.printStackTrace(); }
        }

        tickedTimer = new TickedTimer();
        tickedTimer.stop();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        currentFile = new File(directory + spammerFile.getValue());
        if(!currentFile.exists()) {
            sendMessage("&cThe file &4" + spammerFile.getValue() + "&c does not exists!");
            toggle();
            return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(currentFile);
            byte[] byteArray = new byte[(int)currentFile.length()];
            fileInputStream.read(byteArray);
            String data = new String(byteArray);
            String[] stringArray = data.split("\n");
            lines = stringArray;
        } catch (Exception exception) {
            exception.printStackTrace();
            sendMessage("&cThere was an error! Please check console log and make a ticket with it.");
            toggle();
            return;
        }

        if(lines.length == 0) {
            sendMessage("&cThe file is empty!");
            toggle();
            return;
        }

        tickedTimer.reset();
        tickedTimer.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        tickedTimer.stop();
    }

    private void sendChatMessage(String message) {
        if(usePackets.isEnabled())
            mc.player.connection.sendPacket(new CPacketChatMessage(message));
        else mc.player.sendChatMessage(message);
    }

    private int listIndex = 0;

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        int realDelay = delay.getValue().intValue()*20;
        if(tickedTimer.passed(realDelay)) {
            switch(mode.getValue()) {
                case "LIST": {
                    if(listIndex >= lines.length)
                        listIndex = 0;

                    sendChatMessage(lines[listIndex++]);
                } break;

                case "RANDOM": {
                    int index = 0;
                    if(lines.length > 1)
                        index = new Random().nextInt(lines.length);

                    sendChatMessage(lines[index]);
                } break;

                default:
                    break;
            }

            tickedTimer.reset();
        }
    });
}