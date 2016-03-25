package com.automation.NewTours.Apps;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import com.thoughtworks.selenium.DefaultSelenium;
import com.automation.NewTours.testdata.TestReports;
import com.automation.NewTours.util.Log4JPropertiesTest;
import com.automation.NewTours.util.TestUtil;
import com.automation.NewTours.util.Xlfile_Reader;

public class DriverApp{
	
	public static Properties CONFIG;
	public static Properties Objects;
	public static Properties APPTEXT;
	public static Xlfile_Reader Core;
	public static Xlfile_Reader testData=null;
	public static Xlfile_Reader DBresults=null;
	public static Random randomGenerator = new Random(); // Random Port Number generation 
	public static String currentTest;
	public static String keyword;
	public static SeleniumServer server;
	public static DefaultSelenium selenium=null;
	public static WebDriver dr=null;
	public static EventFiringWebDriver driver=null;
	public static String browser;
	public static String object;
	public static String currentTSID;
	public static String stepDescription;
	public static String proceedOnFail;
	public static String testStatus;
	public static String data_column_name;
	public static int  testRepeat;
	public static int nSelPort;
	public static String sSelPort;
	public static Calendar cal = new GregorianCalendar();
	public static  int month = cal.get(Calendar.MONTH);
	public static int year = cal.get(Calendar.YEAR);
	public static  int sec =cal.get(Calendar.SECOND);
	public static  int min =cal.get(Calendar.MINUTE);
	public static  int date = cal.get(Calendar.DATE);
	public static  int day =cal.get(Calendar.HOUR_OF_DAY);
	public static String strDate;
	public static String result;
	public static String mailresult=" - Script successfully executed - no errors found";
	public static String mailscreenshotpath;
	public static final Logger SELENIUM_LOGS = Logger.getRootLogger();
	//public static final Logger APPLICATION_LOGS = Logger.getLogger("devpinoyLogger");
	
	public static Logger APPLICATION_LOGS = Logger.getLogger(Log4JPropertiesTest.class);
	
	
	//Get the current system time - used for generated unique file ids (ex: Screenshots, Reports etc on every test run)
	public static String getCurrentTimeStamp()
    { 
          SimpleDateFormat CurrentDate = new SimpleDateFormat("MM-dd-yyyy"+"_"+"HH-mm-ss");
          Date now = new Date(); 
         String CDate = CurrentDate.format(now); 
          return CDate; 
    }

	
	//Loaded the Selenium and Application log files
	
   	@BeforeSuite
	public void startTesting() throws Exception{
   		
   		// Code to Generate random numbers  		
        strDate=getCurrentTimeStamp();
		//Loading Config File
		CONFIG = new Properties();
		FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\properties\\Config.Properties");
		CONFIG.load(fs);
		System.setProperty("webdriver.firefox.bin",CONFIG.getProperty("firefoxPATH"));
		 // Start testing method will start generating the Test Reports from the beginning       
		TestReports.startTesting(CONFIG.getProperty("logHTMLPath")+strDate+".html",
		TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"), 
        "NewTours",
        "1.1");
		// LOAD Objects properties File
		Objects = new Properties();
		//fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\properties\\OR_Properties.java");
		fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\properties\\Object.Properties");
		Objects.load(fs);
		APPTEXT = new Properties();
		fs = new FileInputStream(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\properties\\objectText.Properties");
		APPTEXT.load(fs);
		//Load datatable

		Core= new Xlfile_Reader(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\testdata\\Core.xlsx");
		testData  =  new Xlfile_Reader(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\testdata\\TestData.xlsx");
		//DBresults = new Xlfile_Reader(System.getProperty("user.dir")+"\\src\\com\\automation\\NewTours\\testdata\\db_data.xlsx");

		//Initializing Webdriver
				//dr = new FirefoxDriver();
		browser = CONFIG.getProperty("browser");
				
				if(browser.equalsIgnoreCase("firefox")){					
					FirefoxProfile fp = new FirefoxProfile();
					fp.setAcceptUntrustedCertificates( true );
					fp.setPreference( "security.enable_java", true ); 
					fp.setPreference( "plugin.state.java", 2 );
					//WebDriver d = new FirefoxDriver( fp );			
					dr = new FirefoxDriver(fp);			 
			        }
			    else if(browser.equalsIgnoreCase("chrome")){
			            System.setProperty("webdriver.chrome.driver","D:\\SHARE\\SELENIUM\\chromedriver.exe");
			            dr = new ChromeDriver();			 
			        }
			 	else if(browser.equalsIgnoreCase("ie")){
			            System.setProperty("webdriver.ie.driver","D:\\SHARE\\SELENIUM\\IEdriverServer.exe");  
			            dr = new InternetExplorerDriver();
			         }			 
			     else{ 
			            throw new Exception("Browser is not correct"); 
			        }

				driver = new EventFiringWebDriver(dr);	
				//maximize window
				driver.manage().window().maximize();				
				//wait for 30 seconds and then fail
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	
}
	
	@Test
	
	public void testApp() {
		
		String startTime=null;		
		
		TestReports.startSuite("Suite 1");
		for(int tcid=2 ; tcid<=Core.getRowCount("Suite1");tcid++){
			currentTest = Core.getCellData("Suite1", "TCID", tcid);
			// initilize start time of test
			if (Core.getCellData("Suite1", "Runmode", tcid).equals("N")){
				continue;
			}
			else if(Core.getCellData("Suite1", "Runmode", tcid).equals("Y")){	
				// executed the keywords				
				// loop again - rows in test data
				int totalSets=testData.getRowCount(currentTest+"1");; // holds total rows in test data sheet. IF sheet does not exist then 2 by default
				if(totalSets<=1){
					totalSets=2; // run atleast once
				}
					
				for( testRepeat=2; testRepeat<=totalSets;testRepeat++){	
					startTime=TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa");

				APPLICATION_LOGS.debug("Executing the test "+ currentTest);
				// implemented keywords file
				try{
				for(int tsid=2;tsid<=Core.getRowCount(currentTest);tsid++){
					// Get values from xls file					
					keyword=Core.getCellData(currentTest, "Keyword", tsid);
					object=Core.getCellData(currentTest, "Object", tsid);
					currentTSID=Core.getCellData(currentTest, "TSID", tsid);
					stepDescription=Core.getCellData(currentTest, "Decription", tsid);
					proceedOnFail=Core.getCellData(currentTest, "ProceedOnFail", tsid);
					data_column_name=Core.getCellData(currentTest, "Data_Column_Name", tsid);
					Method method= KeywordsApp.class.getMethod(keyword);
					result = (String)method.invoke(method);
					APPLICATION_LOGS.debug("***Result of execution -- "+result);
					// take screenshot - every keyword
					String fileName="Suite1_TC"+(tcid-1)+"_TS"+tsid+"_"+keyword+testRepeat+".jpeg";						
					if(result.startsWith("Pass")){
						testStatus=result;						
			
						//TestUtil.captureScreenshot(CONFIG.getProperty("screenshotPath")+TestUtil.imageName+".jpeg");
					TestReports.addKeyword(stepDescription, keyword, result, "Suite1_TC"+(tcid-1)+"_TS"+tsid+"_"+keyword+testRepeat+".jepg");
					}
					
					else if(result.startsWith("Fail")){
							testStatus=result;
							// take screenshot - only on error
						//TestUtil.captureScreenshot(CONFIG.getProperty("screenshotPath")+TestUtil.imageName+".jpeg");
						TestUtil.captureScreenshot(CONFIG.getProperty("screenshotPath")+fileName);
						//changed to make the screenshot path generic
						TestReports.addKeyword(stepDescription, keyword, result, "Suite1_TC"+(tcid-1)+"_TS"+tsid+"_"+keyword+testRepeat+".jpeg");
						if(proceedOnFail.equalsIgnoreCase("N")){
						
								break;								
							}
						break;
						
						}
					
					}
					
					
				}
				catch(Throwable t){
					APPLICATION_LOGS.debug("Error came");
					
				}
				
				// report pass or fail in HTML Report
				
				if(testStatus == null){
					testStatus="Pass";
				}
				APPLICATION_LOGS.debug("##"+currentTest+" --- " +testStatus);
				TestReports.addTestCase(currentTest, 
										startTime, 
										TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),
										testStatus );
				
				if(result.startsWith("Fail")){
				
					break; 
	                  }
				
				}// test data

				
				
			}else{
				APPLICATION_LOGS.debug("Skipping the test "+ currentTest);
				testStatus="Skip";
				
				// report skipped
				APPLICATION_LOGS.debug("####"+currentTest+" --- " +testStatus);
				TestReports.addTestCase(currentTest, 
										TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"), 
										TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),
										testStatus );
				
			}
			
			testStatus=null;
			
			if(result.startsWith("Fail")){
                break; 
                }

		}
		TestReports.endSuite();
	}
	
	

	
	@AfterSuite
	public static void endScript() throws Exception{
	
		// Once the test is completed update the end time in HTML report
		TestReports.updateEndTime(TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"));
		driver.quit();

	}
	
}
