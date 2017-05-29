package uno.meng.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CUID {
	static DBHelper db = new DBHelper();  
    static ResultSet ret = null;  
    static int retu = 0;
    String sql = new String(); 
    public Connection conn = null;  
    public PreparedStatement pst = null;
    String[] table = new String[]{"CDT","JDT","PDE","Platform"};
    public CUID(){
    		conn = db.open();
    }
	public void Search(){
		sql = "";//SQL语句  
//        db = new DBHelper(sql);//创建DBHelper对象  
        try {  
            ret = db.pst.executeQuery();//执行语句，得到结果集  
            while (ret.next()){  
            }//显示数据  
            ret.close();  
            db.close();//关闭连接  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }
	}
	
	 public void Insert(int bug_id,int time,int flag,String reporter) throws SQLException{
			sql = "insert into "+table[flag]+" set BUG_ID = "+bug_id+",TIME = "+time+",REPORTER = \'"+reporter+"\'";//SQL语句  
	        System.out.println(sql);
	        pst = conn.prepareStatement(sql);//准备执行语句 
	        try {  
	            retu = pst.executeUpdate(sql); 
	        } catch (SQLException e){  
	            e.printStackTrace();  
	        }
		}
	 
	 public boolean Update(int bud_id,int time,int flag,int tag,int item){
			sql = "update   set  where ";//SQL语句  
//			db = new DBHelper(sql);//创建DBHelper对象  
	        try {  
	            retu = db.pst.executeUpdate(sql);
	           if(retu>0){ 
	               return true;
	            }//显示数据  
	            db.close();//关闭连接  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }
	        return false;
		}
	 
	 public boolean Delete(){
			sql = "delete from  where ";//SQL语句  
//	        db = new DBHelper(sql);//创建DBHelper对象  
	        try {  
	            retu = db.pst.executeUpdate(sql);
	           if(retu>0){ 
	               return true;
	            }//显示数据  
	            db.close();//关闭连接  
	        } catch (SQLException e){  
	            return false;  
	        }
	        return false;
		}
	 
}
