import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class Checker {
    public static void main(String[] args) throws Exception {
        String path = "app/src/main/java/com/davidkazad/chantlouange/datas/CC.java";
        String content = new String(Files.readAllBytes(Paths.get(path)));
        
        Pattern p = Pattern.compile("pg\\.add\\(new Page\\(\\s*(\\d+)\\s*,\\s*\"(.*?)\"\\s*,\\s*\"(.*?)\"");
        Matcher m = p.matcher(content);
        
        int expectedNum = 1;
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            String numStr = m.group(2).replaceAll("[^0-9]", ""); // extract just the digits
            if (!numStr.isEmpty()) {
                int num = Integer.parseInt(numStr);
                if (num > expectedNum) {
                    System.out.println("Missing number before " + m.group(2) + " (Title: " + m.group(3) + ")");
                    expectedNum = num;
                }
                if (num == expectedNum) {
                    // stays same (e.g. 1A, 1B), or increments
                    // Actually, if num == expectedNum it's fine.
                }
                if (num > expectedNum) {
                    expectedNum = num;
                }
                while (expectedNum < num) {
                    expectedNum++;
                }
                if(num == expectedNum) {
                   expectedNum++;
                }
            }
        }
        System.out.println("Check done.");
    }
}
