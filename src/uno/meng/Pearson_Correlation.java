package uno.meng;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uno.meng.db.CUID;

/**
 *         皮尔逊相关度评价
 */
public class Pearson_Correlation {
	private Map<String, Map<String, Double>> dataset = null;
	static CUID cuid = new CUID();
    static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
	static String mark[] = {"trivial","minor","normal","major","critical","blocker"};
	public Pearson_Correlation(int[][] Matrix) {
		initDataSet(Matrix);
	}
	/**
	 * 初始化数据集
	 */
	private void initDataSet(int[][] Matrix) {
		dataset = new HashMap<String, Map<String, Double>>();
		// 初始化Mac数据集
		Map<String, Double> Mac = new HashMap<String, Double>();
		for (int i = 0; i < 6; i++) {
			Mac.put(mark[i],(double) Matrix[0][i]);
		}
		dataset.put("Mac", Mac);
		// 初始化Linux数据集
		Map<String, Double> Linux = new HashMap<String, Double>();
		for (int i = 0; i < 6; i++) {
			Linux.put(mark[i],(double) Matrix[1][i]);
		}
		dataset.put("Linux", Linux);
		// 初始化Windows数据集
		Map<String, Double> Windows = new HashMap<String, Double>();
		for (int i = 0; i < 6; i++) {
			Windows.put(mark[i],(double) Matrix[2][i]);
		}
		dataset.put("Windows", Windows);
	}
	public Map<String, Map<String, Double>> getDataSet() {
		return dataset;
	}
	/*
	 * @return 皮尔逊相关度值
	 */
	public double sim_pearson(String parameter1, String parameter2) {
		List<String> list = new ArrayList<String>();
		for (Entry<String, Double> p1 : dataset.get(parameter1).entrySet()) {
			if (dataset.get(parameter2).containsKey(p1.getKey())) {
				list.add(p1.getKey());
			}
		}
		double sumX = 0.0;
		double sumY = 0.0;
		double sumX_Sq = 0.0;
		double sumY_Sq = 0.0;
		double sumXY = 0.0;
		int N = list.size();
		for (String name : list) {
			Map<String, Double> p1Map = dataset.get(parameter1);
			Map<String, Double> p2Map = dataset.get(parameter2);
			sumX += p1Map.get(name);
			sumY += p2Map.get(name);
			sumX_Sq += Math.pow(p1Map.get(name), 2);
			sumY_Sq += Math.pow(p2Map.get(name), 2);
			sumXY += p1Map.get(name) * p2Map.get(name);
		}
		double numerator = sumXY - sumX * sumY / N;
		double denominator = Math.sqrt((sumX_Sq - sumX * sumX / N)
				* (sumY_Sq - sumY * sumY / N));
		// 分母不能为0
		if (denominator == 0) {
			return 0;
		}
		return numerator / denominator;
	}
	public static void main(String[] args) throws SQLException {
		for(int i =0;i<4;i++){
			System.out.println("对 "+table[i]+" 部分分析结果： ");
			Pearson_Correlation pearsonCorrelationScore = new Pearson_Correlation(cuid.SearchOS(i));
			System.out.println("Mac OS 与 Linux 相关性："+pearsonCorrelationScore.sim_pearson("Mac","Linux"));
			System.out.println("Mac OS 与 Windows 相关性："+pearsonCorrelationScore.sim_pearson("Mac","Windows"));
			System.out.println("Windows 与 Linux 相关性："+pearsonCorrelationScore.sim_pearson("Windows","Linux"));
		}
	}
}

