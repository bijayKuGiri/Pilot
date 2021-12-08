import com.opencsv.CSVWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestAccessibility {

    private static ArrayList<String> urlList=new ArrayList<>();
    private static ArrayList<String> timeList=new ArrayList<>();

    public static void main(String[] args) {

        var driver=navigateToApp();
        try {
            //driver.navigate().to("https://unileveraemcs:Un1l2v2r@A3mcs@magnumicecream-com-uat-aemcs.unileversolutions.com/br/home.html");
            driver.navigate().to("https://www.magnumicecream.com/br/home.html");
            System.out.println("Navigate to the url sucessfully");
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
            handleCookie(driver);
            selectCountry(driver);
            writeDataLineByLine("result.csv",getCsvData(),urlList,timeList);
        }
        catch (Exception ex){
            System.out.println("Error due to"+ex.getMessage());
        }
        finally {
            driver.quit();
            System.out.println("Quiting the Browser");
        }
    }

    private static WebDriver navigateToApp(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT_AND_NOTIFY);
        options.addArguments("enable-automation");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");
        options.addArguments("--dns-prefetch-disable");
        options.addArguments("--disable-gpu");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        var _driver = new ChromeDriver(options);
        _driver.manage().deleteAllCookies();

        _driver.manage().window().maximize();
        return _driver;
    }

    public static void handleCookie(WebDriver _driver) {
        if (_driver.findElements(By.cssSelector("div#onetrust-button-group-parent>div>button#onetrust-accept-btn-handler")).size() == 0 |
                !_driver.findElement(By.cssSelector("div#onetrust-button-group-parent>div>button#onetrust-accept-btn-handler")).isDisplayed())
            return;
        WebElement webElement = _driver.findElement(By.cssSelector("div#onetrust-button-group-parent>div>button#onetrust-accept-btn-handler"));
        clickItem(_driver,webElement);
        while (_driver.findElement(By.cssSelector("div#onetrust-button-group-parent")).isDisplayed()) {
        }

    }
    public static void clickItem(WebDriver driver, WebElement element){
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOf(element));
        Actions action = new Actions(driver);
        action.moveToElement(element).click().perform();
    }

    public static WebDriver selectCountry(WebDriver driver) {

        clickItem(driver,driver.findElement(By.xpath("//a[contains(@href,'country')]//span")));
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        List<WebElement> lstElements = driver.findElements(By.xpath("//a[@class='cmp-languagenavigation__item-link']"));
        for (var item:lstElements) {
            long start = System.currentTimeMillis();
            clickItem(driver,item);
            var wait = new WebDriverWait(driver,60);
            wait.until(wd -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));
            long finish = System.currentTimeMillis();
            double totalTime =0.01*( finish - start);
            ArrayList<String> tabs2 = new ArrayList<>(driver.getWindowHandles());
            if (tabs2.size() > 1) {
                driver.switchTo().window(tabs2.get(1));
                System.out.println(driver.getCurrentUrl());
                urlList.add(driver.getCurrentUrl());
                timeList.add(String.valueOf(totalTime));
                System.out.println("Loading time in second ->"+totalTime+"\n");
                driver.close();
                driver.switchTo().window(tabs2.get(0));
            }

        }
        return driver;
    }

    public static void writeDataLineByLine(String filePath,ArrayList<String> ActualLst,ArrayList<String> lst1, ArrayList<String> lst2) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        try {
            FileWriter outputfile = new FileWriter(file);

            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = { "Expected", "Actual", "Time" };
            writer.writeNext(header);

            for(int i=0;i< lst1.size();i++) {
                // add data to csv
                String[] data1 = {ActualLst.get(i), lst1.get(i), lst2.get(i)};
                writer.writeNext(data1);
            }
//            String[] data2 = { "Suraj", "10", "630" };
//            writer.writeNext(data2);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

   public static ArrayList<String> getCsvData() throws IOException {
       BufferedReader br = new BufferedReader(new FileReader("src/test/url.csv"));
       ArrayList<String> expectedData=new ArrayList<>();
       String line;
       while ((line = br.readLine()) != null) {
           expectedData.add(line);
       }
       return expectedData;
   }
}
