package net.cpprograms.minecraft.General.uuidconverter;

import net.cpprograms.minecraft.General.PluginBase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
 * @author Max Lee aka Phoenix616 (mail@moep.tv)
 */

public class UuidConverter {

    private final PluginBase plugin;
    private static final String MOJANG_API = "https://api.mojang.com/profiles/minecraft";

    private List<Source> sources = new ArrayList<>();

    public UuidConverter(PluginBase plugin) {
        this.plugin = plugin;

        if (plugin.getServer().getPluginManager().isPluginEnabled("UUIDDB")) {
            sources.add(new UuidbSource(plugin));
        }
        if (plugin.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            sources.add(new EssentialsSource(plugin));
        }
    }

    public Map<String, UUID> getUuidMap(Collection<String> usernames) {
        plugin.logInfo("Converting " + usernames.size() + " usernames to UUIDs...");
        Map<String, UUID> map = new HashMap<>();
        List<String> manualConvert = new ArrayList<>();

        if (sources.isEmpty()) {
            manualConvert.addAll(usernames);
        } else {
            int i = 0;
            for (String username : usernames) {
                i++;
                if (i % 100 == 0) {
                    plugin.logInfo(i + "...");
                }
                UUID uuid = null;
                for (Source source : sources) {
                    uuid = source.getUUID(username);
                    if (uuid != null) {
                        break;
                    }
                }
                if (uuid != null) {
                    map.put(username, uuid);
                } else {
                    manualConvert.add(username);
                }
            }
        }

        if (!manualConvert.isEmpty()) {
            JSONParser jsonParser = new JSONParser();
            plugin.logInfo("Only " + map.size() + " of " + usernames.size() + " found. Contacting Mojang for the rest (" + manualConvert.size() + ")...");
            for (int i = 0; i < Math.ceil(manualConvert.size() / 100.0); i++) {
                try {
                    if (i > 0) {
                        Thread.sleep(100L);
                        plugin.logInfo(i * 100 + "...");
                    }
                    HttpURLConnection connection = createConnection();
                    String body = JSONArray.toJSONString(manualConvert.subList(i * 100, Math.min(manualConvert.size(), (i + 1) * 100)));
                    writeBody(connection, body);
                    JSONArray profiles = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                    for (Object profile : profiles) {
                        JSONObject jsonProfile = (JSONObject) profile;
                        try {
                            map.put((String) jsonProfile.get("name"), parseUuid((String) jsonProfile.get("id")));
                        } catch (IllegalArgumentException e) {
                            plugin.logWarning("Invalid UUID (" + jsonProfile.get("id") + ") for " + jsonProfile.get("name"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        plugin.logInfo("Converted " + map.size() + "/" + usernames.size() + " usernames to UUIDs!");
        return map;
    }

    private void writeBody(HttpURLConnection connection, String body) throws Exception {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private HttpURLConnection createConnection() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(MOJANG_API).openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private UUID parseUuid(String id) {
        return UUID.fromString(id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public interface Source {
        UUID getUUID(String name);
    }
}
