package uno.meng;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import uno.meng.db.CUID;
import uno.meng.lda.LdaGibbs;
import uno.meng.lda.LdaUtil;
import uno.meng.lda.Vocabulary;

/**
 * 语料库，也就是文档集合
 */
public class LDA{
    List<int[]> documentList;
    Vocabulary vocabulary;
    static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
    public LDA(){
        documentList = new LinkedList<int[]>();
        vocabulary = new Vocabulary();
    }
    public int[] addDocument(List<String> document){
        int[] doc = new int[document.size()];
        int i = 0;
        for (String word : document){
            doc[i++] = vocabulary.getId(word, true);
        }
        documentList.add(doc);
        return doc;
    }
    public int[][] toArray(){
        return documentList.toArray(new int[0][]);
    }
    public int getVocabularySize(){
        return vocabulary.size();
    }
    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        for (int[] doc : documentList){
            sb.append(Arrays.toString(doc)).append("\n");
        }
        sb.append(vocabulary);
        return sb.toString();
    }
    public static LDA load(String folderPath) throws IOException{
        LDA lDA = new LDA();
        File folder = new File(folderPath);
        for (File file : folder.listFiles()){
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            List<String> wordList = new LinkedList<String>();
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    if (word.trim().length() < 2) continue;
                    wordList.add(word);
                }
            }
            br.close();
            lDA.addDocument(wordList);
        }
        if (lDA.getVocabularySize() == 0) return null;
        return lDA;
    }
    public Vocabulary getVocabulary(){
        return vocabulary;
    }
    public int[][] getDocument() {
        return toArray();
    }
    public static int[] loadDocument(String path, Vocabulary vocabulary) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        List<Integer> wordList = new LinkedList<Integer>();
        while ((line = br.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                if (word.trim().length() < 2) continue;
                Integer id = vocabulary.getId(word);
                if (id != null)
                    wordList.add(id);
            }
        }
        br.close();
        int[] result = new int[wordList.size()];
        int i = 0;
        for (Integer integer : wordList){
            result[i++] = integer;
        }
        return result;
    }
    public static void main(String[] args) throws IOException, SQLException{
    		CUID cuid = new CUID();
    		ResultSet rs = null;
    		for(int i = 0;i<4;i++){
    			rs = null;
        		rs = cuid.SearchDESC(i);
        		FileWriter fw = new FileWriter("data/desc/"+table[i]+".txt");   
             while(rs.next()){   
            	       if(rs.getString(1) == null){
            	    	   		continue;
            	       }else{
            	    	   		fw.write(rs.getString(1)+"\r\n");  
            	       }
                }   
             fw.close();   
    		}
        // 1. Load corpus from disk
        LDA lDA = LDA.load("data/desc");
        // 2. Create a LDA sampler
        LdaGibbs ldaGibbs = new LdaGibbs(lDA.getDocument(), lDA.getVocabularySize());
        // 3. Train it
        ldaGibbs.gibbs(10);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbs.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, lDA.getVocabulary(), 10);
        LdaUtil.explain(topicMap);
    }
}
