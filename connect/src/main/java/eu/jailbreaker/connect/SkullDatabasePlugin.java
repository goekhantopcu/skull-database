package eu.jailbreaker.connect;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Author("JailBreaker")
@Website("https://griefergames.net")
@ApiVersion(ApiVersion.Target.v1_15)
@Plugin(name = "SkullKlauer", version = "0.0.1")
public final class SkullDatabasePlugin extends JavaPlugin implements Listener {
    private static final Set<String> CATEGORIES = new HashSet<String>() {{
        add("alphabet");
        add("animals");
        add("blocks");
        add("decoration");
        add("food-drinks");
        add("humans");
        add("humanoid");
        add("miscellaneous");
        add("monsters");
        add("plants");
    }};
    private final AtomicBoolean download = new AtomicBoolean(false);

    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.NETHER_STAR) {
                if (this.download.get()) {
                    player.sendMessage("§8[§6SkullKlauer§8] §cDownloade alle Köpfe von minecraft-heads.com");
                    return;
                }
                CompletableFuture.runAsync(() -> {
                    try {
                        this.download.set(true);
                        final Map<String, Set<SkullEntry>> categories = Maps.newHashMap();
                        CATEGORIES.forEach(category -> {
                            try {
                                categories.put(category.toLowerCase(), SkullDownloader.download(category));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                                player.sendMessage("§8[§6SkullKlauer§8] §cEin Fehler ist aufgetreten [" + category + "]");
                            }
                        });
                        final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
                        final String json = gson.toJson(categories);
                        final String fileName = "skulls_" + System.currentTimeMillis() + ".json";
                        final Path path = Paths.get(this.getDataFolder().toString(), fileName);
                        if (Files.notExists(path)) {
                            Files.createFile(path);
                        }
                        final FileWriter writer = new FileWriter(path.toFile());
                        writer.write(StringEscapeUtils.unescapeJson(StringEscapeUtils.unescapeJava(json)));
                        writer.flush();
                        player.sendMessage("§8[§6SkullKlauer§8] §7Geklaut und gespeichert in §e" + fileName);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        player.sendMessage("§8[§6SkullKlauer§8] §cEin Fehler ist aufgetreten ya salame");
                    } finally {
                        this.download.set(false);
                    }
                });
            }
        }
    }
}
