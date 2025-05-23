package toni.jtn;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class JTNClient {
    public static long ticks = 0;

    public static @Nullable Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static float getColorTicks() {
        return (ticks + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)) / 0.5F;
    }
}
