package uno.meng.db;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	 public List<int[]> SearchD(int flag) throws SQLException{
		 sql = "select `TIME`,`BUG_ID` from "+table[flag]+" WHERE STATUS = \'NEW\'";//SQL语句
		pst = conn.prepareStatement(sql);//准备执行语句
		List<int[]> list = new ArrayList<int[]>();
		try{  
			ret = pst.executeQuery();//执行语句，得到结果集  
	         while (ret.next()){  
	    		 	String sql2 = "select `TIME` from "+table[flag]+" WHERE STATUS is NULL and `BUG_ID` = "+ret.getInt(2);//SQL语句
	    		 	pst = conn.prepareStatement(sql2);//准备执行语句
	    		 	ResultSet rets = pst.executeQuery();//执行语句，得到结果集
	    		 	int fp = 0;
	    		 	while(rets.next()){
	    		 		fp = rets.getInt(1);
	    		 	}
	    			 String sql3 = "select `TIME` from "+table[flag]+" WHERE STATUS = \'RESOLVED\' and `BUG_ID` = "+ret.getInt(2);//SQL语句
	    			 pst = conn.prepareStatement(sql3);//准备执行语句  
	    			 ResultSet retss = pst.executeQuery();//执行语句，得到结果集
	    			 int jj = 0;
	    			 while(retss.next()){
	    				 jj = retss.getInt(1);
	    			 }
	    			 if(fp - ret.getInt(1) > 0 && jj - fp >0){
	    				 list.add(new int[]{fp-ret.getInt(1),jj-fp});
	    			 }
	         }//显示数据  
	         return list;
	    } catch (SQLException e) {  
	        	System.out.println("SearchD 出错！");
	        	return null;
	    }
	 }
	 
	 public int[][] SearchOS(int flag) throws SQLException{
		 sql = "select `OS`,`SEVERITY` from "+table[flag];//SQL语句  
		pst = conn.prepareStatement(sql);//准备执行语句
		try{  
			ret = pst.executeQuery();//执行语句，得到结果集  
			int[][] Matrix = new int[3][6]; 
			for(int i = 0;i<3;i++){
				for(int j =0;j<6;j++){
					Matrix[i][j] = 0;
				}
			}
	         while (ret.next()){  
	           		int col = -1,row = -1;
	           		if(ret.getString(2) ==  null || ret.getString(2).equals("") || ret.getString(1) ==  null || ret.getString(1).equals("")){
	           			continue;
	           		}
	           		switch(ret.getString(2)){
					case "trivial":
						col = 0;
						break;
					case "minor":
						col = 1;
						break;
					case "normal":
						col = 2;
						break;
					case "major":
						col = 3;
						break;
					case "critical":
						col = 4;
						break;
					case "blocker":
						col = 5;
						break;
					default:
						break;
					}
	           		if(ret.getString(1).contains("Mac") || ret.getString(1).contains("All")){
	           			row = 0;
	           		}else if(ret.getString(1).contains("Linux") || ret.getString(1).contains("All")){
	           			row = 1;
	           		}else if(ret.getString(1).contains("Windows") || ret.getString(1).contains("All")){
	           			row = 2;
	           		}
	           		if(row >= 0 && col >= 0){
	           			Matrix[row][col]+=1;
	           		}
	            }//显示数据 
	         for(int i=0;i<3;i++){
	        	 	for(int j=0;j<6;j++){
	        	 		System.out.print(Matrix[i][j]+"\t");
	        	 	}
	        	 System.out.println();
	         }
	         return Matrix;
	    } catch (SQLException e) {  
	        	System.out.println("SearchOS 出错！");
	        	return null;
	    }
	 }
	 public boolean SearchReOpened(int flag) throws SQLException, IOException{
		 sql = "select `DESC` from "+table[flag]+" where `STATUS` = \'REOPENED\'";//SQL语句  
		pst = conn.prepareStatement(sql);//准备执行语句
		try{  
			ret = pst.executeQuery();//执行语句，得到结果集 
       	    FileWriter fw = new FileWriter("data/desc_re/"+table[flag]+"_reopened.txt");   
	         while (ret.next()){  
	            	   if(ret.getString(1) == null){
	            	    	   		continue;
	            	       }else{
	            	    	   		fw.write(ret.getString(1)+"\r\n");  
	            	       }   
	            } 
             fw.close();   
	         return true;
	    } catch (SQLException e) {  
	        	System.out.println("SearchReOpened 出错！");
	        	return false;
	    }
	 }
}
