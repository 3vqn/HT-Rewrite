package me.overruling.exeterimports.mcapi.settings;

import me.overruling.client.clickgui.components.buttons.settings.bettermode.BetterMode;

public class ModeSetting extends Setting {
    public final BetterMode[] modes;
    private int i = 0;

    public ModeSetting(String label, String[] aliases, int defaultIndex, BetterMode[] modes) {
        super(label, aliases);
        this.modes = modes;
        this.i = defaultIndex;
    }

    public String getValue() {
        return modes[i].mode;
    }
    public void setValue(int i) {
        this.i = i;
        if(i > modes.length-1 || i < 0)
            this.i = modes.length-1;
    }
    public int getI() { return i; }
    public BetterMode[] getModes() { return modes; }
    public BetterMode getModeByName(String name) {
        for(int i = 0; i < modes.length; i++)
            if(modes[i].mode.equalsIgnoreCase(name))
                return modes[i];
        return null;
    }
    public int getModeIndexByName(String name) {
        for(int i = 0; i < modes.length; i++)
            if(modes[i].mode.equalsIgnoreCase(name))
                return i;
        return -1;
    }

    public void increment() {
        i++;
        if(i > modes.length-1)
            i = 0;
    }

    public void decrement() {
        i--;
        if(i < 0)
            i = modes.length-1;
    }
}