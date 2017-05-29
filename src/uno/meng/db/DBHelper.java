package uno.meng.db;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.SQLException;  
  
public class DBHelper {  
    public static final String url = "jdbc:mysql://127.0.0.1:3306/EclipseBugInformation?characterEncoding=utf8&useSSL=false";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "";  
    public Connection conn = null;  
    public PreparedStatement pst = null;  
    public Connection open(){
    	try {  
            Class.forName(name);//指定连接类型  
            conn = DriverManager.getConnection(url, user, password);//获取连接 
            return conn;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    		return null;
    }
    public void close(){  
        try {  
            this.conn.close();  
            this.pst.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}  