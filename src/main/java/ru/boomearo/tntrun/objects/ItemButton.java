package ru.boomearo.tntrun.objects;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.GameControlException;

public enum ItemButton {

    LEAVE(8) {

        @Override
        public ItemStack getItem() {
            ItemStack item = new ItemStack(Material.MAGMA_CREAM, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§cПокинуть игру §8[§cSHIFT+ПКМ§8]");
            meta.setLore(Arrays.asList("§fКликните чтобы покинуть игру."));
            meta.addEnchant(Enchantment.DIG_SPEED, 1, true);
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
            return item;
        }

        @Override
        public void handleClick(TntPlayer player) {
            if (player.getPlayer().isSneaking()) {
                try {
                    GameControl.getInstance().getGameManager().leaveGame(player.getPlayer());
                }
                catch (GameControlException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    private final int slot;

    ItemButton(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public abstract ItemStack getItem();
    public abstract void handleClick(TntPlayer player);

    public static ItemButton getButtonByItem(ItemStack item) {
        for (ItemButton ib : values()) {
            if (ib.getItem().isSimilar(item)) {
                return ib;
            }
        }
        return null;
    }

}
