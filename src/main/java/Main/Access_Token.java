/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 * Test to try to log via Selene, currently not working
 * @author Mauro
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Access_Token {
    
    public static void getAccessToken(){
		account=""
		passwd=""
                      //System.setProperty("webdriver.firefox.marionette","C:\\geckodriver.exe");
		//WebDriver driver = new FirefoxDriver();

        System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
    	
        String baseUrl = "https://auth.mercadolibre.com.ar/authorization?response_type=code&client_id=6284609745736122";

        // launch Browser and direct it to the Base URL
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.get(baseUrl);

        
        //Buscamos Login
        //String tagName = driver.findElement(By.id("user_id")).getTagName();
        //System.out.println(tagName);
        
        WebElement user = driver.findElement(By.id("user_id"));
        user.sendKeys(account);
        user.sendKeys(Keys.RETURN);
        
        //Password
        WebDriverWait myWait = new WebDriverWait(driver,10);
        
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        try {
            //System.out.println(driver.findElement(By.id("password")).getTagName());
            driver.findElement(By.id("password")).sendKeys(passwd);
            driver.findElement(By.id("password")).sendKeys(Keys.RETURN);
        } catch (Exception e) {
        }
        
        
        //We check for Second Verification Method
        if(driver.findElement(By.id("pushNotification")).isDisplayed()){
		driver.findElement(By.id("pushNotification")).click();
            
                Scanner sc = new Scanner(System.in);
 		System.out.println("Enter a string"); 
 		String code = sc.nextLine();
                
                driver.findElement(By.id("verificationCode")).sendKeys(code);
                driver.findElement(By.id("validateVerificationCode")).click();
                
                // create file named Cookies to store Login Information		
                File file = new File("Cookies.data");							
                try		
                {	  
                    // Delete old file if exists
                    file.delete();		
                    file.createNewFile();			
                    FileWriter fileWrite = new FileWriter(file);							
                    BufferedWriter Bwrite = new BufferedWriter(fileWrite);							

                    // loop for getting the cookie information 		
                    for(Cookie ck : driver.manage().getCookies())							
                    {			
                        Bwrite.write((ck.getName()+";"+ck.getValue()+";"+ck.getDomain()+";"+ck.getPath()+";"+ck.getExpiry()+";"+ck.isSecure()));																									
                        Bwrite.newLine();            
                        System.out.println(ck.getName()+";"+ck.getValue()+";"+ck.getDomain()+";"+ck.getPath()+";"+ck.getExpiry()+";"+ck.isSecure());
                    }			
                    Bwrite.close();			
                    fileWrite.close();	

                }
                catch(Exception ex)					
                {		
                    ex.printStackTrace();			
                }
        }
        
        //Thread.sleep(2000);
       
        //close Browser
        //driver.close();
    }
}
