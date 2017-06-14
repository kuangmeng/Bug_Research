package uno.meng;

import java.math.BigDecimal;  
import java.util.ArrayList;  
  
public class Linear{  
    /** sum of x */  
    private double sumX;  
    /** sum of y */  
    private double sumY;  
    /** sum of x*x */  
    private double sumXX;  
    /** sum of x*y */  
    private double sumXY;  
    /** sum of y*y */  
    private double sumYY;  
    /** sum of sumDeltaY^2 */  
    private double sumDeltaY2;  
    private double sst;  
    private double E;  
    private String[] xy;  
    private ArrayList<String> listX;  
    private ArrayList<String> listY;  
    private int XMin, XMax, YMin, YMax;  
    /** line coefficient a0 */  
    private float a0;  
    /** line coefficient a1 */  
    private float a1;  
    /** number of data points */  
    private int pn;  
    /** true if coefficients valid */  
    private boolean coefsValid;  
    public Linear() {  
        XMax = 0;  
        YMax = 0;  
        pn = 0;  
        xy = new String[2];  
        listX = new ArrayList<String>();  
        listY = new ArrayList<String>();  
    }  
    public Linear(double[][] data,int num) {  
        pn = 0;  
        xy = new String[2];  
        listX = new ArrayList<String>();  
        listY = new ArrayList<String>();  
        for (int i = 0; i <num; ++i) {
        	    double[] datas = new double[]{data[0][i],data[1][i]};
            addDataPoint(datas);  
        }  
    }  
    public int getDataPointCount() {  
        return pn;  
    }  
    public float getA0() {  
        validateCoefficients();  
        return a0;  
    }  
    public float getA1() {  
        validateCoefficients();  
        return a1;  
    }  
    public double getSumX() {  
        return sumX;  
    }  

    public double getSumY() {  
        return sumY;  
    }  
    public double getSumXX() {  
        return sumXX;  
    }  
    public double getSumXY() {  
        return sumXY;  
    }  
    public double getSumYY() {  
        return sumYY;  
    }  
    public int getXMin() {  
        return XMin;  
    }  
    public int getXMax() {  
        return XMax;  
    }  
    public int getYMin() {  
        return YMin;  
    }  
    public int getYMax() {  
        return YMax;  
    }  
    public void addDataPoint(double[] dataPoint) {  
        sumX += dataPoint[0];  
        sumY += dataPoint[1];  
        sumXX += dataPoint[0] * dataPoint[0];  
        sumXY += dataPoint[0]* dataPoint[1];  
        sumYY += dataPoint[0] * dataPoint[1];  
  
        if (dataPoint[0] > XMax) {  
            XMax = (int) dataPoint[0];  
        }  
        if (dataPoint[1] > YMax) {  
            YMax = (int) dataPoint[1];  
        }  
  
        // 把每个点的具体坐标存入ArrayList中，备用  
  
        xy[0] = (int) dataPoint[0] + "";  
        xy[1] = (int) dataPoint[1] + "";  
        if (dataPoint[0] != 0 && dataPoint[1] != 0) {  
            try {  
                listX.add(pn, xy[0]);  
                listY.add(pn, xy[1]);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        ++pn;  
        coefsValid = false;  
    }  
    public float at(int x) {  
        if (pn < 2)  
            return Float.NaN;  
        validateCoefficients();  
        return a0 + a1 * x;  
    }  
    public void reset() {  
        pn = 0;  
        sumX = sumY = sumXX = sumXY = 0;  
        coefsValid = false;  
    }  
    private void validateCoefficients() {  
        if (coefsValid)  
            return;  
        if (pn >= 2) {  
            float xBar = (float) sumX / pn;  
            float yBar = (float) sumY / pn;  
            a1 = (float) ((pn * sumXY - sumX * sumY) / (pn * sumXX - sumX  
                    * sumX));  
            a0 = (float) (yBar - a1 * xBar);  
        } else {  
            a0 = a1 = Float.NaN;  
        }  
        coefsValid = true;  
    }  
    /** 
     * 返回误差 
     */  
    public double getR() {  
        // 遍历这个list并计算分母  
        for (int i = 0; i < pn - 1; i++) {  
            float Yi = (float) Integer.parseInt(listY.get(i).toString());  
            float Y = at(Integer.parseInt(listX.get(i).toString()));  
            float deltaY = Yi - Y;  
            float deltaY2 = deltaY * deltaY;  
            sumDeltaY2 += deltaY2;    
        }  
        sst = sumYY - (sumY * sumY) / pn;  
        E = 1 - sumDeltaY2 / sst;  
        return round(E, 4);  
    }  
    // 用于实现精确的四舍五入  
    public double round(double v, int scale) {  
        if (scale < 0) {  
        		System.out.println("0除错误！"); 
        }  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }  
    public float round(float v, int scale) {  
        if (scale < 0) {  
            System.out.println("0除错误！");
        }  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();  
    }  
	public void printLine(Linear line) {  
        System.out.println("回归公式:  y = " + line.getA1() + "x + " + line.getA0());  
        System.out.println("误差：     R^2 = " + line.getR());  
    }  
}  