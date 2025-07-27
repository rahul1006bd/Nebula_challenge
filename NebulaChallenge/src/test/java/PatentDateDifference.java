import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PatentDateDifference {

    public static void main(String[] args) {
        WebDriver driver = null;
        try {
            // Check for command-line argument
            if (args.length == 0) {
                System.out.println("Please provide a search keyword as a command-line argument.");
                return;
            }
            String searchKey = args[0];

            // Setup Chrome with Incognito mode
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();

            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Navigate to site
            driver.get("https://patinformed.wipo.int/");

            // Locate and fill the search box
            WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[contains(@placeholder,'Search pharmaceutical patents')]")));
            searchField.sendKeys(searchKey);


            // Handle dynamic popup
            try {
                WebElement consentButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[text()='I have read and agree to the terms']")));
                consentButton.click();
                System.out.println("Popup handled.");
            } catch (TimeoutException e) {
                System.out.println("Popup not found.");
            }



            // Click search icon
            WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@class='searchField']/following::button[1]")));
            searchIcon.click();

            // Click first result
            WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//tr/th[text()='PATENTS']/ancestor::table/tbody/tr/td[3]/ul/li[1])[1]")));
            firstResult.click();

            // Extract raw date text
            String pDate = getTextSafe(driver, wait, "(//b[text()='Publication date']/parent::td/following::td)[1]");
            String gDate = getTextSafe(driver, wait, "(//b[text()='Grant date']/parent::td/following::td)[1]");
            String fDate = getTextSafe(driver, wait, "(//b[text()='Filing date']/parent::td/following::td)[1]");

            System.out.println("Raw Dates:");
            System.out.println("Publication Date: " + pDate);
            System.out.println("Grant Date: " + gDate);
            System.out.println("Filing Date: " + fDate);

            // Clean the date
            String pubDateStr = cleanDateString(pDate);
            String grantDateStr = cleanDateString(gDate);
            String filingDateStr = cleanDateString(fDate);

            // Format and parse
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate pubDate = LocalDate.parse(pubDateStr, formatter);
            LocalDate grantDate = LocalDate.parse(grantDateStr, formatter);
            LocalDate filingDate = LocalDate.parse(filingDateStr, formatter);

            // Calculate differences
            long pubToGrant = ChronoUnit.DAYS.between(pubDate, grantDate);
            long pubToFiling = ChronoUnit.DAYS.between(pubDate, filingDate);
            long grantToFiling = ChronoUnit.DAYS.between(grantDate, filingDate);

            // Output differences
            System.out.println("\nDate Differences:");
            System.out.println("Difference between Publication and Grant date: " + Math.abs(pubToGrant) + " days.");
            System.out.println("Difference between Publication and Filing date: " + Math.abs(pubToFiling) + " days.");
            System.out.println("Difference between Grant and Filing date: " + Math.abs(grantToFiling) + " days.");

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Extracts text using XPath.
     */
    private static String getTextSafe(WebDriver driver, WebDriverWait wait, String xpath) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            return element.getText();
        } catch (TimeoutException e) {
            System.err.println("Element not found for XPath: " + xpath);
            return "";
        }
    }

    /**
     * Cleans a date
     */
    private static String cleanDateString(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        return raw.split("\\s+|\\(")[0].trim();  // splits on whitespace or '('
    }
}
