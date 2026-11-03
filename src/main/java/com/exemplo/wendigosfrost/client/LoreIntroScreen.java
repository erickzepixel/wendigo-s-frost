package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** A readable two-page introduction, deliberately lightweight and shader-free. */
public final class LoreIntroScreen extends Screen {
    private int page;
    private Button next;

    public LoreIntroScreen() {
        super(Component.translatable("screen.wendigosfrost.title"));
    }

    @Override
    protected void init() {
        int panelWidth=Math.min(452,width-24);
        int top=Math.max(14, height/2-104);
        next = Button.builder(Component.translatable("screen.wendigosfrost.next"),
            btn -> {
                if (page==0) {
                    page=1;
                    next.setMessage(Component.translatable("screen.wendigosfrost.enter"));
                } else onClose();
            }).bounds(width/2-76,Math.min(height-36,top+185),152,22).build();
        addRenderableWidget(next);
    }

    @Override
    public void render(GuiGraphics gui,int mouseX,int mouseY,float partialTick) {
        int w=Math.min(452,width-24);
        int x=(width-w)/2;
        int y=Math.max(14,height/2-104);
        gui.fillGradient(0,0,width,height,0xEA040C14,0xEC0C2438);
        gui.fill(x-2,y-2,x+w+2,y+211,0xFF4D8DA5);
        gui.fill(x,y,x+w,y+209,0xF7081928);
        gui.fill(x+12,y+13,x+w-12,y+15,0xFF78CCEA);
        gui.drawCenteredString(font,Component.translatable("screen.wendigosfrost.title"),
            width/2,y+23,0xDDF6FF);
        gui.drawCenteredString(font,Component.translatable(
            page==0 ? "screen.wendigosfrost.subtitle_1" : "screen.wendigosfrost.subtitle_2"),
            width/2,y+43,0x7BDCEB);
        int left=x+18, lineWidth=w-36;
        int color=0xC4DAE6;
        gui.drawWordWrap(font,Component.translatable(
            page==0 ? "screen.wendigosfrost.story_1" : "screen.wendigosfrost.story_2"),
            left,y+67,lineWidth,color);
        gui.drawWordWrap(font,Component.translatable(
            page==0 ? "screen.wendigosfrost.story_1b" : "screen.wendigosfrost.story_2b"),
            left,y+115,lineWidth,0xACD8E8);
        gui.drawCenteredString(font,(page+1)+"/2",width/2,y+167,0x88B9CF);
        super.render(gui,mouseX,mouseY,partialTick);
    }

    @Override
    public boolean isPauseScreen() { return true; }

    @Override
    public void onClose() {
        ModNetwork.acknowledgeIntroduction();
        super.onClose();
    }
}
