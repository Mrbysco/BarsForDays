package com.mrbysco.barsfordays.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.barsfordays.network.message.UpdateCustomBarPacket;
import com.mrbysco.barsfordays.storage.bar.BarInfo;
import com.mrbysco.barsfordays.storage.bar.ClientBarInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.UUID;

public class OverlayHandler extends AbstractGui {
	private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
	private static final Map<UUID, ClientBarInfo> events = Maps.newLinkedHashMap();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPreRender(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != ElementType.BOSSHEALTH)
			return;

		Minecraft mc = Minecraft.getInstance();
		MatrixStack poseStack = event.getMatrixStack();
		this.render(mc, poseStack);
	}

	public void render(Minecraft minecraft, MatrixStack poseStack) {
		if (!events.isEmpty()) {
			int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
			int scaledHeight = minecraft.getWindow().getGuiScaledHeight();

			for(ClientBarInfo clientBarInfo : events.values()) {
				double scale = clientBarInfo.getScale();
				if(scale < 0.5)
					scale = 0.5;
				double scaleMultiplier = 1 / scale;


				int offsetX = 0;
				if(clientBarInfo.getXPos() > 0)
					offsetX = (int)((clientBarInfo.getXPos() * scaleMultiplier));
				if(clientBarInfo.isXInverted())
					offsetX = (int)(scaledWidth * scaleMultiplier) - 184 - offsetX;

				int offsetY = 0;
				if(clientBarInfo.getYPos() > 0)
					offsetY = (int)((clientBarInfo.getYPos() * scaleMultiplier));
				if(clientBarInfo.isYInverted())
					offsetY = (int)(scaledHeight * scaleMultiplier) - offsetY;


				int posX = 92;
				if(clientBarInfo.isCenterX())
					posX += scaledWidth / 2;

				int posY = 12;
				if(clientBarInfo.isCenterY())
					posY += scaledHeight / 2;

				RenderSystem.pushMatrix();
				RenderSystem.enableRescaleNormal();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				if(scale > 0) RenderSystem.scaled(scale, scale, -0);

				minecraft.getTextureManager().bind(GUI_BARS_LOCATION);
				posX -= 91;
				this.drawBar(poseStack, posX + offsetX, posY + offsetY, clientBarInfo);

				ITextComponent itextcomponent = clientBarInfo.getName();
				int textWidth = minecraft.font.width(itextcomponent);
				posX += 91 - textWidth / 2;
				posY += -9;
				minecraft.font.drawShadow(poseStack, itextcomponent, (float)posX + offsetX, (float)posY + offsetY, 16777215);

				if(scale > 0) RenderSystem.scaled(scaleMultiplier, scaleMultiplier, -0);
				RenderSystem.disableRescaleNormal();
				RenderSystem.disableBlend();
				RenderSystem.popMatrix();
			}
		}
	}

	private void drawBar(MatrixStack poseStack, int x, int y, BarInfo barInfo) {
		this.blit(poseStack, x, y, 0,
				barInfo.getColor().ordinal() * 5 * 2, 182, 5);
		if (barInfo.getOverlay() != BarInfo.Overlay.PROGRESS) {
			this.blit(poseStack, x, y, 0,
					80 + (barInfo.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
		}

		int i = (int)(barInfo.getPercent() * 183.0F);
		if (i > 0) {
			this.blit(poseStack, x, y, 0,
					barInfo.getColor().ordinal() * 5 * 2 + 5, i, 5);
			if (barInfo.getOverlay() != BarInfo.Overlay.PROGRESS) {
				this.blit(poseStack, x, y, 0,
						80 + (barInfo.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
			}
		}

	}

	public static void update(UpdateCustomBarPacket packet) {
		if (packet.getOperation() == UpdateCustomBarPacket.Operation.ADD) {
			events.put(packet.getId(), new ClientBarInfo(packet));
		} else if (packet.getOperation() == UpdateCustomBarPacket.Operation.REMOVE) {
			events.remove(packet.getId());
		} else {
			events.get(packet.getId()).update(packet);
		}
	}

	public static void reset() {
		events.clear();
	}
}
