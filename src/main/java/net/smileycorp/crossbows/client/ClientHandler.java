package net.smileycorp.crossbows.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.item.ItemCrossbow;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHandler {
	
	@SubscribeEvent
	public static void renderHand(RenderSpecificHandEvent event) {
		EnumHand hand = event.getHand();
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null) return;
		if (hand == EnumHand.MAIN_HAND) {
			if (player.getActiveHand() != EnumHand.OFF_HAND) return;
			if (player.getActiveItemStack().getItem() == CrossbowsContent.CROSSBOW) event.setCanceled(true);
		}
		else if (hand == EnumHand.OFF_HAND) {
			if (player.getActiveHand() == EnumHand.MAIN_HAND && player.getActiveItemStack().getItem() == CrossbowsContent.CROSSBOW) event.setCanceled(true);
			ItemStack main = player.getHeldItemMainhand();
			if (main.getItem() == CrossbowsContent.CROSSBOW && ItemCrossbow.isCharged(main)) event.setCanceled(true);
		}
	}

}
