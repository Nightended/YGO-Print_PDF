import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoPrint {

    private static final int IMAGES_PER_PAGE = 9;
    private static final float IMAGE_WIDTH_CM = 5.9f;
    private static final float IMAGE_HEIGHT_CM = 8.6f;
    private static final float CM_TO_POINTS = 28.35f; // Conversion factor from cm to points
    private static final float IMAGE_WIDTH = IMAGE_WIDTH_CM * CM_TO_POINTS;
    private static final float IMAGE_HEIGHT = IMAGE_HEIGHT_CM * CM_TO_POINTS;
    private static final float PAGE_WIDTH = 21.0f * CM_TO_POINTS; // A4 paper width in points
    private static final float PAGE_HEIGHT = 29.7f * CM_TO_POINTS; // A4 paper height in points

    public static void main(String[] args) {
        clearFolder("temp-deck");
        YdkProcessor.YdkProcess();
        String inputFolderPath = "temp-deck";
        String outputFolderPath = "deck-output";

        File inputFolder = new File(inputFolderPath);
        File[] subfolders = inputFolder.listFiles(File::isDirectory);
        if (subfolders == null || subfolders.length == 0) {
            System.err.println("错误：在输入文件夹中找不到子文件夹。");
            return;
        }

        for (File subfolder : subfolders) {
            File[] imageFiles = subfolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
            if (imageFiles == null || imageFiles.length == 0) {
                System.err.println("错误：在子文件夹 " + subfolder.getName() + " 中找不到图片文件。");
                continue;
            }

            try {
                String outputPdfPath = outputFolderPath + "/" + subfolder.getName() + ".pdf";
                createPdf(imageFiles, outputPdfPath);
                System.out.println("PDF 文件已保存：" + outputPdfPath);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("按回车键继续...");
        scanner.nextLine();
    }

    private static void createPdf(File[] imageFiles, String outputPdfPath) throws IOException, DocumentException {
        // 创建 PDF 文件
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPdfPath));
        document.open();

        // 计算每张图片之间的间隔和边距
        float spacingX = (PAGE_WIDTH - 3 * IMAGE_WIDTH) / 4;
        float spacingY = (PAGE_HEIGHT - 3 * IMAGE_HEIGHT) / 4;

        PdfContentByte canvas = writer.getDirectContent();

        // 将图片添加到 PDF 文件中
        for (int i = 0; i < imageFiles.length; i++) {
            if (i % IMAGES_PER_PAGE == 0) {
                // 创建新的页面
                document.newPage();
            }

            // 计算图片在当前页面的位置
            int row = i / 3 % 3;
            int col = i % 3;
            float x = spacingX + col * (IMAGE_WIDTH + spacingX);
            float y = PAGE_HEIGHT - (spacingY + (row + 1) * IMAGE_HEIGHT + row * spacingY);

            String imagePath = imageFiles[i].getPath();
            Image image = Image.getInstance(imagePath);
            image.scaleToFit(IMAGE_WIDTH, IMAGE_HEIGHT);
            image.setAbsolutePosition(x, y);
            canvas.addImage(image);
        }

        document.close();
    }
    public static void clearFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        clearFolder(file.getAbsolutePath());
                    }
                    file.delete();
                }
            }
        }
    }
}
