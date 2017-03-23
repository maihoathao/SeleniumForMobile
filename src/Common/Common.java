/*
 * Thực hiện các hàm chung của tất cả các class
 * @Author: hieuht
 * @Date: 22/03/2017
 */
package Common;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;

public class Common {

	/*
	 * Lấy chỉ số từ mảng (để xác định vị trí)
	 * Date: 22/03/2017
	 * Para:
	 * 	String[] array: mảng
	 * 	String find: dữ liệu tìm kiếm
	 * Output:
	 * 	int: chỉ số của dữ liệu
	 */
	public static int getIndex(String[] array, String find) {
		for(int i=0; i<array.length; i++) {
			if(array[i].equals(find))
				return i;
		}
		return -1;
	}
	
	/*
	 * Author: hieuht
	 * Kiểm tra rỗng
	 * Date: 22/03/2017
	 * Parameter: String string: chuỗi kiểm tra
	 * Output: String: chuỗi sau check
	 */
	public static String checkNull(String string){
		if(string == null || string.equals("") ){
			return "";
		}
		return string;
	}
	
	/*
	 * Author: hieuht
	 * Kiểm tra tồn tại trong mảng
	 * Date: 22/03/2017
	 * Para:
	 * 	List<String>: mảng
	 * 	String string: dữ liệu cần check
	 * Output:
	 * 	boolean: true/false if exist/not exist
	 */
	public static boolean isExist (List<String> list, String string) {
		if(list != null)
			for(int i=0; i<list.size(); i++) {
				if (list.get(i).equals(string))
					return true;
			}
		return false;
	}
	
//	//delete empty line
//	public static String deleteEmptyline (String string) {
//		return string.replaceAll("(?m)^[ \t]*\r?\n", "");
//	}
}
