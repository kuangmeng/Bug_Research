package uno.meng;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import uno.meng.db.CUID;

public class Polynomial_Curve_Fitter  extends JFrame{
	private static final long serialVersionUID = 1L;
	static ResultSet rs = null;
    	static int count = 3;
    	static MyPanel2 mp2 = null;
    	static int flag = -1;
    	static CUID cuid = new CUID();
	static List<double[]> li = new ArrayList<double[]>();
    static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
	public void draw(int flag,double[] coeff,int times,int idx){
			mp2 = new MyPanel2(coeff,times,idx);  
			this.add(mp2);  
		    this.setSize(1500,500);  
	        this.setVisible(true);  
	        this.setTitle(table[flag]+"曲线拟合结果");
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	}
	
	public static void main(String[] args) throws SQLException {
		int times[] = {0,0,0,0};
		for(int i =0;i<4;i++){
		//	Polynomial_Curve_Fitter p = new Polynomial_Curve_Fitter();
			rs = null;
			rs = cuid.SearchTime(i);
			double a[] = new double[65535],b[] = new double[65535];
			int idx = 0;
			while(rs.next()){
				a[idx] = idx;
				b[idx] = Double.parseDouble(rs.getString(1));
				idx++;
			}
			times[i] = idx;
			List<Double[]> set = new ArrayList<Double[]>();
			for(int j=0;j<idx;j++){
				Double[] tmp = {a[j],b[j]};
				set.add(tmp);
			}
			int num = count;
			double coeff[]= detrend(set,num);
		//	p.draw(i,coeff,count,idx);
			li.add(coeff);
		}
		for(int i=0;i<4;i++){
			int num =count;
			System.out.print("对"+table[i]+"拟合结果为：TIME = ");
			for(double c : li.get(i)){
				if(num>0){
					System.out.print(c + " X^"+(num--)+"+");
				}else{
					System.out.print(c);
				}
			}    
			System.out.println("\n**最近的10次BUG 预测时间为：");
			for(int k=times[i];k<times[i]+10;k++){
				double result = k;
				for(int l = 0;l<count;l++){
					result = result*(li.get(i)[l]+1);
				}
				result+=li.get(i)[count];
				System.out.println("第"+k+"号BUG预测时间："+result);
			}
		}
	}
	public static double[] detrend(List<Double[]> set,int detrendPolynomial){
		  PolynomialCurveFitter p=PolynomialCurveFitter.create(detrendPolynomial);
		  WeightedObservedPoints wop=new WeightedObservedPoints();
		  for (int i=0; i < set.size(); i++) {
		    wop.add(set.get(i)[0],set.get(i)[1]);
		  }
		  double[] coeff=p.fit(wop.toList());
		  for (int h=0; h < set.size(); h++) {
		    double val=set.get(h)[0];
		    double off=0;
		    for (int i=detrendPolynomial; i >= 0; i--) {
		      off+=coeff[i] * Math.pow(val,i);
		    }
		    set.set(h,new Double[]{set.get(h)[0],set.get(h)[1] - off});
		  }
		  return coeff;
		}
}
class MyPanel2 extends JPanel{  
	private static final long serialVersionUID = 1L;
	private static final double WIDTH = 1350;  
    private static final double HEIGHT = 500;  
    private static final int INCREMENT = 20;  
    double[] coeff;
    int count;
    int idx;
	public MyPanel2(double[] coeff,int times,int idx){
		this.coeff = coeff;
		this.count = times;
		this.idx = idx;
	}
    //覆盖JPanel的paint方法  
    //Graphics是绘图的重要类，可以理解成一支画笔  
    public void paint(Graphics g){  
    		super.paint(g);  
         Graphics2D g2d = (Graphics2D) g;  
         g2d.setColor(Color.black);             
         // 画 X 轴  
         g2d.drawLine(INCREMENT, (int)HEIGHT-50, (int)WIDTH-INCREMENT, (int)HEIGHT-50);  
         g2d.drawLine((int)WIDTH-INCREMENT, (int)HEIGHT-50, (int)WIDTH-30, (int)HEIGHT-55);  
         g2d.drawLine((int)WIDTH-INCREMENT, (int)HEIGHT-50, (int)WIDTH-30, (int)HEIGHT-45);  
         // 画 Y 轴  
         g2d.drawLine((int)INCREMENT, 40, (int)20, (int)HEIGHT-50);  
         g2d.drawLine((int)INCREMENT, 40, (int)10, 50);  
         g2d.drawLine((int)INCREMENT, 40, (int)30, 50);  
         // 将当前画笔移动到中心  
         g2d.translate((int) WIDTH / 2, (int) HEIGHT / 2);  
         // 利用GeneralPath类来画曲线  
         GeneralPath gp = new GeneralPath();  
         // 将GeneralPath的实例gp的画笔移动到当前画面的中心，但是这个点是相对于g2d画笔的中心的  
         gp.moveTo(0, 0);     
         // sin(x)的图像  
         draw(gp, g2d,coeff,count,idx);  
    }  
    private void draw(GeneralPath gp, Graphics2D g2d,double[] coeff,int count,int idx) {  
        for (double i = 0.00001; i <=idx; i+=idx/1000){  
        	    double result = i;
			for(int l = 0;l<count;l++){
				result = result*(coeff[l]+1);
			}
			result+= coeff[count];
			//result /= 1000;
             gp.lineTo(20*(i/1000), -result);  
            }  
        g2d.draw(gp);  
    }  
}  

