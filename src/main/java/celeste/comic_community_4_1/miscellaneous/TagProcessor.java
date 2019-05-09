package celeste.comic_community_4_1.miscellaneous;

public class TagProcessor {

    public static final int MAX_TAG_PER_SERIES = 5;
    public static final int MAX_TAG_LENGTH = 10;

    public static boolean validTag(String tag) {
        return !tag.trim().isEmpty();
    }

    public static String process(String tag) {
        String[] words = tag.split(" ");
        StringBuilder sb = new StringBuilder();
        String temp;
        for (int i = 0; i < words.length; i++) {
            temp = words[i].trim();
            if (temp.isEmpty()) continue;
            sb.append(temp.substring(0, 1).toUpperCase());
            sb.append(temp.substring(1).toLowerCase());
        }
        return sb.toString();
    }

}
