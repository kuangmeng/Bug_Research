package uno.meng;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;  
import java.util.List;  
import org.dom4j.Attribute;  
import org.dom4j.Document;  
import org.dom4j.Element;  
import org.dom4j.io.SAXReader;

import uno.meng.db.CUID;  
public class Eclipse_Bug_Report{  
	static List<File> filelist = new ArrayList<File>();
    static CUID cuid = new CUID();
	public static void main(String[] args) throws Exception{  
        // 创建saxReader对象  
        SAXReader reader = new SAXReader(); 
        List<File> list = getFileList("data"); 
        for(int i = 0;i< list.size();i++){
        	    int flag = 4;
        	    if(list.get(i).getAbsolutePath().contains("CDT")){
        	    		flag = 0;
        	    }else if(list.get(i).getAbsolutePath().contains("JDT")){
        	    		flag = 1;
        	    }else if(list.get(i).getAbsolutePath().contains("PDE")){
        	    		flag = 2;
        	    }else if(list.get(i).getAbsolutePath().contains("Platform")){
        	    		flag = 3;
        	    }
        	    int tag = -1;
        	    if(list.get(i).getAbsolutePath().contains("assigned_to")){
        	    		tag = 0;
        	    }else if(list.get(i).getAbsolutePath().contains("bug_status")){
        	    		tag = 1;
        	    }else if(list.get(i).getAbsolutePath().contains("component")){
        	    	    tag = 2;
        	    }else if(list.get(i).getAbsolutePath().contains("op_sys")){
        	    		tag = 3;
        	    }else if(list.get(i).getAbsolutePath().contains("priority")){
        	    		tag = 4;
        	    }else if(list.get(i).getAbsolutePath().contains("product")){
        	    		tag = 5;
        	    }else if(list.get(i).getAbsolutePath().contains("resolution")){
        	    		tag = 6;
        	    }else if(list.get(i).getAbsolutePath().contains("severity")){
        	    		tag = 7;
        	    }else if(list.get(i).getAbsolutePath().contains("short_desc")){
        	    		tag = 8;
        	    }else if(list.get(i).getAbsolutePath().contains("version")){
        	    		tag = 9;
        	    }
    	   	    // 通过read方法读取一个文件 转换成Document对象  
    	        Document document = reader.read(list.get(i));  
    	        //获取根节点元素对象  
    	        Element node = document.getRootElement();  
    	        if(node.getName().equals("reports")){
    	        	//	ReadReportsXML(node,flag);
    	        }else if(node.getName().equals("cc")){
    	        		
    	        }else{
    	        		//遍历所有的元素节点  
    	        		ReadXML(node,flag,tag);  
    	        }
        }
    }  
	 /**
     * 读取某个文件夹下的所有文件
     */
	public static List<File> getFileList(String strPath){
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileName.endsWith("xml")) { // 判断文件名是否以.avi结尾
                    filelist.add(files[i]);
                } else {
                    continue;
                }
            }
        }
        return filelist;
    }
	/** 
     * 遍历当前节点元素下面的所有(元素的)子节点 
     *  
     * @param node 
     */  
    public static void ReadXML(Element node,int flag,int tag){  
    	 // 当前节点下面子节点迭代器  
       Iterator<Element> it = node.elementIterator(); 
        // 获取当前节点的所有属性节点 
       while(it.hasNext()){
    	   Element n = it.next();
        if(n.getName().equals("report")){
        	    List<Attribute> list = n.attributes();  
             // 遍历属性节点  
             for (Attribute attr : list){  
            	 	if(attr.getName().equals("id")){
                        System.out.println("report\'s "+attr.getName() + "为：" + attr.getValue());  
            	 	}
             }  
             Iterator<Element> con = n.elementIterator();
             while(con.hasNext()){
         	 	Element e = con.next();
            	 	if(e.getName().equals("update")){
            	 		Iterator<Element> tmp = e.elementIterator();
            	 		while(tmp.hasNext()){
            	 			Element t = tmp.next();
            	 			System.out.println(t.getName()+"内容："+t.getText());
            	 		}
            	 	}
             }
        }
       }
    }  
//    
//    /** 
//     * 遍历reports.xml当前节点元素下面的所有(元素的)子节点 
//     *  
//     * @param node 
//     * @throws SQLException 
//     */  
//    public static void ReadReportsXML(Element node,int flag) throws SQLException{  
//    		int bug_id = 0;
//    		int time = 0;
//    		String reporter = null;
//    	 // 当前节点下面子节点迭代器  
//       Iterator<Element> it = node.elementIterator(); 
//        // 获取当前节点的所有属性节点 
//       while(it.hasNext()){
//    	   Element n = it.next();
//        if(n.getName().equals("report")){
//        	    List<Attribute> list = n.attributes();  
//             // 遍历属性节点  
//             for (Attribute attr : list){  
//            	 	if(attr.getName().equals("id")){
//                        System.out.println("report\'s "+attr.getName() + "为：" + attr.getValue());  
//                        bug_id = Integer.parseInt(attr.getValue());
//            	 	}
//             }  
//             Iterator<Element> con = n.elementIterator();
//             while(con.hasNext()){
//         	 	Element e = con.next();
//            	 	System.out.println(e.getName()+"内容为："+e.getText());
//            	 	if(e.getName().equals("opening_time")){
//            	 		time = Integer.parseInt(e.getText());
//            	 	}else if(e.getName().equals("reporter")){
//            	 		reporter = e.getText();
//            	 	}
//             }
//            cuid.Insert(bug_id,time,flag,reporter);
//        }
//       }
//    }  
    
}  