package uno.meng;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import uno.meng.db.*;
public class K_means extends JFrame{
	private static final long serialVersionUID = 1L;
	static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
	private int k;// 分成多少簇
	private int m;// 迭代次数
	static MyPanel mp = null;
	static int flag = -1;
	static double[][] graph = new double[3][2];
	private int dataSetLength;// 数据集元素个数，即数据集的长度
	private ArrayList<float[]> dataSet;// 数据集链表
	private ArrayList<float[]> center;// 中心链表
	private ArrayList<ArrayList<float[]>> cluster; // 簇
	private ArrayList<Float> jc;// 误差平方和，k越接近dataSetLength，误差越小
	private Random random;
	static CUID cuid = new CUID();
	static ResultSet rs = null;
	/**
	 * 设置需分组的原始数据集
	 */
	public void setDataSet(ArrayList<float[]> dataSet) {
		this.dataSet = dataSet;
	}
	/**
	 * 获取结果分组
	 */
	public ArrayList<ArrayList<float[]>> getCluster() {
		return cluster;
	}
	/**
	 * 构造函数，传入需要分成的簇数量
	 */
	public K_means(int k) {
		this.k = k;
	}
	/**
	 * 初始化
	 * @throws SQLException 
	 */
	private void init() throws SQLException {
		m = 0;
		random = new Random();
		initDataSet(rs);
		dataSetLength = dataSet.size();
		if (k > dataSetLength) {
			k = dataSetLength;
		}
		center = initCenters();
		cluster = initCluster();
		jc = new ArrayList<Float>();
	}
	/**
	 * 如果调用者未初始化数据集，则采用内部测试数据集
	 * @throws SQLException 
	 */
	private void initDataSet(ResultSet rs) throws SQLException {
		dataSet = new ArrayList<float[]>();
		while(rs.next()){
			if(rs.getString(1) == null || rs.getString(2) == null || rs.getString(1).equals("") || rs.getString(2).equals("")){
				continue;
			}else{
				float first = 0;
				float last = 0;
				switch(rs.getString(1)){
					case "P1":
						first = 1;
						break;
					case "P2":
						first = 2;
						break;
					case "P3":
						first = 3;
						break;
					case "P4":
						first = 4;
						break;
					case "P5":
						first = 5;
						break;
					default:
						break;
				}
				switch(rs.getString(2)){
				case "trivial":
					last = 1;
					break;
				case "minor":
					last = 2;
					break;
				case "normal":
					last = 3;
					break;
				case "major":
					last = 4;
					break;
				case "critical":
					last = 5;
					break;
				case "blocker":
					last = 6;
					break;
				default:
					break;
				}
				float[] data = new float[]{first,last};
				dataSet.add(data);
			}
		}
	}
	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点
	 */
	private ArrayList<float[]> initCenters() {
		ArrayList<float[]> center = new ArrayList<float[]>();
		int[] randoms = new int[k];
		boolean flag;
		int temp = random.nextInt(dataSetLength);
		randoms[0] = temp;
		for (int i = 1; i < k; i++) {
			flag = true;
			while (flag) {
				temp = random.nextInt(dataSetLength);
				int j = 0;
				while (j < i) {
					if (temp == randoms[j]) {
						break;
					}
					j++;
				}
				if (j == i) {
					flag = false;
				}
			}
			randoms[i] = temp;
		}
		for (int i = 0; i < k; i++) {
			center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
		}
		return center;
	}
	/**
	 * 初始化簇集合
	 */
	private ArrayList<ArrayList<float[]>> initCluster() {
		ArrayList<ArrayList<float[]>> cluster = new ArrayList<ArrayList<float[]>>();
		for (int i = 0; i < k; i++) {
			cluster.add(new ArrayList<float[]>());
		}
		return cluster;
	}
	/**
	 * 计算两个点之间的距离
	 */
	private float distance(float[] element, float[] center) {
		float distance = 0.0f;
		float x = element[0] - center[0];
		float y = element[1] - center[1];
		float z = x * x + y * y;
		distance = (float) Math.sqrt(z);
		return distance;
	}
	/**
	 * 获取距离集合中最小距离的位置
	 */
	private int minDistance(float[] distance) {
		float minDistance = distance[0];
		int minLocation = 0;
		for (int i = 1; i < distance.length; i++) {
			if (distance[i] < minDistance) {
				minDistance = distance[i];
				minLocation = i;
			} else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
			{
				if (random.nextInt(10) < 5) {
					minLocation = i;
				}
			}
		}
		return minLocation;
	}
	/**
	 * 核心，将当前元素放到最小距离中心相关的簇中
	 */
	private void clusterSet() {
		float[] distance = new float[k];
		for (int i = 0; i < dataSetLength; i++) {
			for (int j = 0; j < k; j++) {
				distance[j] = distance(dataSet.get(i), center.get(j));
			}
			int minLocation = minDistance(distance);
			cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中
		}
	}
	/**
	 * 求两点误差平方的方法
	 */
	private float errorSquare(float[] element, float[] center) {
		float x = element[0] - center[0];
		float y = element[1] - center[1];
		float errSquare = x * x + y * y;
		return errSquare;
	}
	/**
	 * 计算误差平方和准则函数方法
	 */
	private void countRule() {
		float jcF = 0;
		for (int i = 0; i < cluster.size(); i++) {
			for (int j = 0; j < cluster.get(i).size(); j++) {
				jcF += errorSquare(cluster.get(i).get(j), center.get(i));
			}
		}
		jc.add(jcF);
	}
	/**
	 * 设置新的簇中心方法
	 */
	private void setNewCenter() {
		for (int i = 0; i < k; i++) {
			int n = cluster.get(i).size();
			if (n != 0) {
				float[] newCenter = { 0, 0 };
				for (int j = 0; j < n; j++) {
					newCenter[0] += cluster.get(i).get(j)[0];
					newCenter[1] += cluster.get(i).get(j)[1];
				}
				// 设置一个平均值
				newCenter[0] = newCenter[0] / n;
				newCenter[1] = newCenter[1] / n;
				center.set(i, newCenter);
			}
		}
	}
	public void printDataArray(ArrayList<float[]> dataArray,
		String dataArrayName) {
		for (int i = 0; i < dataArray.size(); i++) {
			System.out.println(dataArrayName + "[" + i + "]={"
					+ dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}");
			graph[i][0] =  dataArray.get(i)[0];
			graph[i][1] = dataArray.get(i)[1];
		}
		System.out.println("===================================");
	}
	/**
	 * Kmeans算法核心过程方法
	 * @throws SQLException 
	 */
	private void kmeans() throws SQLException {
		init();
		// 循环分组，直到误差不变为止
		while (true) {
			clusterSet();
			countRule();
			// 误差不变了，分组完成
			if (m != 0) {
				if (jc.get(m) - jc.get(m - 1) == 0) {
					draw(graph,flag);
					break;
				}
			}
			setNewCenter();
			printDataArray(center,"新聚类中心");
			m++;
			cluster.clear();
			cluster = initCluster();
		}
		System.out.println("迭代次数："+m);//输出迭代次数
	}
	public void draw(double[][] graph,int flag){
		mp = new MyPanel(graph);  
        this.add(mp);  
        this.setSize(500,500);  
        this.setVisible(true);  
        this.setTitle(table[flag]+"聚类结果");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	}
	/**
	 * 执行算法
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException{
		for(int i =0;i<4;i++){
			flag = i;
			System.out.println("对 "+table[i]+" 部分分析结果： ");
			rs = null;
			rs = cuid.SearchSeverity(i);
			K_means k = new K_means(3);
			k.kmeans();
			System.out.println("*******************************\n\n*******************************");
		}
	}
}

class MyPanel extends JPanel{  
	private static final long serialVersionUID = 1L;
	double[][] graph;
	public MyPanel(double[][] graph){
		this.graph = graph;
	}
    //覆盖JPanel的paint方法  
    //Graphics是绘图的重要类，可以理解成一支画笔  
    public void paint(Graphics g){  
        //1.调用父类函数完成初始化  
        super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		int x = 100;
		int y = 100;
		int[] pox ={ 90, 100, 100 };
		int[] poy ={ 110, 90, 100 };
		int[] poxx ={110, 100, 100 };
		int[] poyy ={ 110, 90, 100 };
		int[] poxB = {387,397,407};
		int[] poyB = {390,400,400};
		int[] poxBB = {387,397,407};
		int[] poyBB = {410,400,400};
		g2d.setColor(Color.black);
		g2d.fillRect(99, 100, 2, 300);
		g2d.fillRect(99, 400, 300, 2);
		g2d.fillRect(82, 402, 15, 15);
		g2d.setColor(Color.blue);
		g2d.fillRect((int)Math.round(100+30*graph[0][0]),(int)Math.round(400-30*graph[0][1]),5,5);
		g2d.setColor(Color.green);
		g2d.fillRect((int)Math.round(100+30*graph[1][0]),(int)Math.round(400-30*graph[1][1]),5,5);
		g2d.setColor(Color.red);
		g2d.fillRect((int)Math.round(100+30*graph[2][0]),(int)Math.round(400-30*graph[2][1]),5,5);
		g2d.setColor(Color.black);
		for (int i = 0; i < 13; i++){
			g2d.drawLine(100 + i * 15, 300 + y, y + i * 15, y + 120);
			g2d.drawLine(100, 300 + y - i * 15, y + 180, y + 300 - i * 15);
			if (i % 2 == 0 && i / 2 != 0){
				g2d.drawString(String.valueOf(i / 2), x - 20, 405 - i / 2 * 30);
				g2d.drawString(String.valueOf(i / 2), x - 5 + i / 2 * 30, 420);
			}
		}
		g2d.setColor(Color.white);
		g2d.drawString("0", 85, 415);
		g2d.setColor(Color.black);
		g2d.drawString("Y", 80, 140);
		g2d.drawString("X", 420, 420);
		g2d.fillPolygon(pox,poy,3);
		g2d.fillPolygon(poxx,poyy,3);
		g2d.fillPolygon(poxB,poyB,3);
		g2d.fillPolygon(poxBB,poyBB,3);
		g2d.dispose();
    }  
}  

