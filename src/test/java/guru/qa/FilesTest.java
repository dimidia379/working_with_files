package guru.qa;

import com.codeborne.pdftest.PDF;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class FilesTest {

    @Test
    @DisplayName("Загрузка файла по относительному пути")
    void filenameShouldDisplayedAfterUploadActionFromClasspathTest() {
        open("https://dropmefiles.com/");
        $("#upload_container").$("input").uploadFromClasspath("test_upload.txt");
        $(".expand").click();
        $(".files").$("li").shouldHave(text("test_upload.txt"));
    }

    @Test
    @DisplayName("Скачивание текстового файла")
    void TextFileDownloadTest() throws IOException {
        open("https://github.com/dimidia379/network/blob/master/README.md");
        File download = $("#raw-url").download();
        String fileContent = IOUtils.toString(new FileReader(download));
        assertTrue(fileContent.contains("This is a training project"));
    }

    @Test
    @DisplayName("Скачивание PDF файла")
    void pdfFileDownloadTest() throws IOException {
        open("https://duoasia.ru/");
        File pdf = $("a[title='Меню']").download();
        PDF parsedPdf = new PDF(pdf);
        assertThat(parsedPdf).containsText("Севиче из лосося");
    }

    @Test
    @DisplayName("Скачивание XLSX файла")
    void xlsFileDownloadTest() throws IOException {
        open("http://school320.ru");
        File file = $(byText("Уроки 1-4 классы")).download();
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        boolean checkPassed = wb
                .getSheetAt(0)
                .getRow(9)
                .getCell(2)
                .getStringCellValue()
                .contains("Математика");
        assertTrue(checkPassed);
    }

    @Test
    @DisplayName("Парсинг CSV файлов")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream ist = classLoader.getResourceAsStream("test_parse.csv");
             Reader reader = new InputStreamReader(ist)) {
            CSVReader csvReader = new CSVReader(reader);

            List<String[]> strings = csvReader.readAll();
            assertEquals("[что, где, когда]", Arrays.toString(strings.get(0)));
            assertEquals(4, strings.size());
        }
    }

    @Test
    @DisplayName("Парсинг ZIP файлов")
    void parseZipFileTest() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream ist = classLoader.getResourceAsStream("test_upload.zip");
             ZipInputStream zis = new ZipInputStream(ist)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Assertions.assertEquals("test_upload.txt", entry.getName());
            }
        }
    }
}


