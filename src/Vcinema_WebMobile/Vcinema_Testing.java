/*
 * Author: hieuht
 * Các hàm test chức năng Vcinema web mobile
 * Date: 22/03/2017
 */
package Vcinema_WebMobile;

import java.util.List;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import Common.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class Vcinema_Testing 
{
	/*
	 * 	public String pathDat: đường dẫn file excel
		String timeBuild: thời gian chạy test
		public Driver driver: driver
		public String link: link trang web
		public long startTime: thời gian bắt đầu chạy
		public long endTime: thời gian kết thúc chạy
		public String env: môi trường: Android / IOS
	 */
	public String pathData = "data/vcinema-mobile/data-test.xls";
	String timeBuild = "";
	public Driver driver = null;
	public String link = "http://vcinema3.bestapps.vn/";
	public long startTime;
	public long endTime;
	public String OS;
	
	/*
	 * Author: hieuht
	 * Setup thông số device, connect database, tạo file kết quả
	 * Date: 22/03/2017
	 */
	@BeforeTest
	public void open() throws Exception {
//		System.out.println("Working Directory = " +
//	              System.getProperty("user.dir"));
		
		//Set time cho tên file log
		timeBuild = new SimpleDateFormat("yyyy-MM-dd HH.mm").format(Calendar.getInstance().getTime());
		System.setProperty("current.date", timeBuild);
		PropertyConfigurator.configure("log4j.properties");
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		Logger logger = Logger.getLogger("open");
		logger.info("Start test at " + dateFormat.format(date));
		logger.info("Setup to connect devices");
		
		startTime = System.currentTimeMillis();
		
		//Tạo file output
		Excel excel = new Excel();
		excel.createFileOutput(pathData, timeBuild);
		excel.accessSheet("Config");
		
		String IP = excel.getStringData(1, 1);
		String Port = excel.getStringData(1, 2);
		OS = excel.getStringData(1, 7);
		String DeviceName = excel.getStringData(1, 8);
		String OsVersion = excel.getStringData(1, 9);
		String pathChrome = excel.getStringData(1, 10);
		
		//Setup device và IP theo thông số trong excel (sheet "config")
		try {
			
			DesiredCapabilities capabilities = null;
			if(OS.equals("Android")) {
				//Android
				capabilities=DesiredCapabilities.android();		 
				capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
				capabilities.setCapability(MobileCapabilityType.PLATFORM,Platform.ANDROID);
				capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, OS);
				capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, DeviceName);
				capabilities.setCapability(MobileCapabilityType.VERSION, OsVersion);	
				capabilities.setCapability("app", pathChrome);	

			} else if (OS.equals("IOS")) {
				//IOS
				capabilities=DesiredCapabilities.iphone();		 
				capabilities.setCapability("deviceName", DeviceName);
				capabilities.setCapability("platformName", "iOS");
				capabilities.setCapability("platformVersion", OsVersion);
				//capabilities.setCapability("autoAcceptAlerts", true);
				//capabilities.setCapability("safariAllowPopups", true);
				capabilities.setCapability(CapabilityType.BROWSER_NAME, "Safari");
			}
			URL url= new URL("http://"+IP+":"+Port+"/wd/hub");
			driver = new Driver(capabilities, url);
			logger.info("Done setup to connect devices");
			
		} catch (Exception e) {
			logger.error("Can not set up device to connect Appium: "+ e.getMessage());
			logger.info("Exit.");
			System.exit(0);
		}
		
		//Log tên phiên bản brower đang chạy
		if(OS.equals("Android")) {
			driver.get("chrome://version");
			
			logger.info("Brower version: " + driver.getText("id=version"));
		} else if (OS.equals("IOS")) {
			driver.get("http://www.whoishostingthis.com/tools/user-agent/");
			List<WebElement> lse = driver.getListElenment("className=info-box");
			logger.info(lse.get(0).getText());
		}
		
		//Connect database
		logger.info("Connect database");
		try {
		//connect db
		Database.connectDatabase();
		} catch (Exception e) {
			logger.error("Fail to connect database");
			System.exit(0);
		}
		logger.info("Connect database success");
		
		
	}
	
	/*
	 * Author: hieuht
	 * Gọi đến các hàm theo thứ tự flow trong excel
	 * Date: 22/03/2017
	 */
	@Test
	public void run () throws Exception {
		
		Logger logger = Logger.getLogger("run");
		Excel excel = new Excel(pathData, timeBuild);
		excel.accessSheet("Run");

		String[] array = { "case", "filterCinemaByLocation", "verifyTrailer", "validateImage",
				"movieSchedule", "news", "type", "result"};
		
		int resultColumn = Common.getIndex(array, "result");
		
		//Gọi các chức năng theo thứ tự flow
		for(int i=1; i<excel.getSheet().getPhysicalNumberOfRows(); i++) {
//			try {
				if(excel.getStringData(Common.getIndex(array, "case"), i).equals(""))
					break;
				logger.info("Start testcase "+excel.getStringData(Common.getIndex(array, "case"), i));
				if(accessAction(excel, array, i) == true)
					excel.printResultToFlow("T", excel.getStringData(Common.getIndex(array, "type"), i), excel, i, resultColumn);
				else{
					excel.printResultToFlow("F", excel.getStringData(Common.getIndex(array, "type"), i), excel, i, resultColumn);
				}
				logger.info("End testcase "+excel.getStringData(Common.getIndex(array, "case"), i));
//			} catch (Exception e) {
//				excel.accessSheet("Run");
//				excel.printResultToFlow("F", excel.getStringData(Common.getIndex(array, "type"), i), excel, i, resultColumn);
//				logger.error(e.getMessage());
//				logger.info("End testcase "+excel.getStringData(Common.getIndex(array, "case"), i));
//			}
		}
		excel.finish();
	}
	
	/*
	 * Author: hieuht
	 * Gọi đến các chức năng theo thứ tự trong mảng
	 * Date: 22/03/2017
	 * Para:
	 * 	String[] array: mảng
	 * 	Excel excel: file excel
	 * 	int row: dòng chứa dữ liệu
	 */
	public boolean accessAction(Excel excel, String[] array, int row)throws Exception{
		for(int i=1; i<array.length; i++){
			//System.out.println(excel.getStringData(Common.getIndex(array, array[i]), row).equals(""));
			if(!excel.getStringData(Common.getIndex(array, array[i]), row).equals("")){
				int column = excel.getColumn(excel.getStringData(Common.getIndex(array, array[i]), row));
				switch(array[i]){
					case "filterCinemaByLocation": {
						return filterCinemaByLocation(column);
					}
					case "verifyTrailer": {
						return verifyTrailer(column);
					}
					case "validateImage": {
						return validateImageSlide(column);
					}
					case "movieSchedule": {
						return viewMovieSchedule(column);
					}
					case "news": {
						return viewNews(column);
					}
				}
				excel.write();
			}
		}
		return true;
	}
	
	/*
	 * Author: hieuht
	 * Lọc rạp chiếu phim theo địa chỉ
	 * Date: 22/03/2017
	 * Para:
	 * 	int column: cột chứa dữ liệu
	 * Output:
	 * 	boolean: true/false
	 */
	public boolean filterCinemaByLocation(int column) throws Exception {
		Logger logger = Logger.getLogger("filterCinemaByLocation");
		logger.info("Start filterCinemaByLocation on GUI");
		
		Excel excel = new Excel(pathData, timeBuild);
		excel.accessSheet("Filter cinema by location");
		
		//Vào link rạp
		driver.get(link+"mobile/list_cinema");
		
		//Chọn địa chỉ
		String city = excel.getStringData(column, 0);
		driver.selectByVisibleText("className=select-rap", city, 1000);
		
		List<WebElement> ls = driver.getListElenment("xpath=//span[contains(@class, 'rap-name')]");
		//Có 2 dòng là address và phone dùng chung 1 loại đối tượng
		List<WebElement> ls1 = driver.getListElenment("xpath=//span[contains(@class, 'txt-addr')]");
		int count_name = 0;
		int count_info = 0;
		
		//Lấy list rạp trên web
		List<String> listWeb = new ArrayList<String>();
		
		while(true) {	//khi chưa duyệt hết list rạp chiếu trên MH web
			if(count_name >= ls.size())
				break;
			if(!ls.get(count_name).getText().equals("")) {
				listWeb.add(ls.get(count_name).getText());	//đọc và lấy tên rạp chiếu
				listWeb.add(ls1.get(count_info).getText());		//đọc và lấy địa chỉ
				listWeb.add(ls1.get(count_info+1).getText());	//đọc và lấy số điện thoại
			}
			count_name++;
			count_info+=2;
		}
		
		//lấy list rạp trong excel
		List<String> listExcel = new ArrayList<String>();
	
		for (int i = 1; i < excel.getSheet().getPhysicalNumberOfRows(); i++) {
			if(excel.getStringData(column, i).equals(""))
				break;
			if(excel.getStringData(column, i).equals("#none#"))
				listExcel.add("");
			else 
				listExcel.add(excel.getStringData(column, i));
		}

		//So sánh dữ liệu trên web và excel
		if(listWeb.containsAll(listExcel) && listWeb.size() == listExcel.size()) {
			logger.info("Success filterCinemaByLocation on GUI");
			logger.info("Start filterCinemaByLocation on DB");
			//Check db
			ResultSet rs = null;

			rs = Database.stmt.executeQuery("select city_id from city where city_name = N'"+city+"'");
			rs.next();
			String id_city = rs.getString("city_id");
			
			rs = Database.stmt.executeQuery("select * from cinema where cine_active = 1 AND cine_city_id = "+id_city+" order by cine_id ASC ");
			
			//Lấy list rạp trong db
			List<String> listResult = new ArrayList<String>();
			while(rs.next()) {
				listResult.add(rs.getString("cine_name"));
				listResult.add(rs.getString("cine_address"));
				listResult.add(rs.getString("cine_phone"));
			}
			
			//So sánh dữ liệu db và web
			if(listWeb.containsAll(listResult) && listWeb.size() == listResult.size()) {
				logger.info("Success filterCinemaByLocation on DB");
				return true;
			} else {
				logger.error("Fail filterCinemaByLocation on DB");
				return false; 
			}
		} else {
			logger.error("Fail filterCinemaByLocation on GUI");
			return false;
		}
	}
	
	/*
	 * Author: hieuht
	 * Kiểm tra trailer
	 * Date: 22/03/2017
	 * Para:
	 * 	int column: cột chứa dữ liệu
	 * Output:
	 * 	boolean: true/false
	 */
	public boolean verifyTrailer(int column) throws Exception {
		Logger logger = Logger.getLogger("verifyTrailer");
		logger.info("Start verifyTrailer on GUI");
		
		Excel excel = new Excel(pathData, timeBuild);
		excel.accessSheet("Verify trailer");
		
		ResultSet rs = null;
		
		String trailer = excel.getStringData(column, 1);
		String movie_name = excel.getStringData(column, 0);
		
		//Lấy id film từ tên film
		rs = Database.stmt.executeQuery("select mv_id from movie where mv_title = N'"+movie_name+"'");
		rs.next();
		String movie_id = rs.getString("mv_id");
		
		//Vào màn hình chi tiết
		driver.get(link+"mobile/detail_film/"+movie_id);
		driver.sleep(1000);
		
		//driver.sleep(500);
		//driver.click("className=xem-trailer", 500);
		driver.focusAndClick("className=xem-trailer", 500);
		
		String linkTrailerExcel = excel.getStringData(column, 2);
		
//		try {
			driver.sleep(5000);
			System.out.println("trailer: "+driver.getDriver().findElement(By.className("xem-trailer")).getAttribute("href"));
			
			System.out.println("ok");
			driver.click("className=ytp-large-play-button-bg", 2000);
			System.out.println("error "+driver.getText("className=ytp-error-content-wrap"));
			System.out.println("ok1");
			//Check hiện form trailer, nếu ko có --> false
			driver.click("xpath=//span[contains(@class, 'YouTubePopUp-Close')]", 1000);
			
//			if (trailer.equals("1")) {
				logger.info("Success verifyTrailer on GUI");
				logger.info("Start verifyTrailer on DB");

				//So sánh link trailer trong excel và db
				rs = Database.stmt.executeQuery("select mv_trailer from movie where mv_id = "+movie_id);
				rs.next();
				String movie_trailer = rs.getString("mv_trailer");
				
				//Compare to trailer get in website
				
				if(movie_trailer.equals(linkTrailerExcel)) {
					logger.info("Success verifyTrailer on DB");
					return true;
				} 
				else {
					logger.error("Fail verifyTrailer on DB");
					return false;
				}
//			}
//			else {
//				logger.error("Fail verifyTrailer on GUI");
//				return false;
//			}
//		} catch (org.openqa.selenium.NoSuchElementException e) {
//			if (trailer.equals("0")) {
//				logger.info("Success verifyTrailer on GUI");
//				return true;
//			}
//			else {
//				logger.error("Fail verifyTrailer on GUI");
//				return false;
//			}
//		}
	}
	
	/*
	 * Author: hieuht
	 * Kiểm tra slide ảnh trong xem chi tiết film
	 * Date: 22/03/2017
	 * Para:
	 * 	int column: cột chứa dữ liệu
	 * Output:
	 * 	boolean: true/false
	 */
	public boolean validateImageSlide (int column) throws Exception {
		Logger logger = Logger.getLogger("validateImageSlide");
		logger.info("Start validateImageSlide on GUI");
		
		Excel excel = new Excel(pathData, timeBuild);
		excel.accessSheet("Validate image slide");
		ResultSet rs = null;
		
		String movie_name = excel.getStringData(column, 0);
		
		//Lấy id film từ tên film
		rs = Database.stmt.executeQuery("select mv_id from movie where mv_title = N'"+movie_name+"'");
		rs.next();
		String movie_id = rs.getString("mv_id");
		
		//Vào màn hình chi tiết
		driver.get(link+"mobile/detail_film/"+movie_id);
		
		//Lấy list ảnh trên web
		List<String> dataWeb = new LinkedList<String>();
		List<WebElement> allImages = driver.getListElenment("className=slick-slide");
		int notUse = 0;
		for(WebElement e : allImages) {
			if(!Common.isExist(dataWeb, e.getAttribute("src")) && notUse != 0) {
				dataWeb.add(e.getAttribute("src"));
			}
			notUse++;
		}
		
		//Check ảnh not found
		logger.info("Start check error 404");
		//Check error 404 not found
		for(int i = 0; i<dataWeb.size() ; i++) {
			driver.get(dataWeb.get(i));
			if(driver.getTitle().equals("404 Page Not Found")) {
				logger.error("Error 404 - " + dataWeb.get(i));
				return false;
			}
			
		}
		logger.info("End check error 404");
		
		//Lấy list ảnh trong excel
		int row = 1;
		List<String> dataExcel = new LinkedList<String>();
		while(!excel.getStringData(column, row).equals("")) {
			dataExcel.add(link+"assets/images/images/"+excel.getStringData(column, row));
			row ++;
		}
		
		//So sánh ảnh trên web và excel
		if(dataExcel.containsAll(dataWeb)) {
			logger.info("Success validateImageSlide on GUI");
			logger.info("Start validateImageSlide on DB");
			//check db
			rs = Database.stmt.executeQuery("select img_path from image where img_active = 1 AND img_mv_id = "+movie_id+" order by img_id ASC");
			
			//Lấy list ảnh trong db
			List<String> listResult = new ArrayList<String>();
			while(rs.next()) {
				listResult.add(link+"assets/images/images/"+rs.getString("img_path"));
			}
			
			//So sánh ảnh trong db và web
			if(dataWeb.containsAll(listResult)) {
				logger.info("Success validateImageSlide on DB");
				return true;
			}
			else {
				logger.error("Fail validateImageSlide on DB");
				return false;
			}
		}
		else {
			logger.error("Fail validateImageSlide on GUI");
			return false;
		}
	}

	
	/*
	 * Author: hieuht
	 * Xem lịch chiếu phim
	 * Date: 22/03/2017
	 * Para:
	 * 	int column: cột chứa dữ liệu
	 * Output:
	 * 	boolean: true/false
	 */
	public boolean viewMovieSchedule (int column) throws Exception {
		Logger logger = Logger.getLogger("viewMovieSchedule");
		logger.info("Start viewMovieSchedule on GUI");
		
		Excel excel = new Excel(pathData, timeBuild);
		excel.accessSheet("View movie schedule");
		
		ResultSet rs = null;
		
		//Lấy id film từ tên film
		String movie_name = excel.getStringData(column, 0);
		
		rs = Database.stmt.executeQuery("select mv_id from movie where mv_title = N'"+movie_name+"'");
		rs.next();
		String movie_id = rs.getString("mv_id");
		
		String have_schedule = excel.getStringData(column, 1);
		
		driver.get(link+"mobile/detail_film/"+movie_id);
		
		//Check lịch chiếu phim
		if(OS.equals("IOS")) {
		try {
			driver.click("xpath=/html/body/section/div/div[4]/div[2]/a", 0);
			//get list
			String default_location = driver.getTextFromDropDownList("className=select-rap");
			
			if(have_schedule.equals("1") && default_location.equals("Hà Nội")) {
				logger.info("Success viewMovieSchedule on GUI");
				return true;
			}
			else {
				logger.error("Fail viewMovieSchedule on GUI");
				return false;
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {
			driver.closeAlertAndGetItsText(true, 0);
			if(have_schedule.equals("0")) {
				logger.info("Success viewMovieSchedule on GUI");
				return true;
			}
			else {
				logger.error("Fail viewMovieSchedule on GUI");
				return false;
			}
		}
		} else if (OS.equals("Android")) {
			try {
				driver.get(link+"mobile/list_cinema?mv="+movie_id);
				//get list
				String default_location = driver.getTextFromDropDownList("className=select-rap");
				
				if(have_schedule.equals("1") && default_location.equals("Hà Nội")) {
					logger.info("Success viewMovieSchedule on GUI");
					return true;
				}
				else {
					logger.error("Fail viewMovieSchedule on GUI");
					return false;
				}
			} catch (org.openqa.selenium.UnhandledAlertException e) {
				driver.closeAlertAndGetItsText(true, 0);
				if(have_schedule.equals("0")) {
					logger.info("Success viewMovieSchedule on GUI");
					return true;
				}
				else {
					logger.error("Fail viewMovieSchedule on GUI");
					return false;
				}
			}
		} else
			return false;
	}
	
	/*
	 * Author: hieuht
	 * Xem news
	 * Date: 22/03/2017
	 * Para:
	 * 	int column: cột chứa dữ liệu
	 * Output:
	 * 	boolean: true/false
	 */
	public boolean viewNews (int column) throws Exception {
		Logger logger = Logger.getLogger("viewNews");
		logger.info("Start viewNews on GUI");
		
		Excel excel = new Excel(pathData, timeBuild);
		excel.accessSheet("View news");
		
		String type = excel.getStringData(column, 0);
		
		//Vào link news theo loại
		if (type.equals("Tin Vcinema")) {
			driver.get(link+"mobile/news/3");
			type = "Tin vCinema";
		}
		else if (type.equals("Tin Rạp")) {
			driver.get(link+"mobile/news/2");
			type = "Tin tức rạp";
		}
		else if (type.equals("Tin Điện ảnh")) {
			driver.get(link+"mobile/news/1");
			type = "Tin điện ảnh";
		}
		
		List<WebElement> ls = driver.getListElenment("xpath=//span[contains(@class, 'rap-name')]");
		List<WebElement> ls1 = driver.getListElenment("xpath=//span[contains(@class, 'txt-addr')]");
		int count_name = 0;
		int count_time = 0;
		
		//Lấy list thông tin trên web
		List<String> listWeb = new ArrayList<String>();
		
		while(true) {
			if(count_name >= ls.size())
				break;
			if(!ls.get(count_name).getText().equals("")) {
				listWeb.add(ls.get(count_name).getText());
				listWeb.add(ls1.get(count_time).getText());
			}
			count_name++;
			count_time++;
		}
		
		//Lấy list thông tin trong excel
		List<String> listExcel = new ArrayList<String>();
		
		for (int i = 1; i < excel.getSheet().getPhysicalNumberOfRows(); i++) {
			if(excel.getStringData(column, i).equals(""))
				break;
			if(excel.getStringData(column, i).equals("#none#"))
				listExcel.add("");
			else 
				listExcel.add(excel.getStringData(column, i));
		}		
		
		//So sánh dữ liệu trên web và excel
		if(listWeb.containsAll(listExcel) && listWeb.size() == listExcel.size()) {
			logger.info("Success viewNews on GUI");
			logger.info("Start viewNews on DB");
			
			//Lấy list dữ liệu trong db
			//Check db
			ResultSet rs = null;
			rs = Database.stmt.executeQuery("select cat_nw_id from category_news where cat_nw_name = N'"+type+"'");
			rs.next();
			String cat_new_id = rs.getString("cat_nw_id");
			
			rs = Database.stmt.executeQuery("select * from news where nw_active = 1 AND nw_cat_id = "+cat_new_id+" order by nw_date DESC limit 20");
			List<String> listResult = new ArrayList<String>();
			
			while(rs.next()) {
				listResult.add(rs.getString("nw_title"));
				listResult.add(rs.getString("nw_date"));
			}
			
			//So sánh dữ liệu web và db
			if(listWeb.containsAll(listResult) && listWeb.size() == listResult.size()) {
				logger.info("Success viewNews on DB");
				return true;
			} else  {
				logger.error("Fail viewNews on DB");
				return false;
			}
		}
		else {
			logger.error("Fail viewNews on GUI");
			return false;
		}
	}
	
	
	/*
	 * Author: hieuht
	 * Log các thông số
	 * Date: 22/03/2017
	 */
	@AfterTest
	public void end () throws Exception {
		Logger logger = Logger.getLogger("end");
		endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		logger.info("Tong thoi gian chay: "+ totalTime/1000 + "s ~ "+totalTime/1000/60+"p"+totalTime/1000%60+"s");
		logger.info("Tong time nghi: "+ driver.getCountTime()/1000 + "s ~ "+driver.getCountTime()/1000/60+"p"+driver.getCountTime()/1000%60+"s");
	}
}


