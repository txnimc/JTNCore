package toni.jtn.content.runes.gem.bonus;

import java.util.function.Consumer;


import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;
import toni.jtn.JTN;
import toni.jtn.content.runes.gem.GemClass;
import toni.jtn.content.runes.gem.GemInstance;
import toni.jtn.content.runes.gem.GemView;
import toni.jtn.content.runes.gem.Purity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.HitResult;
import toni.jtn.foundation.codec.CodecMap;
import toni.jtn.foundation.codec.CodecProvider;
import toni.jtn.foundation.events.AttributeTooltipContext;
import toni.jtn.foundation.events.GetEnchantmentLevelEvent;

public abstract class GemBonus implements CodecProvider<GemBonus> {

    // TODO: Convert to Registry<Codec<?>> instead of using a raw codec map.
    public static final CodecMap<GemBonus> CODEC = new CodecMap<>("Gem Bonus");

    protected final GemClass gemClass;

    public GemBonus(GemClass gemClass) {
        this.gemClass = gemClass;
    }

    /**
     * Checks if this bonus supports this purity.
     *
     * @param purity The purity being checked.
     * @return True, if this bonus contains values for the specified purity.
     * @apiNote Other methods in this class will throw an exception if the bonus does not support this purity.
     */
    public abstract boolean supports(Purity purity);

    /**
     * Gets the one-line socket bonus tooltip.
     *
     * @param gem    The gem view.
     */
    public abstract Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx);

    /**
     * Retrieve the modifiers from this bonus to be applied to the socketed stack.
     * All modifiers for all slots should be supplied unconditionally.
     * <p>
     * To generate modifier ids, use {@link #makeModifierId(GemInstance, EquipmentSlotGroup, String)}
     *
     * @param inst  The current gem instance.
     * @param event The attribute modifier event, which will accept any created modifiers.
     */
    //public void addModifiers(GemInstance inst, StackAttributeModifiersEvent event) {}

    /**
     * Calculates the protection value of this bonus, with respect to the given damage source.
     *
     * @param inst   The current gem instance.
     * @param source The damage source to compare against.
     * @return How many protection points this affix is worth against this source.
     */
    public float getDamageProtection(GemInstance inst, DamageSource source) {
        return 0;
    }

    /**
     * Calculates the additional damage this bonus provides.
     * This damage is dealt as player physical damage.
     *
     * @param inst The current gem instance.
     */
    public float getDamageBonus(GemInstance inst, Entity target) {
        return 0.0F;
    }

    /**
     * Called when someone attacks an entity with an items that has this bonus.<br>
     * Specifically, this is invoked whenever the user attacks a target, while having an items with this bonus in either hand or any armor slot.
     *
     * @param inst   The current gem instance.
     * @param user   The wielder of the weapon. The weapon stack will be in their main hand.
     * @param target The target entity being attacked.
     */
    public void doPostAttack(GemInstance inst, LivingEntity user, @Nullable Entity target) {}

    /**
     * Called when an entity that has this bonus on one of its armor items is damaged.
     *
     * @param inst   The current gem instance.
     * @param user   The entity wearing an items with this bonus.
     * @param source The source of the attack.
     */
    public void doPostHurt(GemInstance inst, LivingEntity user, DamageSource source) {}

    /**
     * Called when a user fires a projectile from a weapon with this affix on it.
     */
    public void onProjectileFired(GemInstance inst, LivingEntity user, Projectile proj) {}

    /**
     * Called when Item#useOn(ItemUseContext) would be called for an items with this affix.
     * Return null to not impact the original result type.
     */
    @Nullable
    public InteractionResult onItemUse(GemInstance inst, UseOnContext ctx) {
        return null;
    }

    /**
     * Called when a projectile that was marked with this affix hits a target.
     */
    public void onProjectileImpact(GemInstance inst, Projectile proj, HitResult res) {}

    /**
     * Called when a shield with this affix blocks some amount of damage.
     *
     * @param inst   The current gem instance.
     * @param entity The blocking entity.
     * @param source The damage source being blocked.
     * @param amount The amount of damage blocked.
     * @return The amount of damage that is *actually* blocked by the shield, after this affix applies.
     */
    public float onShieldBlock(GemInstance inst, LivingEntity entity, DamageSource source, float amount) {
        return amount;
    }

    /**
     * Called when a player with this affix breaks a block.
     *
     * @param inst   The current gem instance.
     * @param player The breaking player.
     * @param level  The level the block was broken in.
     * @param pos    The position of the block.
     * @param state  The state that was broken.
     */
    public void onBlockBreak(GemInstance inst, Player player, LevelAccessor level, BlockPos pos, BlockState state) {

    }

    /**
     * Allows an affix to reduce durability damage to an items.
     *
     * @param inst The current gem instance.
     * @return The percentage [0, 1] of durability damage to ignore. This value will be summed with all other affixes that increase it.
     */
    public float getDurabilityBonusPercentage(GemInstance inst) {
        return 0;
    }

    /**
     * Fires during the LivingHurtEvent, and allows for modification of the damage value.<br>
     * If the value is set to zero or below, the event will be cancelled.
     *
     * @param inst   The current gem instance.
     * @param src    The Damage Source of the attack.
     * @param user   The entity being attacked.
     * @param amount The amount of damage that is to be taken.
     * @return The amount of damage that will be taken, after modification. This value will propagate to other bonuses.
     */
    public float onHurt(GemInstance inst, DamageSource src, LivingEntity user, float amount) {
        return amount;
    }

    /**
     * Fires during GetEnchantmentLevelEvent} and allows for increasing enchantment levels.
     *
     * @param inst  The current gem instance.
     * @param event The GetEnchantmentLevelEvent, which allows for modification of enchantment levels.
     */
    public void getEnchantmentLevels(GemInstance inst, GetEnchantmentLevelEvent event) {}

    /**
     * Fires from LootModifier#apply(ObjectArrayList, LootContext)} when this bonus is active on the tool given by the context.
     *
     * @param inst The current gem instance.
     * @param loot The generated loot.
     * @param ctx  The loot context.
     */
    public void modifyLoot(GemInstance inst, ObjectArrayList<ItemStack> loot, LootContext ctx) {}

    /**
     * Fires from the GatherSkippedAttributeTooltipsEvent to allow the gem to hide any relevant attribute modifiers.
     * <p>
     * If a bonus implements #addModifiers(GemInstance, ItemAttributeModifierEvent), it should override this method as well to hide the modifiers.
     *
     * @param inst The current gem instance.
     * @param skip A consumer that accepts resource locations to skip.
     */
    public void skipModifierIds(GemInstance inst, Consumer<ResourceLocation> skip) {}

    /**
     * Returns the serialization key for this GemBonus.
     * <p>
     * This is unique on a per-type basis, rather than per-instance basis.
     */
    public final ResourceLocation getTypeKey() {
        return GemBonus.CODEC.getKey(this.getCodec());
    }

    public final GemClass getGemClass() {
        return this.gemClass;
    }

    /**
     * Generates a deterministic {@link ResourceLocation} that is unique for a given socketed gem instance.
     * <p>
     * Can be used to generate attribute modifiers, track cooldowns, and other things that need to be unique per-gem-in-slot.
     *
     * @param view The owning gem instance for the bonus
     * @param salt A salt value, which can be used if the bonus needs multiple modifiers.
     */
    protected static ResourceLocation makeUniqueId(GemView view, String salt) {
        String path = view.gem().getId().getPath() + "_modifier_";
        if (view instanceof GemInstance inst) {
            //path += inst.category().getSlots().id().toShortLanguageKey() + "_" + inst.slot();
        }
        return ResourceLocation.fromNamespaceAndPath(view.gem().getId().getNamespace(), path + salt);
    }

    /**
     * Calls #makeUniqueId(GemInstance, String) with an empty salt value.
     */
    protected static ResourceLocation makeUniqueId(GemView inst) {
        return makeUniqueId(inst, "");
    }

    public static void initCodecs() {
        register("enchantment", EnchantmentBonus.CODEC);
    }

    protected static <T extends GemBonus> App<RecordCodecBuilder.Mu<T>, GemClass> gemClass() {
        return GemClass.CODEC.fieldOf("gem_class").forGetter(GemBonus::getGemClass);
    }

    private static void register(String id, Codec<? extends GemBonus> codec) {
        CODEC.register(JTN.location(id), codec);
    }

    public static abstract class Builder {

        public abstract GemBonus build(GemClass gClass);
    }

}
