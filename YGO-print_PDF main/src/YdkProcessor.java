import java.io.*;
import java.nio.file.*;
import java.util.*;

public class YdkProcessor {
    public static void YdkProcess() {

        String deckInputFolderPath = "deck-input";

        try {
            String tempDeckFolderPath = "temp-deck";
            processDeckFiles(deckInputFolderPath, tempDeckFolderPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processDeckFiles(String deckInputFolderPath, String tempDeckFolderPath) throws IOException {
        File deckInputFolder = new File(deckInputFolderPath);
        File[] deckFiles = deckInputFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ydk"));

        if (deckFiles == null || deckFiles.length == 0) {
            System.out.println("No .ydk files found in the deck-input folder.");
            return;
        }

        // 使用Files类创建temp-deck文件夹（如果不存在）
        Files.createDirectories(Paths.get(tempDeckFolderPath));

        for (File deckFile : deckFiles) {
            String deckFileName = deckFile.getName();
            String subfolderName = deckFileName.substring(0, deckFileName.lastIndexOf(".ydk"));
            String subfolderPath = tempDeckFolderPath + File.separator + subfolderName;

            // 使用Files类创建对应的子文件夹（如果不存在）
            Files.createDirectories(Paths.get(subfolderPath));

            Map<String, Integer> fileCounter = new HashMap<>(); // 用于记录重复文件名的计数器

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(deckFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        int cardNumber = Integer.parseInt(line.trim()); // 将每一行解析为数字
                        String cardFileName = cardNumber + ".jpg"; // 获取对应的.jpg文件名
                        String cardFilePath = "card/" + cardFileName; // card文件夹中.jpg文件的路径

                        // 生成唯一的新文件名
                        String newFileName = generateUniqueFileName(subfolderPath, cardFileName, fileCounter);

                        // 使用Files类将.jpg文件复制到子文件夹中
                        Files.copy(Paths.get(cardFilePath), Paths.get(subfolderPath, newFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (NumberFormatException | IOException e) {
                        // 如果解析数字失败或复制文件时出现异常，跳过该行继续处理下一行
                        continue;
                    }
                }
            }
        }
    }

    // 生成唯一的新文件名，以处理文件名冲突
    private static String generateUniqueFileName(String subfolderPath, String fileName,
                                                 Map<String, Integer> fileCounter) {
        String baseName = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        int counter = fileCounter.getOrDefault(baseName, 0) + 1;
        fileCounter.put(baseName, counter);

        if (counter > 1) {
            return baseName + "-" + counter + "." + extension;
        } else {
            return fileName;
        }
    }
}
