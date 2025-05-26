package toni.jtn.content.lore;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import toni.immersivemessages.ImmersiveFont;
import toni.immersivemessages.ImmersiveMessagesManager;
import toni.immersivemessages.api.ImmersiveMessage;

import java.util.function.Consumer;

public class PoemHandler {
    public static void sendYggdrvald(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
        if (level.isClientSide)
            return;

        ImmersiveMessage.builder(10f, "MAKE THE SACRIFICE")
            .subtext(2f, "[10 Ancient Bones]", 13, subtext -> subtext
                .font(ImmersiveFont.ROBOTO)
                .fadeIn(3f)
                .fadeOut())
            .color(ChatFormatting.RED)
            .font(ImmersiveFont.NORSE)
            .bold()
            .obfuscate(0.5f)
            .slideUp()
            .fadeIn(3f)
            .fadeOut()
            .shake()
            .size(2f)
            .sendServer((ServerPlayer) player);

        ImmersiveMessage.builder(4f, "YOUR SACRIFICE IS ACCEPTED")
            .color(ChatFormatting.RED)
            .font(ImmersiveFont.NORSE)
            .bold()
            .obfuscate(0.5f)
            .fadeIn(0.5f)
            .fadeOut(0.5f)
            .shake()
            .size(2f)
            .sendServer((ServerPlayer) player);

        sendPoemSegment("The ground trembles, the forest shakes", player, 3.75f, null);
        sendPoemSegment("The air grows thick, the earth forsakes", player, 3.75f, null);
        sendPoemSegment("The woods bear witness to those who died", player, 3.75f, null);
        sendPoemSegment("With the blood of beasts, on bark, now dried", player, 3.75f, null);

        sendPoemSegment("With trembling hands, the brave men stood", player, 3.75f, null);
        sendPoemSegment("To face the god of root and wood", player, 3.75f, null);
        sendPoemSegment("Beneath the leaves, they could not hide", player, 3.75f, null);
        sendPoemSegment("THE FOREST SWALLOWED ALL WHO TRIED", player, 4.75f, tooltip -> tooltip.color(ChatFormatting.RED).size(2f));


        ImmersiveMessage.builder(8f, "YGGDRVALD AWAKENS")
            .subtext(2f, "God of the Forest", 13, subtext -> subtext
                .font(ImmersiveFont.ROBOTO)
                .italic()
                .fadeIn(3f)
                .shake()
                .fadeOut())
            .color(ChatFormatting.DARK_GREEN)
            .font(ImmersiveFont.NORSE)
            .bold()
            .obfuscate(0.3f)
            .fadeIn(3f)
            .fadeOut()
            .shake(125f, 1f)
            .size(2.5f)
            .sendServer((ServerPlayer) player);

//            ImmersiveMessage.builder(10f, "The wind howls, the night draws near.")
//                    .subtext(2f, "Seek shelter, build a campfire.", subtext -> subtext
//                            .font(ImmersiveFont.ROBOTO)
//                            .italic()
//                            .fadeIn(3f)
//                            .fadeOut())
//                    .font(ImmersiveFont.NORSE)
//                    .bold()
//                    .obfuscate()
//                    .slideUp()
//                    .fadeIn(3f)
//                    .fadeOut()
//                    .size(1.5f)
//                    .send(player);
    }


    public static void sendPoemSegment(String text, Player player, float duration, Consumer<ImmersiveMessage> consumer) {
        ImmersiveMessage.builder(duration, text)
            .color(ChatFormatting.WHITE)
            .font(ImmersiveFont.NORSE)
            .bold()
            .italic()
            .obfuscate(1.2f)
            .fadeIn(0.2f)
            .fadeOut(0.2f)
            .shake(75f, 0.4f)
            .size(1.5f)
            .apply(tooltip -> { if (consumer != null) consumer.accept(tooltip); })
            .sendServer((ServerPlayer) player);
    }
}
