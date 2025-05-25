package toni.jtn.foundation.accessors;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

public interface IComponentTooltip {

    void jtn$RenderComponentTooltip(Font font, List<? extends FormattedText> tooltips, int mouseX, int mouseY);
}
