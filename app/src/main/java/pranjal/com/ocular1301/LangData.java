package pranjal.com.ocular1301;


import java.util.List;
import java.util.Map;

public class LangData {
    private static List<String> dirs;
    private static Map<String, String> langs;

    public static List<String> getDirs() {
        return dirs;
    }

    public static void setDirs(List<String> dirs) {
        LangData.dirs = dirs;
    }

    public static Map<String, String> getLangs() {
        return langs;
    }

    public static void setLangs(Map<String, String> langs) {
        LangData.langs = langs;
    }
}
