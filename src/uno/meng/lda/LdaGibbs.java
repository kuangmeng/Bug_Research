package uno.meng.lda;
import java.text.DecimalFormat;
import java.text.NumberFormat;
public class LdaGibbs {
    int[][] documents;
    int V;
    int K;
    double alpha = 2.0;
    double beta = 0.5;
    int z[][];
    int[][] nw;
    int[][] nd;
    int[] nwsum;
    int[] ndsum;
    double[][] thetasum;
    double[][] phisum;
    int numstats;
    private static int THIN_INTERVAL = 20;
    private static int BURN_IN = 100;
    private static int ITERATIONS = 1000;
    /**
     * 最后的模型个数（取收敛后的n个迭代的参数做平均可以使得模型质量更高）
     */
    private static int SAMPLE_LAG = 10;
    private static int dispcol = 0;
    /**
     * 用数据初始化采样器
     */
    public LdaGibbs(int[][] documents, int V) {
        this.documents = documents;
        this.V = V;
    }

    /**
     * 随机初始化状态
     */
    public void initialState(int K) {
        int M = documents.length;
        // initialise count variables. 初始化计数器
        nw = new int[V][K];
        nd = new int[M][K];
        nwsum = new int[K];
        ndsum = new int[M];
        // The z_i are are initialised to values in [1,K] to determine the
        // initial state of the Markov chain.
        z = new int[M][];   // z_i := 1到K之间的值，表示马氏链的初始状态
        for (int m = 0; m < M; m++) {
            int N = documents[m].length;
            z[m] = new int[N];
            for (int n = 0; n < N; n++) {
                int topic = (int) (Math.random() * K);
                z[m][n] = topic;
                // number of instances of word i assigned to topic j
                nw[documents[m][n]][topic]++;
                // number of words in document i assigned to topic j.
                nd[m][topic]++;
                // total number of words assigned to topic j.
                nwsum[topic]++;
            }
            // total number of words in document i
            ndsum[m] = N;
        }
    }
    public void gibbs(int K) {
        gibbs(K, 2.0, 0.5);
    }
    public void gibbs(int K, double alpha, double beta) {
        this.K = K;
        this.alpha = alpha;
        this.beta = beta;
        // init sampler statistics  分配内存
        if (SAMPLE_LAG > 0) {
            thetasum = new double[documents.length][K];
            phisum = new double[K][V];
            numstats = 0;
        }
        // initial state of the Markov chain:
        initialState(K);
        for (int i = 0; i < ITERATIONS; i++) {
            // for all z_i
            for (int m = 0; m < z.length; m++) {
                for (int n = 0; n < z[m].length; n++) {
                    // (z_i = z[m][n])
                    // sample from p(z_i|z_-i, w)
                    int topic = sampleFullConditional(m, n);
                    z[m][n] = topic;
                }
            }
            if ((i < BURN_IN) && (i % THIN_INTERVAL == 0)) {
                dispcol++;
            }
            // display progress
            if ((i > BURN_IN) && (i % THIN_INTERVAL == 0)) {
                dispcol++;
            }
            // get statistics after burn-in
            if ((i > BURN_IN) && (SAMPLE_LAG > 0) && (i % SAMPLE_LAG == 0)) {
                updateParams();
                if (i % THIN_INTERVAL != 0)
                    dispcol++;
            }
            if (dispcol >= 100) {
                System.out.println();
                dispcol = 0;
            }
        }
    }
    /**
     * 根据上述公式计算文档m中第n个词语的主题的完全条件分布，输出最可能的主题
     */
    private int sampleFullConditional(int m, int n) {
        // remove z_i from the count variables  先将这个词从计数器中抹掉
        int topic = z[m][n];
        nw[documents[m][n]][topic]--;
        nd[m][topic]--;
        nwsum[topic]--;
        ndsum[m]--;
        // do multinomial sampling via cumulative method: 通过多项式方法采样多项式分布
        double[] p = new double[K];
        for (int k = 0; k < K; k++) {
            p[k] = (nw[documents[m][n]][k] + beta) / (nwsum[k] + V * beta)
                    * (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
        }
        // cumulate multinomial parameters  累加多项式分布的参数
        for (int k = 1; k < p.length; k++) {
            p[k] += p[k - 1];
        }
        // scaled sample because of unnormalised p[] 正则化
        double u = Math.random() * p[K - 1];
        for (topic = 0; topic < p.length; topic++) {
            if (u < p[topic])
                break;
        }
        // add newly estimated z_i to count variables   将重新估计的该词语加入计数器
        nw[documents[m][n]][topic]++;
        nd[m][topic]++;
        nwsum[topic]++;
        ndsum[m]++;
        return topic;
    }
    /**
     * 更新参数
     */
    private void updateParams() {
        for (int m = 0; m < documents.length; m++) {
            for (int k = 0; k < K; k++) {
                thetasum[m][k] += (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
            }
        }
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                phisum[k][w] += (nw[w][k] + beta) / (nwsum[k] + V * beta);
            }
        }
        numstats++;
    }

    /**
     * 获取文档——主题矩阵
     */
    public double[][] getTheta() {
        double[][] theta = new double[documents.length][K];
        if (SAMPLE_LAG > 0) {
            for (int m = 0; m < documents.length; m++) {
                for (int k = 0; k < K; k++) {
                    theta[m][k] = thetasum[m][k] / numstats;
                }
            }
        } else {
            for (int m = 0; m < documents.length; m++) {
                for (int k = 0; k < K; k++) {
                    theta[m][k] = (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
                }
            }
        }
        return theta;
    }

    /**
     * 获取主题——词语矩阵
     */
    public double[][] getPhi() {
        double[][] phi = new double[K][V];
        if (SAMPLE_LAG > 0) {
            for (int k = 0; k < K; k++) {
                for (int w = 0; w < V; w++) {
                    phi[k][w] = phisum[k][w] / numstats;
                }
            }
        } else {
            for (int k = 0; k < K; k++) {
                for (int w = 0; w < V; w++) {
                    phi[k][w] = (nw[w][k] + beta) / (nwsum[k] + V * beta);
                }
            }
        }
        return phi;
    }
    public static void hist(double[] data, int fmax) {
        double[] hist = new double[data.length];
        // scale maximum
        double hmax = 0;
        for (int i = 0; i < data.length; i++) {
            hmax = Math.max(data[i], hmax);
        }
        double shrink = fmax / hmax;
        for (int i = 0; i < data.length; i++) {
            hist[i] = shrink * data[i];
        }
        NumberFormat nf = new DecimalFormat("00");
        String scale = "";
        for (int i = 1; i < fmax / 10 + 1; i++) {
            scale += "    .    " + i % 10;
        }
        System.out.println("x" + nf.format(hmax / fmax) + "\t0" + scale);
        for (int i = 0; i < hist.length; i++) {
            System.out.print(i + "\t|");
            for (int j = 0; j < Math.round(hist[i]); j++) {
                if ((j + 1) % 10 == 0)
                    System.out.print("]");
                else
                    System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * 配置采样器
     */
    public void configure(int iterations, int burnIn, int thinInterval,
                          int sampleLag) {
        ITERATIONS = iterations;
        BURN_IN = burnIn;
        THIN_INTERVAL = thinInterval;
        SAMPLE_LAG = sampleLag;
    }

    /**
     * Inference a new document by a pre-trained phi matrix
     */
    public static double[] inference(double alpha, double beta, double[][] phi, int[] doc) {
        int K = phi.length;
        int V = phi[0].length;
        // initialise count variables. 初始化计数器
        int[][] nw = new int[V][K];
        int[] nd = new int[K];
        int[] nwsum = new int[K];
        int ndsum = 0;
        // The z_i are are initialised to values in [1,K] to determine the
        // initial state of the Markov chain.
        int N = doc.length;
        int[] z = new int[N];   // z_i := 1到K之间的值，表示马氏链的初始状态
        for (int n = 0; n < N; n++) {
            int topic = (int) (Math.random() * K);
            z[n] = topic;
            // number of instances of word i assigned to topic j
            nw[doc[n]][topic]++;
            // number of words in document i assigned to topic j.
            nd[topic]++;
            // total number of words assigned to topic j.
            nwsum[topic]++;
        }
        // total number of words in document i
        ndsum = N;
        for (int i = 0; i < ITERATIONS; i++) {
            for (int n = 0; n < z.length; n++) {
                // (z_i = z[m][n])
                // sample from p(z_i|z_-i, w)
                // remove z_i from the count variables  先将这个词从计数器中抹掉
                int topic = z[n];
                nw[doc[n]][topic]--;
                nd[topic]--;
                nwsum[topic]--;
                ndsum--;
                // do multinomial sampling via cumulative method: 通过多项式方法采样多项式分布
                double[] p = new double[K];
                for (int k = 0; k < K; k++) {
                    p[k] = phi[k][doc[n]]
                            * (nd[k] + alpha) / (ndsum + K * alpha);
                }
                // cumulate multinomial parameters  累加多项式分布的参数
                for (int k = 1; k < p.length; k++) {
                    p[k] += p[k - 1];
                }
                // scaled sample because of unnormalised p[] 正则化
                double u = Math.random() * p[K - 1];
                for (topic = 0; topic < p.length; topic++) {
                    if (u < p[topic])
                        break;
                }
                if (topic == K) {
                    throw new RuntimeException("the param K or topic is set too small");
                }
                // add newly estimated z_i to count variables   将重新估计的该词语加入计数器
                nw[doc[n]][topic]++;
                nd[topic]++;
                nwsum[topic]++;
                ndsum++;
                z[n] = topic;
            }
        }
        double[] theta = new double[K];
        for (int k = 0; k < K; k++) {
            theta[k] = (nd[k] + alpha) / (ndsum + K * alpha);
        }
        return theta;
    }
    public static double[] inference(double[][] phi, int[] doc) {
        return inference(2.0, 0.5, phi, doc);
    }
}