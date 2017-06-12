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
    String[] area = new String[]{"MANAGER","STATUS","COMPONENT","OS","PRIORITY","PRODUCT","RESOLUTION","SEVERITY","DESC","VERSION"};
    public CUID(){
    		conn = db.open();
    }
	public boolean Search(int flag,int bug_id,String when) throws SQLException{
		sql = "select * from "+table[flag]+" where BUG_ID = "+bug_id+" and TIME = "+Integer.parseInt(when);//SQL语句  
        System.out.println(sql);
		pst = conn.prepareStatement(sql);//准备执行语句
		try {  
            ret = pst.executeQuery();//执行语句，得到结果集  
            while (ret.next()){  
            		return true;
            }//显示数据  
        } catch (SQLException e){
        	System.out.println("Search 出错！");
           return false;
        }
        return false;
	}
	public boolean SearchCC(int flag,int bug_id,String when,String what) throws SQLException{
		sql = "select * from "+table[flag]+"_Extra where BUG_ID = "+bug_id+" and TIME = "+Integer.parseInt(when)+" and PRISON = \'"+what+"\'";//SQL语句  
        System.out.println(sql);
		pst = conn.prepareStatement(sql);//准备执行语句
		try{  
            ret = pst.executeQuery();//执行语句，得到结果集  
            while (ret.next()){  
            		return true;
            }//显示数据  
        } catch (SQLException e) {  
        	System.out.println("SearchCC 出错！");
           return false;
        }
        return false;
	}
	 public void InsertOthers(int bug_id,int flag,int tag,String when,String what) throws SQLException{
		    if(Search(flag,bug_id,when)){
		      	sql = "update "+table[flag]+"  set `"+area[tag]+"`=\'"+what+"\' where BUG_ID = "+bug_id+" and TIME = "+Integer.parseInt(when);//SQL语句  
		        System.out.println(sql); 
		      	pst = conn.prepareStatement(sql);//准备执行语句 
		      	try {  
		      		retu = pst.executeUpdate(sql); 
		        } catch (SQLException e) {  
		        		System.out.println("InsertOthers Update 出错！");
		        }
		    }else{
		    	sql = "insert into "+table[flag]+" set BUG_ID = "+bug_id+",TIME = "+Integer.parseInt(when)+",`"+area[tag]+"`= \'"+what+"\'";//SQL语句  
		        System.out.println(sql);
		        pst = conn.prepareStatement(sql);//准备执行语句 
		        try {  
		            retu = pst.executeUpdate(sql); 
		        } catch (SQLException e){  
		        		System.out.println("InsertOthers Insert 出错！");
		        }
		    }
		}
	 
	 public void InsertCC(int bug_id,int flag,String when,String what) throws SQLException{
		 if(what.contains(",")){
			 String[] items = what.split("\\,");
			 for(int i = 0;i<items.length;i++){
				 if(!SearchCC(flag,bug_id,when,items[i])){
					 sql = "insert into "+table[flag]+"_Extra set BUG_ID = "+bug_id+",TIME = "+Integer.parseInt(when)+",PRISON = \'"+items[i]+"\'";//SQL语句  
					 System.out.println(sql);
					 pst = conn.prepareStatement(sql);//准备执行语句 
					 try {  
						 retu = pst.executeUpdate(sql); 
					 } catch (SQLException e){  
				        	System.out.println("InsertCC many 出错！");
					 }
				 }
			 }
		 }else{
			 if(!SearchCC(flag,bug_id,when,what)){
				 sql = "insert into "+table[flag]+"_Extra set BUG_ID = "+bug_id+",TIME = "+Integer.parseInt(when)+",PRISON = \'"+what+"\'";//SQL语句  
				 System.out.println(sql);
				 pst = conn.prepareStatement(sql);//准备执行语句 
				 try {  
					 retu = pst.executeUpdate(sql); 
				 } catch (SQLException e){  
			        	System.out.println("InsertCC one 出错！");
				 }
			 }
		 }
	}
	 
	 public void Insert(int bug_id,int time,int flag,String reporter) throws SQLException{
		   if(Search(flag,bug_id,String.valueOf(time))){
		      	sql = "update "+table[flag]+"  set REPORTER  =\'"+reporter+"\' where BUG_ID = "+bug_id+" and TIME = "+time;//SQL语句  
		        System.out.println(sql);
		      	pst = conn.prepareStatement(sql);//准备执行语句 
		      	try {  
		      		retu = pst.executeUpdate(sql); 
		        } catch (SQLException e) {  
		        	System.out.println("Insert Update 出错！");
		        }
		    }else{
			sql = "insert into "+table[flag]+" set BUG_ID = "+bug_id+",TIME = "+time+",REPORTER = \'"+reporter+"\'";//SQL语句  
	        System.out.println(sql);
	        pst = conn.prepareStatement(sql);//准备执行语句 
	        try {  
	            retu = pst.executeUpdate(sql); 
	        } catch (SQLException e){  
	        		System.out.println("Insert Insert 出错！");
	        }
		}	 
	 }
	 public ResultSet SearchDESC(int flag) throws SQLException{
		 sql = "select `DESC`,`SEVERITY` from "+table[flag];//SQL语句  
	    // System.out.println(sql);
		pst = conn.prepareStatement(sql);//准备执行语句
		try{  
			ret = pst.executeQuery();//执行语句，得到结果集  
	         while (ret.next()){  
	           		return ret;
	            }//显示数据  
	    } catch (SQLException e) {  
	        	System.out.println("SearchDESC 出错！");
	        	return null;
	    }
        return null;
	 }
	 public ResultSet SearchTime(int flag) throws SQLException{
		 sql = "select `TIME` from "+table[flag] +" where `STATUS` = \'NEW\'";//SQL语句  
		pst = conn.prepareStatement(sql);//准备执行语句
		try{  
			ret = pst.executeQuery();//执行语句，得到结果集  
	         while (ret.next()){  
	           		return ret;
	            }//显示数据  
	    } catch (SQLException e) {  
	        	System.out.println("SearchTime 出错！");
	        	return null;
	    }
        return null;
	 }
	 
	 public ResultSet SearchSeverity(int flag) throws SQLException{
		 sql = "select `PRIORITY`,`SEVERITY` from "+table[flag];//SQL语句  
		pst = conn.prepareStatement(sql);//准备执行语句
		try{  
			ret = pst.executeQuery();//执行语句，得到结果集  
	         while (ret.next()){  
	           		return ret;
	            }//显示数据  
	    } catch (SQLException e) {  
	        	System.out.println("SearchTime 出错！");
	        	return null;
	    }
        return null;
	 }
}
