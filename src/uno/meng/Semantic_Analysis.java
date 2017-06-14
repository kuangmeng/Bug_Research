package uno.meng;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import uno.meng.db.CUID;

public class Semantic_Analysis {
	static List<String> lists=new ArrayList<String>();
	static ResultSet rs = null;
    static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
	static POSModel model = new POSModelLoader()	.load(new File("en-pos-maxent.bin"));
	static POSTaggerME tagger = new POSTaggerME(model); 
	public static void main(String[] args) throws Exception{ 
		CUID cuid = new CUID();
		int num[]={0,0,0,0};
		int count[]={0,0,0,0};
		for(int i=0;i<4;i++){//"CDT","JDT","PDE","Platform"
			rs = null;
			rs = cuid.SearchDESC(i);
			while(rs.next()){
				num[i]++;
				String line = rs.getString(1);
				if(line == null || line.equals(null)){
					count[i]++;
					continue;
				}else{
					lists = null;
					lists = POSTag(line);
					String severity = rs.getString(2);
					if(Judge(lists,severity)){
						count[i]++;
					}
				}
			}
		}
		for(int i =0;i<4;i++){
			System.out.println("对 "+table[i]+" 部分分析结果： 准确率"+(count[i]+0.0)/num[i]);
		}
	}
	public static List<String> POSTag(String line) throws IOException {
		List<String> list = new ArrayList<String>();
		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
		String[] tags = tagger.tag(whitespaceTokenizerLine);
		for(int i=0;i<tags.length;i++){
			if(tags[i].equals("VBG") || tags[i].equals("MD") || tags[i].equals("NN") || tags[i].equals("NNS") || tags[i].equals("VBZ") || tags[i].equals("RB") || tags[i].equals("JJ")){
				list.add(whitespaceTokenizerLine[i]);
			}
		}
		return list;
	}
	public static boolean Judge(List<String> list,String severity){
		String judge[] = {"reliable Error compare should","empty very no not symbolic Common confuses so ClassCastException same assertion failures indexer ","Disassembly missing incorrectly overflow"};
		String mark[] = {"trivial","minor","normal","major","critical","blocker"};
		if(severity == null || severity.equals("") || list == null || list.isEmpty()){
			return true;
		}
		for(int i = 0;i< list.size();i++){
			for(int j = 0;j < 3;j++){
				if(judge[j].contains(list.get(i))){
					switch(j){
						case 0:
							if(severity.equals(mark[0]) || severity.equals(mark[1])){
								return true;
							}
							break;
						case 1:
							if(severity.equals(mark[2]) || severity.equals(mark[3])){
								return true;
							}
							break;
						case 2:
							if(severity.equals(mark[4]) || severity.equals(mark[5])){
								return true;
							}
							break;
						default:
								break;
					}
				}
			}
		}
		return false;
	}
}
