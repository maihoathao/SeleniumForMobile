/*
 * Date: 22/03/2017
 * Thao tác với web
 */
package Common;

import java.net.URL;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;


public class Driver 
{
	/*
	 * Author: hieuht
	 * RemoteWebDriver driver: driver
	 * int countTime: thời gian nghỉ (chờ load ajax, js,..)
	 */
	private RemoteWebDriver driver = null;
	private int countTime = 0;
	
	/*
	 * Author: hieuht
	 * Khởi tạo driver
	 * Date: 22/03/2017
	 * Para:
	 * 	DesiredCapabilities capabilities: thông số thiết bị test
	 * 	URL url: thông số kết nối Appium
	 */
	public Driver(DesiredCapabilities capabilities, URL url) {
		/*
		 * Khi dùng RemoteWebDriver thì có thể sử dụng cho cả Android và IOS
		 * Nếu dùng riêng, Android dùng AndroidDriver
		 * IOS dùng IOSDriver
		 */
		driver = new RemoteWebDriver(url, capabilities);
	}
	
	/*
	 * Author: hieuht
	 * Các phương thức tìm element trên web
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * Output:
	 * 	By: dữ liệu để get element
	 */
	public By getBy(String locator) {
		By by=null;
		if(locator.startsWith("id=")){
			locator = locator.substring(3);
			by = By.id(locator);
		} else if(locator.startsWith("css=")){
			locator = locator.substring(4);
			by = By.cssSelector(locator);
		} else if(locator.startsWith("xpath=")){
			locator = locator.substring(6);
			by = By.xpath(locator);
		} else if(locator.startsWith("className=")){
			locator = locator.substring(10);
			by = By.className(locator);
		} else if(locator.startsWith("name=")){
			locator = locator.substring(5);
			by = By.name(locator);
		} else if(locator.startsWith("linkText=")){
			locator = locator.substring(9);
			by = By.linkText(locator);
		}
		return by;
	}
	
	/*
	 * Author: hieuht
	 * Get element trên web
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * Output:
	 * 	WebElement: đối tượng cần tìm
	 */
	public WebElement getElenment(String locator) {
		return driver.findElement(getBy(locator));
	}
	
	/*
	 * Author: hieuht
	 * Get list element 
	 * DateL 22/03/2017
	 * String locator: định danh đối tượng
	 * Para:
	 * 	String locator: định danh đối tượng
	 * Output:
	 * 	List<WebElement>: list các đối tượng cần tìm
	 */
	public List<WebElement> getListElenment(String locator) {
		List<WebElement> ls = null;
		ls = driver.findElements(getBy(locator));
		return ls;
	}
	
	/*
	 * Author: hieuht
	 * Get element từ list element
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * 	int index: chỉ số trong list
	 * Output:
	 * 	WebElement: đối tượng web cần lấy
	 */
	public WebElement getElementFormList(String locator, int index) throws Exception {
		return this.getListElenment(locator).get(index);
	}
	
	/*
	 * Author: hieuht
	 * Click element
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * 	int timeSleep: thời gian nghỉ sau khi click
	 */
	public void click(String locator, int timeSleep) throws Exception {
		getElenment(locator).click();
		sleep(timeSleep);
	}
	
	/*
	 * Author: hieuht
	 * Return driver
	 * Date: 22/03/2017
	 * Output:
	 * 	RemoteWebDriver: đối tượng driver
	 */
	public RemoteWebDriver getDriver(){
		return this.driver;
	}
	
	/*
	 * Author: hieuht
	 * Nhập dữ liệu vào các ô Textbox
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * 	String message: dữ liệu cần nhập
	 * 	int timeSleep: thời gian nghỉ sau khi nhập
	 */
	public void sendKey(String locator, String message, int timeSleep) throws Exception {
		getElenment(locator).clear();
		getElenment(locator).sendKeys(message);
		sleep(timeSleep);
	}
	
	/*
	 * Author: hieuht
	 * Select dữ liệu từ dropdownlist
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * 	String message: dữ liệu cần chọn
	 * 	int timeSleep: thời gian nghỉ sau khi nhập
	 */
	public void selectByVisibleText(String locator, String message, int timeSleep) throws Exception {
		new Select(getElenment(locator)).selectByVisibleText(message);
		sleep(timeSleep);
	}
	
	/*
	 * Author: hieuht
	 * Get title current web
	 * Date: 22/03/2017
	 * Output:
	 * 	String: title web
	 */
	public String getTitle() throws Exception {
		return this.driver.getTitle();
	}
	
	/*
	 * Author: hieuht
	 * Focus element and click
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * 	int timeSleep: thời gian nghỉ sau khi click
	 */
	public void focusAndClick (String locator, int timeSleep) throws Exception {
		WebElement e = driver.findElement(getBy(locator));
		
		//Di chuyển màn hình xuống đối tượng trước khi click
		Actions actions = new Actions(driver);
		actions.moveToElement(e);
		actions.perform();
		
		//click
		e.click();
		sleep(timeSleep);
	}
	
	/*
	 * Author: hieuht
	 * Kiểm tra xem đối tượng đã được select chưa
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * Output:
	 * 	boolean: true/false nếu đã chọn/chưa chọn
	 */
	public boolean isSelected(String locator) {
		if(getElenment(locator).isSelected())
			return true;
		else
			return false;
	}
	
	/*
	 * Author: hieuht
	 * Get text đang được chọn trong dropdownlist
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * Output:
	 * 	String: text
	 */
	public String getTextFromDropDownList(String locator) {
		Select select = new Select(getElenment(locator));
		WebElement option = select.getFirstSelectedOption();
		return option.getText();
	}
	
	/*
	 * Author: hieuht
	 * Xoá data text box
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 */
	public void clear(String locator) throws Exception {
		getElenment(locator).clear();
	}
	
	/*
	 * Author: hieuht
	 * Lấy dữ liệu trong textboxx
	 * Date: 22/03/2017
	 * Para:
	 * 	String locator: định danh đối tượng
	 * Output:
	 * 	String: chuỗi vừa lấy
	 */
	public String getText(String locator) {
		return getElenment(locator).getText();
	}
	
	/*
	 * Author: hieuht
	 * Get current link
	 * Date: 22/03/2017
	 * Output:
	 * 	String: link 
	 */
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
	
	/*
	 * Author: hieuht
	 * End session
	 * Date: 22/03/2017
	 */
	public void close() throws Exception {
		this.driver.close();	
	}
	
	//Auto get data from excel and fill to element
//	public void autoFill(ObjectInput[] array, int column, ExcelAction excel) throws Exception {
//		for(int i=0; i<array.length; i++){
//			if(array[i].getActon() == 0)
//				continue;
//			if(array[i].getActon() == 1)
//				doAction(array[i].getType(), array, column, excel, i);
//			if(array[i].getActon() == 2){
//				if(excel.getStringData(column, i).equals(""))
//					continue;
//				else if(excel.getStringData(column, i).equals("#SPACE#")) {
//					excel.printStringIntoExcel(column, i, "");
//					doAction(array[i].getType(), array, column, excel, i);
//				} else {
//					doAction(array[i].getType(), array, column, excel, i);
//				}
//			}
//		}
//	}
	
	/*
	 * Author: hieuht
     * Handle alert and get text from alert
     * Date: 22/03/2017
     * Para:
     * 	boolean acceptNextAlert: true: accept alert, false: cancel alert
     * 	int timeSleep: thời gian nghỉ sau khi xử lý
     * Output:
     * 	String: text from alert
     */  
    public String closeAlertAndGetItsText(boolean acceptNextAlert, int timeSleep) throws Exception {
          Alert alert = driver.switchTo().alert();
          String alertText = alert.getText();
          if (acceptNextAlert) {
            alert.accept();
            sleep(timeSleep*2);
          } else {
            alert.dismiss();
            sleep(timeSleep*2);
          }
          return alertText;
      }
    
    /*
     * Author: hieuht
     * Open link
     * Date: 22/03/2017
     * Para:
     * 	String url: địa chỉ link
     */
    public void get(String url) throws Exception {
		this.driver.get(url);
	}
    
    /*
     * Author: hieuht
     * Dừng hệ thống theo thời gian truyền vào
     * Date: 22/03/2017
     * Para:
     * 	int timeSleep: thời gian nghỉ
     */
    public void sleep(int timeSleep) throws Exception {
		countTime += timeSleep;
		Thread.sleep(timeSleep);
	}
    
    /*
     * Author: hieuht
     * Get tổng thời gian nghỉ
     * Date: 22/03/2017
     * Output: thời gian nghỉ
     */
    public int getCountTime() {
		return countTime;
	}
}
