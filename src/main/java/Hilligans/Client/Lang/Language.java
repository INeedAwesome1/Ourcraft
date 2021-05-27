package Hilligans.Client.Lang;

import Hilligans.Client.Rendering.World.Managers.ShaderManager;
import Hilligans.ModHandler.ModLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Language {

    HashMap<String,String> translations = new HashMap<>();

    public Language(String name) {
        parseLines(read("/Languages/" + name));
    }

    private void parseLines(String[] lines) {
        if(lines != null){
            for (String string : lines) {
                if(!string.startsWith("#")) {
                    String[] strings = string.split("::",2);
                    if (strings.length == 2) {
                        translations.put(strings[0], strings[1]);
                    }
                }
            }
        }
    }

    public String getTranslated(String key) {
        return translations.getOrDefault(key,key);
    }

    public static String[] read(String source) {
        InputStream stream = Language.class.getResourceAsStream(source);
        if(stream == null) {
            System.out.println("Null");
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().toArray(String[]::new);
    }

}
