package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.*;

/**
 * /recipe command.
 */
public class CommandRecipe extends FoundryPlayerCommand {
    private final Essence plugin;
    private UtilMessage msg;

    /**
     * Constructor for the CommandRecipe class.
     * @param plugin References to the main plugin class.
     */
    public CommandRecipe(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.inventory.recipe";
    }

    /**
     * @param cs       Information about who sent the command - player or console.
     * @param command  Information about what command was sent.
     * @param s        Command label - not used here.
     * @param args     The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        Player p = (Player) cs;

        this.msg = new UtilMessage(this.plugin, cs);

        if (args.length > 1) {
            this.msg.send("recipe","usage");
            return true;
        }

        Material material = null;
        if (args.length > 0) {
            material = Material.matchMaterial(args[0]);
        } else if (p.getInventory().getItemInMainHand() != null) {
            material = p.getInventory().getItemInMainHand().getType();
        }

        if (material == null) {
            this.msg.send("generic", "itemnotfound", new String[]{args.length > 0 ? args[0] : "UNKNOWN"});
        } else if (material == Material.AIR) {
            this.msg.send("recipe", "norecipe", new String[]{"AIR"});
        } else {
            this.showRecipe(p, material);
        }

        return true;
    }

    /**
     * Shows a recipe to the player.
     * @param player Player - the player.
     * @param material Material - the material.
     */
    private void showRecipe(Player player, Material material) {
        List<Recipe> recipes = Bukkit.getServer().getRecipesFor(new ItemStack(material));

        if (recipes.isEmpty()) {
            this.msg.send("recipe", "norecipe", new String[]{material.name()});
            return;
        }

        Recipe recipe = recipes.getFirst();
        Inventory inv;
        TypeReadOnlyInventory holder = new TypeReadOnlyInventory();

        if (recipe instanceof ShapelessRecipe shapeless) {
            inv = Bukkit.createInventory(holder, InventoryType.WORKBENCH, "Recipe: " + material.name());

            int slot = 1;
            for (ItemStack i : shapeless.getIngredientList()) {
                inv.setItem(slot++, i);
            }

            inv.setItem(0, new ItemStack(material));
        } else if (recipe instanceof ShapedRecipe shaped) {
            inv = Bukkit.createInventory(holder, InventoryType.WORKBENCH, "Recipe: " + material.name());

            String[]shape = shaped.getShape(); // [a,b,c] [abc,def,ghi]
            Map<Character, RecipeChoice> choiceMap = shaped.getChoiceMap();

            int r = 1;
            for (String s : shape) {
                char[] charArray = s.toCharArray();
                int i = 1;
                for (char c : charArray) {
                    RecipeChoice choice = choiceMap.get(c);
                    if (choice != null) {
                        inv.setItem((r - 1) * 3 + i, choice.getItemStack());
                    } else {
                        inv.setItem((r - 1) * 3 + i, null); // empty slot
                    }
                    i++;
                }
                r++;
            }

            inv.setItem(0, new ItemStack(material));
        } else if (recipe instanceof FurnaceRecipe furnace) {
            inv = Bukkit.createInventory(holder, InventoryType.FURNACE, "Recipe: " + material.name());
            inv.setItem(0, furnace.getInput());
            inv.setItem(2, furnace.getResult());
        } else if (recipe instanceof StonecuttingRecipe stonecut) {
            inv = Bukkit.createInventory(holder, InventoryType.STONECUTTER, "Recipe: " + material.name());
            inv.setItem(0, stonecut.getInput());
            inv.setItem(2, stonecut.getResult());
        } else {
            this.msg.send("recipe", "notsupported", new String[]{material.name()});
            return;
        }

        player.openInventory(inv);
    }
}