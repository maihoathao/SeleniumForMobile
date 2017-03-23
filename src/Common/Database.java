/*
 * Date: 22/03/2017
 * Thao tác với db
 */
package Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Database {
	
	public static Connection conn = null;
    public static Statement stmt = null;
	
    /*
     * Author: hieuht
     * Kết nối db
     * Date: 22/03/2017
     */
	public static void connectDatabase () throws Exception {
		/*
		 * String dbuserName: User kết nối server
		 * String dbpassword: "Password kết nối server
		 * String database: Tên database muốn kết nối
		 * int nLocalPort: Port (default = 3306)
		 * String pathServer: path server
		 */
		String dbuserName = "vcinema_v3";
	    String dbpassword = "vcinema_v32012";
	    String database = "vcinema_v3";
	    int nLocalPort = 3306;
	    String pathServer = "192.168.0.102";
	    //Kết nối DB
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		conn = DriverManager.getConnection("jdbc:mysql://"+pathServer+":"+nLocalPort+"/"+database+"?useUnicode=yes&characterEncoding=UTF-8", dbuserName, dbpassword);
		
		stmt = conn.createStatement();    
	}
	
	
}
