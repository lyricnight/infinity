package me.lyric.infinity.gui.Csgo;

import me.lyric.infinity.api.setting.Setting;

public class CsgoSetting
{
    public int x;
    public int y;
    public int width;
    public int height;
    public Setting<?> setting;
    
    public CsgoSetting(final Setting<?> setting) {
        this.setting = setting;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
    }
    
    public void keyTyped(final char typedChar, final int keyCode) {
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
    }
    
    public boolean isInside(final int mouseX, final int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }
}
