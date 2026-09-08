package zone.moddev.mc.advantageworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

final class WorksResultWriter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File resultDirectory;

    WorksResultWriter() {
        String requested = System.getProperty("advantageWorks.resultsDir", "advantage-works-results");
        resultDirectory = new File(requested);
        if (!resultDirectory.isDirectory() && !resultDirectory.mkdirs()) {
            throw new IllegalStateException("Cannot create Advantage Works result directory " + resultDirectory);
        }
    }

    File write(String stationId, String kind, Map<String, Object> payload) {
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("format", 1);
        document.put("recordedAtUtc", utcNow());
        document.put("minecraft", "1.10.2");
        document.put("forge", net.minecraftforge.common.ForgeVersion.getVersion());
        document.put("mods", loadedMods());
        document.put("station", stationId);
        document.put("kind", kind);
        document.putAll(payload);

        File file = new File(resultDirectory, safe(stationId) + "-" + safe(kind) + ".json");
        File temporary = new File(resultDirectory, file.getName() + ".tmp");
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(temporary), StandardCharsets.UTF_8)) {
            GSON.toJson(document, writer);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot write Advantage Works result " + file, ex);
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Cannot replace Advantage Works result " + file);
        }
        if (!temporary.renameTo(file)) {
            throw new IllegalStateException("Cannot finalize Advantage Works result " + file);
        }
        return file;
    }

    private static Map<String, String> loadedMods() {
        Map<String, String> result = new LinkedHashMap<>();
        List<ModContainer> mods = Loader.instance().getActiveModList();
        for (ModContainer mod : mods) {
            result.put(mod.getModId(), mod.getVersion());
        }
        return result;
    }

    private static String utcNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    private static String safe(String value) {
        return value.replaceAll("[^A-Za-z0-9_.-]", "_");
    }
}
