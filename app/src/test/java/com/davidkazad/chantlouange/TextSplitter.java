//package com.davidkazad.chantlouange;//package com.davidkazad.chantlouange;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;


/**
 *
 */
public class TextSplitter {
    public static void main(String[] args) throws IOException {
        // Read songs.txt from same folder
        String text = new String(Files.readAllBytes(Paths.get("ob_songs.txt")), "UTF-8");

        // Regex to split sections starting with "#number Title"
        Pattern pattern = Pattern.compile("(?m)^#(\\d+)\\s+(.*?)\\n([\\s\\S]*?)(?=^#\\d+\\s+|\\Z)");
        Matcher matcher = pattern.matcher(text);

        List<Section> sections = new ArrayList<>();

        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            String title = matcher.group(2).trim();
            String content = matcher.group(3).trim();
            sections.add(new Section(number, title, content));
        }

        // Write results to songs.json
        try (PrintWriter out = new PrintWriter("songs.json")) {
            out.println("[");
            for (int i = 0; i < sections.size(); i++) {
                Section s = sections.get(i);
                out.printf("  {\"number\": \"%d\", \"title\": \"%s\", \"content\": \"%s\"}%s\n",
                        s.number,
                        s.title.replace("\"", "\\\""),
                        s.content.replace("\"", "\\\"").replace("\n", "\\n"),
                        (i < sections.size() - 1 ? "," : "")
                );
            }
            out.println("]");
        }

        // Write results to ob_overwrite.java
        try (PrintWriter out = new PrintWriter("ob_overwrite.java")) {
            out.println("");
            for (int i = 0; i < sections.size(); i++) {
                Section s = sections.get(i);

                out.printf(" pg.add(new Page(%d, \"%d.\", \"%s\", \"%s\", 9%s));\n",
                        //pg.add(new Page(2, "2. ","AMAZING GRACE (Ab)", "",7));
                //out.printf("  {\"number\": \"%d\", \"title\": \"%s\", \"content\": \"%s\"}%s\n",
                        s.number,
                        s.number,
                        s.title.replace("\"", "\\\""),
                        s.content.replace("\"", "\\\"").replace("\n", "\\n"),
                        (i < sections.size() - 1 ? " " : "")
                );
            }
            out.println("");
        }


        System.out.println("✅ Done. Exported to songs.json");
    }


}
class Section {
    int number;
    String title;
    String content;

    Section(int number, String title, String content) {
        this.number = number;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "#" + number + " - " + title + "\n" + content + "\n";
    }
}