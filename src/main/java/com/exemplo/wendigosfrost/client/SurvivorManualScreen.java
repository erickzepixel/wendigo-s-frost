package com.exemplo.wendigosfrost.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Playable, offline Survivor's Manual, replacing the fragile legacy
 * WrittenBook screen. Right-click the actual item to open the chapter index,
 * click a topic, navigate pages and scroll long text. All lore, craft advice
 * and commands are included in the mod; no wiki or server packet is needed.
 */
public final class SurvivorManualScreen extends Screen {
    private record Chapter(String title, String... paragraphs) {}

    private static final Chapter[] CHAPTERS = {
        new Chapter("A Vigília",
            "A neve chama uma criatura antiga. Explore os bosques e as ruínas, aprenda a cultivar a Flor-Gelo e prepare a proteção antes de despertar o Wendigo.",
            "O Manual do Sobrevivente é seu guia portátil: clique em um capítulo no índice, use Próximo e Anterior para avançar e a roda do mouse para ler textos maiores.",
            "O frio não é o único perigo. O Wendigo estuda e persegue o jogador, e o fogo continua sendo uma de suas principais fraquezas."),
        new Chapter("Flor-Gelo",
            "As Sementes de Flor-Gelo permitem cultivar Frostbloom em terra arada. A planta tem seis estágios de crescimento.",
            "Procure sementes principalmente nos baús das ruínas: Santuário Ancestral (2 a 5), Memorial dos Caçadores (1 a 3) e Santuário Congelado (3 a 6).",
            "Colha a planta madura para obter cristais. Guarde vários: eles são úteis em armas e rituais."),
        new Chapter("Cristal e Marca",
            "Consumir um Cristal Frostbloom provoca Delírio Gélido e aplica a Marca do Inverno. O efeito momentâneo e a marca persistente são fenômenos diferentes.",
            "Por padrão, após três dias de jogo (72.000 ticks), o Wendigo pode surgir perto do jogador marcado.",
            "A purificação da marca requer um pedestal e materiais raros. Prepare uma rota de fuga antes de tentar o ritual."),
        new Chapter("Wendigo — 500 HP",
            "O Wendigo possui 500 pontos de vida por padrão (250 corações). Essa vida pode ser configurada pelo servidor. Ele resiste aos ataques comuns, mas é vulnerável ao fogo.",
            "Fase 1: caçador quadrúpede, perseguições e rugidos. Fase 2: levanta-se quando a vida cai para aproximadamente 65% (325 HP); o coração exposto no peito é seu ponto fraco.",
            "Fase 3: começa perto de 30% (150 HP), com golpes mais perigosos. Os olhos permanecem glaciais e não há crescimento extra de espinhos nas costas.",
            "Observe partículas de garras, ondas de gelo e almas azuis: elas avisam quando um ataque está sendo preparado."),
        new Chapter("Armadura da Vigília",
            "Elmo, Peitoral, Calças e Botas da Vigília formam a armadura dos antigos caçadores. Cada peça equipada reduz em 8% o dano direto do Wendigo, até 32% com o conjunto completo.",
            "A armadura também oferece proteção normal contra outros danos. Seus modelos 3D representam gelo, ossos e runas de proteção.",
            "Monte o conjunto antes da primeira caçada: as peças usam materiais acessíveis sem precisar derrotar o chefe."),
        new Chapter("Armas contra a Fome",
            "Lança Rúnica do Caçador: causa 55% de dano extra no Wendigo quando seu coração está exposto; na primeira fase, o bônus é menor (15%).",
            "Machado do Degelo: recebe 27% de dano extra contra o Wendigo e o incendeia brevemente, ajudando a interromper ataques de gelo.",
            "Espada do Coração Partido: arma rara feita com um Coração Congelado. É especialmente eficaz na fase final do chefe e recompensa caçadas posteriores."),
        new Chapter("Utilitários e Amuleto",
            "Sinalizador Térmico: explode em uma onda de calor e incendeia Wendigos próximos por 6 segundos. É consumível e tem recarga de 24 segundos.",
            "Tocha Rúnica da Vigília: emite luz espiritual que reduz a velocidade e a força do Wendigo por 5 segundos. Pode ser reutilizada após 45 segundos.",
            "Amuleto de Proteção Gélida: produz uma Onda Glacial defensiva. Tem seis cargas e recarga entre ativações; use para escapar de um cerco.",
            "Selo do Inverno: relíquia que ajuda a explorar lugares escuros e concede proteção breve."),
        new Chapter("Altar de Invocação",
            "Monte o Pedestal das Geleiras Profundas no centro. Coloque quatro FOGUEIRAS DAS ALMAS ACESAS a dois blocos do pedestal nos quatro pontos cardeais.",
            "Use quatro blocos de GELO AZUL nas diagonais adjacentes, todos na mesma altura. Deixe espaço livre de pelo menos cinco blocos acima do altar.",
            "Agache e clique no pedestal com um Cristal Frostbloom na mão. O Wendigo será invocado imediatamente. Há uma recarga de dois minutos, e um chefe próximo impede uma segunda invocação.",
            "Vista de cima: fogueiras em N/S/L/O, gelo azul nas diagonais e pedestal no centro."),
        new Chapter("Ritual de Purificação",
            "Para remover a Marca do Inverno, coloque no Pedestal das Geleiras Profundas um Coração Congelado, um Chifre de Wendigo e um Cristal Frostbloom.",
            "Com os três ingredientes presentes, interaja com o pedestal com a mão vazia. Permaneça a até três blocos dele por 15 segundos.",
            "Se você se afastar demais ou receber dano, o ritual será interrompido. Prepare a área para não ser surpreendido."),
        new Chapter("Ruínas e Exploração",
            "As ruínas são localizáveis em chunks novos. Ative comandos e use:",
            "/locate structure wendigosfrost:ancient_ice_shrine",
            "/locate structure wendigosfrost:hunter_cairn",
            "/locate structure wendigosfrost:frozen_sanctuary",
            "O Santuário Congelado é subterrâneo. Procure os baús: além das sementes, há relíquias, páginas e Fragmentos do Sussurro."),
        new Chapter("História dos Caçadores",
            "Santuários de gelo e pedras rúnicas preservam a história dos caçadores que tentaram selar a criatura. Leia as Páginas do Caçador Desaparecido e toque nas pedras que sussurram.",
            "Os Fragmentos do Sussurro são materiais valiosos. O Selo do Inverno pode ser fabricado com fragmentos, ametista, gelo azul e Coração do Mar.",
            "A criatura não nasceu apenas do inverno, mas de uma fome antiga. A vigília não terminou."),
        new Chapter("Comandos de Teste",
            "Com comandos habilitados, invoque a criatura:",
            "/summon wendigosfrost:wendigo",
            "Comandos do mod para diagnóstico:",
            "/wendigofrost status",
            "/wendigofrost testmark",
            "/wendigofrost testspawn",
            "/wendigofrost diagnostico",
            "Se o /locate não encontrar estruturas, procure chunks que ainda não tenham sido gerados e confirme a versão do mod."),
        new Chapter("Estratégia de Sobrevivência",
            "Antes da luta: prepare comida, proteção contra lentidão e um conjunto da Vigília. Leve a Lança Rúnica, o Machado do Degelo e um Sinalizador Térmico.",
            "Na primeira fase, mantenha distância e observe os sinais do rugido. Na segunda, aproveite o coração exposto para causar dano adicional.",
            "Na terceira fase, não fique imóvel durante as ondas de choque. Use a Tocha Rúnica para abrir uma rota de fuga e o Sinalizador para interromper o frio.",
            "Este é um guia embutido para Minecraft Java 1.20.1, Forge 47.x, GeckoLib e TerraBlender.")
    };

    private final List<Button> chapterButtons = new ArrayList<>();
    private int page = -1; // -1 = chapter index
    private int scroll;
    private int contentHeight;
    private int viewportHeight;
    private int panelX,panelY,panelW,panelH;
    private Button previous,next,index;

    public SurvivorManualScreen() {
        super(Component.literal("Manual do Sobrevivente — Wendigo's Frost"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new SurvivorManualScreen());
    }

    @Override
    protected void init() {
        panelW=Math.min(480,width-18);
        panelH=Math.min(264,height-14);
        panelX=(width-panelW)/2;
        panelY=(height-panelH)/2;
        int gutter=18;
        int columnWidth=(panelW-3*gutter)/2;
        int firstY=panelY+42;
        int rows=(CHAPTERS.length+1)/2;
        int step=Math.max(19,Math.min(25,(panelH-85)/rows));
        for(int i=0;i<CHAPTERS.length;i++) {
            final int id=i;
            int col=i%2,row=i/2;
            Button button=Button.builder(Component.literal((i+1)+". "+CHAPTERS[i].title),
                b -> selectChapter(id))
                .bounds(panelX+gutter+col*(columnWidth+gutter),
                        firstY+row*step,columnWidth,Math.max(16,step-3)).build();
            chapterButtons.add(addRenderableWidget(button));
        }
        int navWidth=100;
        int spacing=8;
        int navX=panelX+(panelW-3*navWidth-2*spacing)/2;
        int navY=panelY+panelH-26;
        previous=addRenderableWidget(Button.builder(Component.literal("◀ Anterior"),
            b -> selectChapter(page>0?page-1:-1))
            .bounds(navX,navY,navWidth,20).build());
        index=addRenderableWidget(Button.builder(Component.literal("Índice"),
            b -> selectChapter(-1))
            .bounds(navX+navWidth+spacing,navY,navWidth,20).build());
        next=addRenderableWidget(Button.builder(Component.literal("Próximo ▶"),
            b -> selectChapter(Math.min(CHAPTERS.length-1,page+1)))
            .bounds(navX+2*(navWidth+spacing),navY,navWidth,20).build());
        syncButtons();
    }

    private void selectChapter(int value) {
        page=value;
        scroll=0;
        syncButtons();
    }

    private void syncButtons() {
        for(Button button:chapterButtons) button.visible=(page<0);
        previous.visible=page>=0;
        previous.active=page>=0;
        next.visible=page>=0;
        next.active=page<CHAPTERS.length-1;
        index.visible=page>=0;
    }

    @Override
    public void render(GuiGraphics gui,int mouseX,int mouseY,float partialTick) {
        gui.fillGradient(0,0,width,height,0xD4061322,0xEC0A2134);
        gui.fill(panelX-2,panelY-2,panelX+panelW+2,panelY+panelH+2,0xFF66BFD9);
        gui.fill(panelX,panelY,panelX+panelW,panelY+panelH,0xF8091C2A);
        gui.fill(panelX+12,panelY+12,panelX+panelW-12,panelY+14,0xFF85E3F4);
        gui.drawCenteredString(font,title, width/2,panelY+18,0xEAFBFF);

        if(page<0) {
            gui.drawCenteredString(font,"Selecione um capítulo para começar",width/2,
                panelY+32,0x9FD7E9);
        } else {
            Chapter chapter=CHAPTERS[page];
            gui.drawCenteredString(font,chapter.title,width/2,panelY+35,0x6EE7FF);
            int top=panelY+52,bottom=panelY+panelH-36;
            viewportHeight=Math.max(20,bottom-top);
            int left=panelX+20, textWidth=panelW-40;
            int y=top-scroll;
            gui.enableScissor(left,top,panelX+panelW-17,bottom);
            for(String paragraph:chapter.paragraphs()) {
                for(var line:font.split(Component.literal(paragraph),textWidth)) {
                    gui.drawString(font,line,left,y,0xDDE7F0,false);
                    y+=font.lineHeight+3;
                }
                y+=8;
            }
            gui.disableScissor();
            contentHeight=y+scroll-top;
            gui.drawCenteredString(font,(page+1)+" / "+CHAPTERS.length,
                width/2,panelY+panelH-39,0x8BBED4);
            if(contentHeight>viewportHeight)
                gui.drawString(font,"Role para ler ↓",panelX+panelW-104,
                    panelY+panelH-39,0x8BBED4,false);
        }
        super.render(gui,mouseX,mouseY,partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX,double mouseY,double delta) {
        if(page>=0) {
            scroll=Mth.clamp(scroll-(int)Math.signum(delta)*25,
                0,Math.max(0,contentHeight-viewportHeight+8));
            return true;
        }
        return super.mouseScrolled(mouseX,mouseY,delta);
    }

    @Override
    public boolean keyPressed(int key,int scancode,int modifiers) {
        if(key==GLFW.GLFW_KEY_RIGHT && page>=0) {
            selectChapter(Math.min(CHAPTERS.length-1,page+1));
            return true;
        }
        if(key==GLFW.GLFW_KEY_LEFT && page>=0) {
            selectChapter(page>0?page-1:-1);
            return true;
        }
        if(key==GLFW.GLFW_KEY_HOME) {
            selectChapter(-1);
            return true;
        }
        return super.keyPressed(key,scancode,modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
