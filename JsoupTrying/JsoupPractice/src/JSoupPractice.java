//import Selenium
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.filechooser.FileSystemView;

//import JSoup
import java.io.File;  
import java.io.IOException;  
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;  
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class JSoupPractice {

    //--------------------------------------------------------------COMPARING------------------------------------------------------------------------------------------
    private String username;
    private String review;
    private String date;

    public JSoupPractice(String username, String review, String date) {
        this.username = username;
        this.review = review;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getReview() {
        return review;
    }

    public String getDate() {
        return date;
    }


     //--------------------------------------------------------------COMPARING------------------------------------------------------------------------------------------

      //--------------------------------------------------------------LOADING------------------------------------------------------------------------------------------

    public static String loadWebPage(String input_url) {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(10000));

        driver.get(input_url);
        System.out.println("Done with get");
        ((JavascriptExecutor)driver).executeScript("scrollTo(0,30000)");
        System.out.println("Done with scroll");

        for(int i=0; i < 5; i++) {
            System.out.println(i);
            WebElement button = driver.findElement(By.xpath("/html/body/div[1]/div[2]/main/div/div[4]/div[3]/div[9]/button"));
 
            Actions actions = new Actions(driver);
            actions.moveToElement(button).click().build().perform();
            try {
                Thread.sleep(3000);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
            System.out.println("Done");
        }

        String pageCode = driver.getPageSource();
        System.out.println("Prepare to return");
       
        driver.quit();
        return pageCode;
    }

    //--------------------------------------------------------------LOADING------------------------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        //--------------------------------------------------------------SCRAPING------------------------------------------------------------------------------------------

        String fileStarter = "C:\\Users\\mattc\\Downloads\\JsoupTrying\\JsoupPractice\\src\\";
        String viewSourceFileName = "target-bowlsAnotherOne.txt";
        String filePath = fileStarter + viewSourceFileName;
        FileWriter targetMarkFileWriter = new FileWriter(fileStarter + "targetmarket.txt");

        String pageCode = loadWebPage("https://www.target.com/p/37oz-plastic-cereal-bowl-polypro-room-essentials/-/A-88065867?preselect=85443068#lnk=sametab");

        System.out.println("Page Loaded");
        try {
            //System.setOut(new PrintStream(new File(FileSystemView.getFileSystemView()
                    //.getDefaultDirectory().toString()
                    //+ File.separator + viewSourceFileName)));
            System.setOut(new PrintStream(new File(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(pageCode);
        System.out.println("Page Printed");

        File input = new File(fileStarter + viewSourceFileName);
        Document doc = Jsoup.parse(input, "UTF-8", ""); // Assuming UTF-8 encoding
        
        // Extract the usernames and reviews
        Elements usernames = doc.select("span[data-test='review-card--username']");
        Elements reviews = doc.select("div[data-test='review-card--text']");
        Elements dateOfReview = doc.select("span[data-test='review-card--reviewTime']");
        
        viewSourceFileName = "socialmediaposts.txt";
        System.setOut(new PrintStream(new File(filePath)));
        
        for (int i = 0; i < usernames.size(); i++) {
            System.out.println(usernames.get(i).text() + " , " + reviews.get(i).text() + " , " + dateOfReview.get(i).text());    
        }
        
        //--------------------------------------------------------------SCRAPING------------------------------------------------------------------------------------------
        List<JSoupPractice> parsedReviewsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" , ");
                if (parts.length == 3) {
                    String username = parts[0];
                    String review = parts[1];
                    String date = parts[2];
                    parsedReviewsList.add(new JSoupPractice(username, review, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSoupPractice[] parsedReviews = parsedReviewsList.toArray(new JSoupPractice[0]);
        
        List<String> keywordsList = new ArrayList<>();
        Scanner scanner = new Scanner(new File(fileStarter+"keywords.txt"));
        while (scanner.hasNextLine()) {
            String keyword = scanner.nextLine().trim();
            keywordsList.add(keyword);
        }

        for (JSoupPractice review : parsedReviews) {
            if (review != null && review.getReview() != null) { 
                String[] words = review.getReview().split("\\s+");
                for (String word : words) {
                    for (String keyword : keywordsList) {
                        if (word.equalsIgnoreCase(keyword)) {
                            targetMarkFileWriter.write("Target User: " + review.getUsername() + " | Keyword: " + keyword + " | Their Review: " + review.getReview() + '\n');
                        }
                    }
                }
            }
        }
        targetMarkFileWriter.close();
    }
}