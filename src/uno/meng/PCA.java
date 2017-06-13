package uno.meng;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import uno.meng.db.CUID;
public class PCA {
     /**
      * Reduce matrix dimension     减少矩阵维度
      * @param source         源矩阵
      * @param dimension       目标维度
      * @return Target matrix     返回目标矩阵
      */ 
    static String[] table = new String[]{"CDT","JDT","PDE","Platform"};
	static CUID cuid = new CUID();
	static List<int[]> list = new ArrayList<int[]>();
    public static void main(String[] args) throws SQLException{
	    	for(int i=0;i<4;i++){
		     System.out.println("对 "+table[i]+" 部分分析结果： ");
	    		list = null;
	    		list = cuid.SearchD(i);
	    		int size = list.size();
	    		double[][] matrix = new double[2][size];
	    		for(int j=0;j<size;j++){
	    			matrix[0][j] = list.get(j)[0];
	    			matrix[1][j] = list.get(j)[1];
	    		}
	        DoubleMatrix d = new DoubleMatrix(matrix);
	        DoubleMatrix result = PCA.dimensionReduction(d, 2);
	        
	        System.out.println(result);
	    	}
    }
    public static DoubleMatrix dimensionReduction(DoubleMatrix source, int dimension) {
        //C=X*X^t/m     矩阵*矩阵^异或/列数
        DoubleMatrix covMatrix = source.mmul(source.transpose()).div(source.columns);
        ComplexDoubleMatrix eigVal = Eigen.eigenvalues(covMatrix);
        ComplexDoubleMatrix[] eigVectorsVal = Eigen.eigenvectors(covMatrix);
        ComplexDoubleMatrix eigVectors = eigVectorsVal[0];
        //通过特征值将符号向量从大到小排序
        List<PCABean> beans = new ArrayList<PCA.PCABean>();
        for (int i = 0; i < eigVectors.columns; i++) {
            beans.add(new PCABean(eigVal.get(i).real(), eigVectors.getColumn(i)));
        }
        Collections.sort(beans);
        DoubleMatrix newVec = new DoubleMatrix(dimension, beans.get(0).vector.rows);
        for (int i = 0; i < dimension; i++) {
            ComplexDoubleMatrix dm = beans.get(i).vector;
            DoubleMatrix real = dm.getReal();
            newVec.putRow(i, real);
        }
        return newVec.mmul(source);
    }
    static class PCABean implements Comparable<PCABean> {
        double eigenValue;
        ComplexDoubleMatrix vector;
        public PCABean(double eigenValue, ComplexDoubleMatrix vector) {
            super();
            this.eigenValue = eigenValue;
            this.vector = vector;
        }
        @Override
        public int compareTo(PCABean o) {
            return Double.compare(o.eigenValue, eigenValue);
        }
        @Override
        public String toString() {
            return "PCABean [eigenValue=" + eigenValue + ", vector=" + vector + "]";
        }
    }
}