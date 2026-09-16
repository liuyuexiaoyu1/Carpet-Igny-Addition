package com.liuyue.igny.utils.uncraftingTable;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//#if MC >= 12103
//$$ import net.minecraft.world.item.crafting.CraftingInput;
//$$ import java.util.Optional;
//$$ import com.liuyue.igny.mixins.rule.uncraftingTable.ShapelessRecipeAccessor;
//#endif
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
//#if MC >= 12002
import net.minecraft.world.item.crafting.RecipeHolder;
//#endif
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

//#if MC >= 12103
//$$ import java.util.Iterator;
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.world.item.Item;
//#endif

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public final class UncraftingTable {

    private static boolean filling;

    private UncraftingTable() {
    }

    public static List<?> candidates(Level level, ItemStack product) {
        if (product.isEmpty()) {
            return List.of();
        }

        RecipeManager manager = level.getServer() == null ? null : level.getServer().getRecipeManager();

        if (manager == null) {
            return List.of();
        }

        List<Object> found = new ArrayList<>();

        for (Object holder : manager.getRecipes()) {
            CraftingRecipe recipe = recipeOf(holder);

            if (recipe == null || recipe.isSpecial()) {
                continue;
            }

            ItemStack output = outputOf(level, recipe);

            if (output.isEmpty() || output.getItem() != product.getItem()) {
                continue;
            }

            ItemStack[] base = decompose(recipe);

            if (base == null) {
                continue;
            }

            int applications = product.getCount() / Math.max(1, output.getCount());
            boolean reachable = applications > 0;

            for (int i = 0; reachable && i < base.length; i++) {
                if (!base[i].isEmpty() && applications > base[i].getMaxStackSize()) {
                    reachable = false;
                }
            }

            if (reachable) {
                found.add(holder);
            }
        }

        return found;
    }

    @Nullable
    public static CraftingRecipe recipeOf(@Nullable Object holder) {
        if (holder == null) {
            return null;
        }

        //#if MC >= 12002
        return holder instanceof RecipeHolder<?> recipeHolder && recipeHolder.value() instanceof CraftingRecipe recipe ? recipe : null;
        //#else
        //$$ return holder instanceof CraftingRecipe recipe ? recipe : null;
        //#endif
    }

    private static ItemStack outputOf(Level level, CraftingRecipe recipe) {
        //#if MC >= 26.1
        //$$ ItemStack[] grid = decompose(recipe);

        //$$ if (grid == null) {
        //$$     return ItemStack.EMPTY;
        //$$ }

        //$$ return recipe.assemble(CraftingInput.of(3, 3, Arrays.asList(grid)));
        //#elseif MC >= 12103
        //$$ ItemStack[] grid = decompose(recipe);

        //$$ if (grid == null) {
        //$$     return ItemStack.EMPTY;
        //$$ }

        //$$ return recipe.assemble(CraftingInput.of(3, 3, Arrays.asList(grid)), level.registryAccess());
        //#else
        return recipe.getResultItem(level.registryAccess());
        //#endif
    }

    @Nullable
    public static ItemStack[] decompose(CraftingRecipe recipe) {
        int width = 3;
        int height = 3;
        List<ItemStack> items = new ArrayList<>();

        //#if MC >= 12103
        //$$ if (recipe instanceof ShapedRecipe shaped) {
        //$$     for (Optional<Ingredient> optional : shaped.getIngredients()) {
        //$$         items.add(optional.map(UncraftingTable::representative).orElse(ItemStack.EMPTY));
        //$$     }
        //$$
        //$$     width = shaped.getWidth();
        //$$     height = shaped.getHeight();
        //$$ } else if (recipe instanceof ShapelessRecipe shapeless) {
        //$$     for (Ingredient ingredient : ((ShapelessRecipeAccessor) shapeless).igny$ingredients()) {
        //$$         items.add(representative(ingredient));
        //$$     }
        //$$ } else {
        //$$     return null;
        //$$ }
        //#else
        if (recipe instanceof ShapedRecipe shaped) {
            for (Ingredient ingredient : shaped.getIngredients()) {
                items.add(representative(ingredient));
            }

            width = shaped.getWidth();
            height = shaped.getHeight();
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            for (Ingredient ingredient : shapeless.getIngredients()) {
                items.add(representative(ingredient));
            }
        } else {
            return null;
        }
        //#endif

        if (width <= 0 || height <= 0) {
            return null;
        }

        ItemStack[] grid = new ItemStack[9];
        Arrays.fill(grid, ItemStack.EMPTY);

        boolean isShaped = recipe instanceof ShapedRecipe;
        int offsetX = isShaped ? Math.max(0, (3 - width) / 2) : 0;
        int offsetY = isShaped ? Math.max(0, (3 - height) / 2) : 0;

        for (int i = 0; i < items.size() && i < width * height; i++) {
            ItemStack material = items.get(i);

            if (material.isEmpty()) {
                continue;
            }

            int x = offsetX + i % width;
            int y = offsetY + i / width;
            int slot = x + y * 3;

            if (slot >= 0 && slot < 9) {
                grid[slot] = single(material);
            }
        }

        return grid;
    }

    private static ItemStack representative(@Nullable Ingredient ingredient) {
        if (ingredient == null) {
            return ItemStack.EMPTY;
        }

        //#if MC >= 12103
        //$$ Iterator<Holder<Item>> iterator = ingredient.items().iterator();
        //$$ return iterator.hasNext() ? single(new ItemStack(iterator.next().value())) : ItemStack.EMPTY;
        //#else
        ItemStack[] stacks = ingredient.getItems();
        return stacks.length == 0 ? ItemStack.EMPTY : single(stacks[0]);
        //#endif
    }

    public static ItemStack single(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    public static boolean decomposable(ItemStack stack) {
        return !stack.isEmpty() && !stack.isEnchanted() && !stack.isDamaged();
    }

    @Nullable
    public static ItemStack[] gridOf(@Nullable Object holder) {
        CraftingRecipe recipe = recipeOf(holder);
        return recipe == null ? null : decompose(recipe);
    }

    public static int outputCount(Level level, @Nullable Object holder) {
        CraftingRecipe recipe = recipeOf(holder);
        return recipe == null ? 1 : Math.max(1, outputOf(level, recipe).getCount());
    }

    public static void writeGrid(AbstractContainerMenu menu, @Nullable ItemStack[] grid) {
        CraftingContainer container = gridContainer(menu);

        if (container == null) {
            return;
        }

        filling = true;

        try {
            for (int i = 0; i < container.getContainerSize(); i++) {
                container.setItem(i, grid != null && i < grid.length && grid[i] != null ? grid[i] : ItemStack.EMPTY);
            }
        } finally {
            filling = false;
        }
    }

    public static boolean writeGrid(AbstractContainerMenu menu, @Nullable ItemStack[] base, int factor) {
        CraftingContainer container = gridContainer(menu);

        if (container == null) {
            return false;
        }

        filling = true;

        try {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack material = base != null && i < base.length ? base[i] : ItemStack.EMPTY;

                if (material.isEmpty()) {
                    container.setItem(i, ItemStack.EMPTY);
                    continue;
                }

                if (factor <= 0 || factor > material.getMaxStackSize()) {
                    for (int j = 0; j < container.getContainerSize(); j++) {
                        container.setItem(j, ItemStack.EMPTY);
                    }

                    return false;
                }

                ItemStack scaled = material.copy();
                scaled.setCount(factor);
                container.setItem(i, scaled);
            }
        } finally {
            filling = false;
        }

        return true;
    }

    public static void readGrid(AbstractContainerMenu menu, int @Nullable [] target) {
        if (target == null) {
            return;
        }

        CraftingContainer container = gridContainer(menu);

        for (int i = 0; i < target.length; i++) {
            target[i] = container != null && i < container.getContainerSize() ? container.getItem(i).getCount() : 0;
        }
    }

    public static void clearGrid(AbstractContainerMenu menu) {
        writeGrid(menu, null);
    }

    public static boolean gridEmpty(@Nullable CraftingContainer container) {
        if (container == null) {
            return true;
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isFilling() {
        return filling;
    }

    @Nullable
    public static CraftingContainer gridContainer(AbstractContainerMenu menu) {
        if (menu.slots.size() < 10) {
            return null;
        }

        Slot slot = menu.slots.get(1);

        if (slot.container instanceof CraftingContainer container && container.getContainerSize() == 9) {
            return container;
        }

        return null;
    }

    public static boolean isUncraftable(AbstractContainerMenu menu) {
        return gridContainer(menu) != null;
    }
}
