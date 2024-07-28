package github.andredimaz.sentinel.autoclick.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class materialUtils {

    private static final Map<Integer, Material> idToMaterialMap = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            idToMaterialMap.put(material.getId(), material);
        }
    }

    public static ItemStack parseMaterial(String materialString) {
        if (materialString.length() == 64 && !materialString.contains(":")) {
            return createCustomSkull(materialString);
        }

        String[] parts = materialString.split(":");
        Material material = null;
        int data = 0;

        try {
            int id = Integer.parseInt(parts[0]);
            material = idToMaterialMap.get(id);
        } catch (NumberFormatException e) {
            material = Material.matchMaterial(parts[0]);
        }

        if (material == null) {
            throw new IllegalArgumentException("Material inválido: " + parts[0]);
        }

        if (parts.length > 1) {
            try {
                data = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Data inválida: " + parts[1]);
            }
        }

        return new ItemStack(material, 1, (short) data);
    }

    private static ItemStack createCustomSkull(String textureHash) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (textureHash.isEmpty()) return skull;

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        String url = "http://textures.minecraft.net/texture/" + textureHash;
        String base64 = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes());
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }
}
