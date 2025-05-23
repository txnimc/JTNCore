package toni.jtn.content.runes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import net.minecraft.world.entity.Entity;
import toni.jtn.JTN;
import toni.jtn.content.runes.gem.GemInstance;
import toni.jtn.content.runes.gem.UnsocketedGem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import toni.jtn.foundation.Registration.Components;
import toni.jtn.foundation.codec.CachedObject;
import toni.jtn.foundation.mixin.ItemContainerContentsAccessor;

/**
 * Utility class for the manipulation of Sockets on items.
 * <p>
 * Sockets may only be applied to items which are of a valid loot category.
 */
public class SocketHelper {

    public static final ResourceLocation GEMS_CACHED_OBJECT = JTN.location("gems");

    private static final ToIntFunction<ItemStack> SOCKET_DEPENDENT_COMPONENTS_HASHER = CachedObject.hashComponents(Components.GEM, Components.PURITY, Components.SOCKETED_GEMS);

    /**
     * Gets the number of sockets on an items.
     * By default, this equals the nbt-encoded socket count, but it may be modified by GetItemSocketsEvent
     *
     * @param stack The stack being queried.
     * @return The number of sockets on the stack.
     */
    public static int getSockets(ItemStack stack) {
        int sockets = stack.getOrDefault(Components.SOCKETS, 0);
        //var event = new GetItemSocketsEvent(stack, sockets);
        //NeoForge.EVENT_BUS.post(event);
        //return event.getSockets();
        return sockets;
    }

    /**
     * Sets the number of sockets on the items to the specified value.
     * <p>
     * The value set here is not necessarily the value that will be returned by {@link #getSockets(ItemStack)} due to GetItemSocketsEvent
     *
     * @param stack   The stack being modified.
     * @param sockets The number of sockets.
     */
    public static void setSockets(ItemStack stack, int sockets) {
        stack.set(Components.SOCKETS, Mth.clamp(sockets, 0, 16));
    }

    /**
     * Gets the list of gems socketed into the items. Gems in the list may be unbound, invalid, or empty.
     *
     * @param stack The stack being queried
     * @return An immutable list of all gems socketed in this items. This list is cached.
     */
    public static SocketedGems getGems(ItemStack stack) {
        return CachedObject.CachedObjectSource.getOrCreate(stack, GEMS_CACHED_OBJECT, SocketHelper::getGemsImpl, SocketHelper::hashSockets);
    }

    /**
     * Computes the invalidation hash for the SocketedGems cache. The hash changes if the number of sockets changes, or the affix data changes.
     */
    private static int hashSockets(ItemStack stack) {
        return Objects.hash(SOCKET_DEPENDENT_COMPONENTS_HASHER.applyAsInt(stack), getSockets(stack));
    }

    /**
     * Implementation for {@link #getGems(ItemStack)}
     */
    private static SocketedGems getGemsImpl(ItemStack stack) {
        int size = getSockets(stack);
        if (size <= 0 || stack.isEmpty()) {
            return SocketedGems.EMPTY;
        }

        LootCategory cat = LootCategory.forItem(stack);
        if (cat.isNone()) {
            return SocketedGems.EMPTY;
        }

        NonNullList<GemInstance> list = NonNullList.withSize(size, GemInstance.EMPTY);
        var socketedGems = (ItemContainerContentsAccessor) (Object) stack.getOrDefault(Components.SOCKETED_GEMS, ItemContainerContents.EMPTY);

        for (int i = 0; i < Math.min(size, socketedGems.getItems().size()); i++) {
            ItemStack gem = getStackInSlot(socketedGems, i);
            if (!gem.isEmpty()) {
                gem.setCount(1);
                GemInstance inst = GemInstance.socketed(stack, gem, i);
                list.set(i, inst);
            }
        }

        return new SocketedGems(list);
    }

    private static ItemStack getStackInSlot(ItemContainerContentsAccessor accessor, int slot) {
        if (slot < 0 || slot >= accessor.getItems().size())
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + accessor.getItems().size() + ")");

        return ((ItemStack)accessor.getItems().get(slot)).copy();
    }

    /**
     * Sets the gem list on the items to the provided list of gems.<br>
     * Setting more gems than there are sockets will cause the extra gems to be lost.
     *
     * @param stack The stack being modified.
     * @param gems  The list of socketed gems.
     */
    public static void setGems(ItemStack stack, SocketedGems gems) {
        var contents = ItemContainerContents.fromItems(gems.stream().map(GemInstance::gemStack).toList());
        stack.set(Components.SOCKETED_GEMS, contents);
    }

    /**
     * Checks if any of the sockets on the items are empty.
     *
     * @param stack The stack being queried.
     * @return True, if any sockets are empty, otherwise false.
     */
    public static boolean hasEmptySockets(ItemStack stack) {
        return getGems(stack).gems().stream().anyMatch(g -> !g.isValid());
    }

    /**
     * Computes the index of the first empty socket, used during socketing.
     *
     * @param stack The stack being queried.
     * @return The index of the first empty socket in the stack's gem list.
     * @see #getGems(ItemStack)
     */
    public static int getFirstEmptySocket(ItemStack stack) {
        SocketedGems gems = getGems(stack);
        for (int socket = 0; socket < gems.size(); socket++) {
            if (!gems.get(socket).isValid()) {
                return socket;
            }
        }
        return 0;
    }

    /**
     * Checks if a gem can be applied to a given {@link ItemStack}.
     * <p>
     * A gem may be socketed into an items if the items has empty sockets, the gem matches the items, and no other mod changes the rules.
     * 
     * @param stack    The items being socketed into
     * @param gemStack The gem to socket
     * @return True if the gem may be socketed into the items.
     */
    public static boolean canSocketGemInItem(ItemStack stack, ItemStack gemStack) {
        UnsocketedGem gem = UnsocketedGem.of(gemStack);

        if (!gem.isValid() || !SocketHelper.hasEmptySockets(stack)) {
            return false;
        }
//
//        CanSocketGemEvent event = NeoForge.EVENT_BUS.post(new CanSocketGemEvent(stack, gemStack));
//        return !event.isCanceled() && gem.canApplyTo(stack);

        return gem.canApplyTo(stack);
    }

    /**
     * Sockets a gem into an items and returns the result of doing so.
     * If the items cannot be socketed (per {@link #canSocketGemInItem(ItemStack, ItemStack)} an empty stack is returned.
     * <p>
     * This method does not modify the input {@code stack}.
     * <p>
     * This method fires the ItemSocketingEvent before returning the final result.
     *
     * @param stack    The items being socketed into
     * @param gemStack The gem to socket
     * @return A copy of the items with the gem socketed into it, or {@link ItemStack#EMPTY} if the action could not be performed.
     * @apiNote If you only care about attempting to socket a gem, you do not need to manually call {@link #canSocketGemInItem}.
     */
    public static ItemStack socketGemInItem(ItemStack stack, ItemStack gemStack) {
        if (!canSocketGemInItem(stack, gemStack)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.copy();
        result.setCount(1);
        int socket = SocketHelper.getFirstEmptySocket(result);
        List<GemInstance> gems = new ArrayList<>(SocketHelper.getGems(result).gems());
        ItemStack gemToInsert = gemStack.copy();
        gemToInsert.setCount(1);
        gems.set(socket, GemInstance.socketed(result, gemStack.copy(), socket));
        SocketHelper.setGems(result, new SocketedGems(gems));

        return result;
//        ItemSocketingEvent event = NeoForge.EVENT_BUS.post(new ItemSocketingEvent(stack, gemToInsert, result));
//        return event.getOutput();
    }

//    /**
//     * Gets a stream of socketed gems that are valid for use by the arrow.
//     *
//     * @param proj The arrow being queried.
//     * @return A stream containing all valid gems in the arrow.
//     * @see GemInstance#isValid()
//     */
//    public static Stream<GemInstance> getGemInstances(Projectile proj) {
//        ItemStack stack = AffixHelper.getSourceWeapon(proj);
//        return getGems(stack).stream().filter(GemInstance::isValid);
//    }
//
//    public static ItemStack getSourceWeapon(Entity entity) {
//        if (entity.getPersistentData().contains(SOURCE_WEAPON)) {
//            return ItemStack.parseOptional(entity.level().registryAccess(), entity.getPersistentData().getCompound(SOURCE_WEAPON));
//        }
//        return ItemStack.EMPTY;
//    }
}
