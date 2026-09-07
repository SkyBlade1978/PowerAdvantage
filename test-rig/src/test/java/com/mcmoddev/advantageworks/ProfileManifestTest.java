package com.mcmoddev.advantageworks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProfileManifestTest {
    @Test
    public void profilesPinEverySuppliedArtifactAndCoverRecipeModes() throws Exception {
        File directory = new File("profiles");
        File[] profiles = directory.listFiles((dir, name) -> name.endsWith(".json"));
        assertNotNull(profiles);
        assertEquals(7, profiles.length);
        Set<String> recipeModes = new HashSet<>();
        Set<String> profileNames = new HashSet<>();

        for (File file : profiles) {
            JsonObject profile;
            try (FileReader reader = new FileReader(file)) {
                profile = new JsonParser().parse(reader).getAsJsonObject();
            }
            assertTrue(file.getName(), profileNames.add(text(profile, "name")));
            assertEquals("6958ed2dfdd745ebc1f614a32072765af061fe8e", text(profile, "sourceCommit"));
            assertEquals("2.4.1.110021", text(profile, "sourceVersion"));
            recipeModes.add(text(profile, "recipeMode"));

            if ("packaged".equals(text(profile, "mode"))) {
                JsonObject forge = profile.getAsJsonObject("forge");
                assertNotNull(file.getName(), forge);
                assertEquals(64, text(forge, "sha256").length());
            }

            JsonArray mods = profile.getAsJsonArray("mods");
            assertNotNull(mods);
            for (JsonElement element : mods) {
                JsonObject mod = element.getAsJsonObject();
                assertFalse(file.getName(), text(mod, "id").isEmpty());
                assertFalse(file.getName(), text(mod, "file").isEmpty());
                assertFalse(file.getName(), text(mod, "version").isEmpty());
                String path = text(mod, "path");
                assertFalse("Hard-coded drive path in " + file, path.matches("^[A-Za-z]:.*"));
                boolean workspaceProduced = mod.has("workspaceProduced")
                        && mod.get("workspaceProduced").getAsBoolean();
                if (!workspaceProduced) assertEquals(file.getName(), 64, text(mod, "sha256").length());
            }
        }

        assertTrue(recipeModes.contains("NORMAL"));
        assertTrue(recipeModes.contains("TECH_PROGRESSION"));
        assertTrue(recipeModes.contains("APOCALYPTIC"));
    }

    private static String text(JsonObject object, String name) {
        return object.has(name) ? object.get(name).getAsString() : "";
    }
}
