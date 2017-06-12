package uno.meng;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import uno.meng.db.CUID;

public class Polynomial_Curve_Fitter {
    	static ResultSet rs = null;
    	static int count = 3;
    static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
	public static void main(String[] args) throws SQLException {
		CUID cuid = new CUID();
		List<double[]> li = new ArrayList<double[]>();
		int times[] = {0,0,0,0};
		for(int i =0;i<4;i++){
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
