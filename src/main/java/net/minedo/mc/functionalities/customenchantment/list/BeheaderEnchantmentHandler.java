package net.minedo.mc.functionalities.customenchantment.list;

import net.minedo.mc.Minedo;
import net.minedo.mc.constants.customenchantment.type.CustomEnchantmentType;
import net.minedo.mc.functionalities.customenchantment.CombatEvent;
import net.minedo.mc.functionalities.customenchantment.CustomEnchantmentHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Drops player head on kill.
 */
public class BeheaderEnchantmentHandler extends CustomEnchantmentHandler implements Listener {

    /**
     * Initialize beheader enchantment handler.
     */
    public BeheaderEnchantmentHandler() {
        super(CustomEnchantmentType.BEHEADER);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();
        CombatEvent combatEvent = super.isAbleToInflictCustomEnchantmentOnHit(defender, attacker);

        if (combatEvent == null || combatEvent.getCustomEnchantment() == null) {
            return;
        }

        LivingEntity defendingEntity = combatEvent.getDefendingEntity();

        if (!(defendingEntity instanceof Player)) {
            return;
        }

        if (defendingEntity.getHealth() > event.getFinalDamage()) {
            return;
        }

        OfflinePlayer offlinePlayer = Minedo.getInstance().getServer().getOfflinePlayer(defendingEntity.getUniqueId());
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwningPlayer(offlinePlayer);
        playerHead.setItemMeta(meta);

        defendingEntity.getWorld().dropItemNaturally(defendingEntity.getLocation(), playerHead);
    }

}
