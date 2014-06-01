/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pomdp;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import org.math.plot.*;

/**
 *
 * @author Amine Benabdeljalil
 */
public class SimplePOMDP {
    
    
    //bellman_error to compute in the first step for N steps    
    static double bellman_error;
    static double myopic_bellman_error;
    //N = {1000, 3000, 7000, 10000}
    static int STEP_1000  = 100;
    static int STEP_3000  = 3000;
    static int STEP_7000  = 7000;
    static int STEP_10000 = 10000;
    
    //States are not 'Good = 1', 'Bad = 0', but the different belief probabilites of each channel
    //Actions: Use Channel 1 ('C1'), Use Channel 2 ('C2')
    //Probability of channel i being in good state ('1') = w_i
    //Observations after actions: state of channel
    //Discount Gamma: 0<Gamma<1
    
    //w_i = p(o€O) = {p(11)_i, p(01)_i} or T(w_i) updated !
    
    /*
    Rows: Discrete representation of continuous belief states (100 steps) for w2
    Columns: Discrete representation of continuous belief states (100 steps) for w1
    Vector: represent the Value Function: Expected reward
    */
    /*
        Value V_t+1(w1, w2) = max{V1(w1,w2), V2(w1,w2)}
        V1(w1, w2) = w1 + Gamma * ( w1 * V_t(p_11,T(w2)) + (1-w1) * V_t(p_01,T(w2))) 
        V2(w1, w2) = w2 + Gamma * ( w2 * V_t(T(w1),p_11) + (1-w2) * V_t(T(w1),p_01))
    */
    //DISCRETE CUTTING OF CONTINOUS RANGE OF BELIEF
    static int SIZE = 101;
    static int decimals = 100; //each zero represents one decimal digit to round to
    //Create 2D Value Vector updatable
        //Optimal
    static double [][] matrix = new double[SIZE][SIZE];
    static double [][] next_step_matrix = new double[SIZE][SIZE];
    static double [][] temp_matrix = new double[SIZE][SIZE];
        //Myopic
    static double [][] myopic_matrix = new double[SIZE][SIZE];
    static double [][] next_step_myopic_matrix = new double[SIZE][SIZE];
        //Create 2D Action Vector updatable
    static int [][] action_matrix = new int[SIZE][SIZE];
    static int [][] myopic_action_matrix = new int[SIZE][SIZE];
    //static int [][] whittle_action_matrix = new int[SIZE][SIZE];
    
    //3D BINARY REPRESENTATION MATRIX
    static double [][] _3DMatrix = new double [SIZE][SIZE];
    static double [][] myopic_3DMatrix = new double[SIZE][SIZE];
    static double [][] whittle_3DMatrix = new double[SIZE][SIZE];
    
    //ACTIONS
    static int SEND_1 = 1;
    static int SEND_2 = 2;
    //CHANNEL 1 transition probabilities
    static double p1_00;
    static double p1_01;
    static double p1_10;
    static double p1_11;
    //CHANNEL 2 transition probabilities
    static double p2_00;
    static double p2_01;
    static double p2_10;
    static double p2_11;
    //Channel beliefs
    //static double w1, w2;
    //clock t
    static int clock = 0;    
    //discount
    static double discount;
    
    static boolean _3D_plot_activated = false;
    static boolean stats_activated = false;
    static boolean stats_output = false;
    static boolean stats_print = false;
    static boolean singleAck_activated = false;
    static boolean output_values_activated = false;
    static boolean output_structure_activated = false;
    
    static int PP =  2;
    static int NN = -2;
    static int PN =  1;
    static int NP = -1;
    
    static int type;
    
    public SimplePOMDP(double a, double b, double c, double d, int size, int dec){
        SIZE = size;
        decimals = dec;
        //1-b, a 
        initialization(a, b, c, d);
        if(a > b && c > d){
            type = PP;
        } else if(a > b && c < d){
            type = PN;            
        } else if(a < b && c > d){
            type = NP;
        } else{
            type = NN;
        }
    }
    
    public int[][] execute(int testNum, String folder) throws IOException {
        
        double w1, w2;
        double outcome_1, outcome_2, outcome_1_0, outcome_1_1, outcome_2_0, outcome_2_1, max_outcome;
        int clock_action;
        
        
        
        for(; clock < STEP_1000; clock++){
            //System.out.println("Clock = " + clock);
            for(int i = 0; i < SIZE; i++){
                System.arraycopy(next_step_matrix[i], 0, matrix[i], 0, next_step_matrix[i].length);
                System.arraycopy(next_step_myopic_matrix[i], 0, myopic_matrix[i], 0, next_step_myopic_matrix[i].length);
            }
            //DO VALUE ITERATION ALGORITHM
            
            for(int i = 0; i < SIZE; i++){
                w1 = (double)i/(double)decimals;
                for(int j = 0; j < SIZE; j++){
                    w2 = (double)j/(double)decimals;
                    
                    //calculate outcome of sending in 1
                        //reward is 1
                    outcome_1_1 = 1.0 + discount * matrix[(int)(p1_11 * decimals)][(int)(round(tao(w2,p2_11,p2_01)) * decimals)];
                        //reward is 0
                    outcome_1_0 = discount * matrix[(int)(p1_01 * decimals)][(int)(round(tao(w2,p2_11,p2_01)) * decimals)];
                    //calculate outcome of sending in 2
                        //reward is 1
                    outcome_2_1 = 1.0 + discount * matrix[(int)(round(tao(w1,p1_11,p1_01)) * decimals)][(int)(p2_11 * decimals)];
                        //reward is 0
                    outcome_2_0 = discount * matrix[(int)(round(tao(w1,p1_11,p1_01)) * decimals)][(int)(p2_01 * decimals)];
                    
                    outcome_1 = w1 * outcome_1_1 + (1-w1) * outcome_1_0;
                    outcome_2 = w2 * outcome_2_1 + (1-w2) * outcome_2_0;
                    
                    max_outcome = outcome_1;
                    
                    clock_action = SEND_1;
                    if(outcome_2 > max_outcome){
                        max_outcome = outcome_2; 
                        clock_action = SEND_2;
                    }
                  
                    next_step_matrix[i][j] = max_outcome;
                    action_matrix[i][j] = clock_action;
                  
                    //MYOPIC POLICY
                    if(w1 >= w2){
                        next_step_myopic_matrix[i][j] = calculate_V1(w1, w2);
                        myopic_action_matrix[i][j] = SEND_1;
                    } else{
                        next_step_myopic_matrix[i][j] = calculate_V2(w1, w2);
                        myopic_action_matrix[i][j] = SEND_2;
                    }                 
                }
            }            
        }
        
        //AFTER END OF ITERATIONS, CALCULATE BELLMAN ERROR
        double frame, myopic_frame;
        Whittle whittle1 = new Whittle(p1_11, p1_01, discount);
        Whittle whittle2 = new Whittle(p2_11, p2_01, discount);
        
        String filename_struct = "empty.txt";
        String filename_vals = "empty.txt";
        
        if(output_structure_activated || output_values_activated){
            if(!singleAck_activated){
                filename_struct = folder + "MATRICES_SIMPLE_STRUCTURE_" + testNum + ".txt";
                filename_vals = folder + "MATRICES_SIMPLE_VALUES_" + testNum + ".txt";
            } else{
                filename_struct = folder + "MATRICES_SIMPLE_1ACK_STRUCTURE_" + testNum + ".txt";
                filename_vals = folder + "MATRICES_SIMPLE_1ACK_VALUES_" + testNum + ".txt";
            }            
        }
        
        FileWriter fw_struct = new FileWriter(filename_struct);
        FileWriter fw_vals = new FileWriter(filename_vals);
        String newLine = System.getProperty("line.separator");
        DecimalFormat df = new DecimalFormat("#.##");
        if(output_structure_activated){                        
            fw_struct.write("PARAMETERS: α1 = " + p1_01 + ", 1-ß1 = " + p1_11 + ", α2 = " + p2_01 + ", 1-ß2 = " + p2_11 + newLine);
            fw_struct.write("(w11, w12)\t: OPT\tMYO\tWHI" + newLine);
        }
        if(output_values_activated){                        
            fw_vals.write("PARAMETERS: α1 = " + p1_01 + ", 1-ß1 = " + p1_11 + ", α2 = " + p2_01 + ", 1-ß2 = " + p2_11 + newLine);
            fw_vals.write("(w11, w12)\t: OPTI\t\tMYOP" + newLine);
        }

        for(int i = 0; i < SIZE; i++){
            double belief1 = (double)i/(double)decimals;
            for(int j = 0; j < SIZE; j++){
                double belief2 = (double)j/(double)decimals;
                //System.out.println("(w1: " + (double)i/(double)decimals + ", w2: " + (double)j/(double)decimals + "), Value: " + next_step_matrix[i][j]+ ", ACTION: " + action_matrix[i][j]);
                //System.out.println("(w1: " + (double)i/(double)decimals + ", w2: " + (double)j/(double)decimals + "),NON-MYOPIC: Value: " + next_step_matrix[i][j]+ ", ACTION: " + action_matrix[i][j] + " - MYOPIC: Value: " + next_step_myopic_matrix[i][j] + ", ACTION: " + myopic_action_matrix[i][j]);
                if(myopic_action_matrix[i][j] == SEND_1){
                    //if positive, put 1, else 0
                    myopic_3DMatrix[i][j] = 1.0;
                }
                if(action_matrix[i][j] == SEND_1){
                    //if positive, put 1, else 0
                    _3DMatrix[i][j] = 1.0;
                }
                double wi1 = whittle1.calculate_index(belief1);
                double wi2 = whittle2.calculate_index(belief2);
                if(wi1 >= wi2){
                    whittle_3DMatrix[i][j] = 1.0;
                } else{
                    whittle_3DMatrix[i][j] = 0.0;
                }

                frame = Math.abs(next_step_matrix[i][j] - matrix[i][j]);
                myopic_frame = Math.abs(next_step_myopic_matrix[i][j] - myopic_matrix[i][j]);
                if(frame > bellman_error){
                    bellman_error = frame;
                }
                if(myopic_frame > myopic_bellman_error){
                    myopic_bellman_error = myopic_frame;
                }

                //fw.write("(w_11, w_12), (w_21, w_22): OPT - MYO - P_1 - P_2 - P_3 - P_4 " + newLine);
                if(output_structure_activated){
                    fw_struct.write("(" + belief1+ ", " + belief2 + ")\t: " + (int)_3DMatrix[i][j] + "\t" + (int)myopic_3DMatrix[i][j] + "\t" + (int)whittle_3DMatrix[i][j] + newLine);
                }
                if(output_values_activated){
                    fw_vals.write("(" + belief1+ ", " + belief2 + ")\t: " + df.format(next_step_matrix[i][j]) + "\t\t" + df.format(next_step_myopic_matrix[i][j]) + newLine);
                }
            }
        }                    
        
        fw_struct.close();
        fw_vals.close();
        
        if(stats_print){
            System.out.println("Computed Bellman Error: " + bellman_error);
            System.out.println("Computed Myopic Bellman Error: " + myopic_bellman_error);
        }        
        
        //statistics
        if(stats_activated){
            Stats stats = new Stats();
            stats.prepare_simple(SIZE, _3DMatrix, myopic_3DMatrix, whittle_3DMatrix);
            stats.calculate_scores_simple(type, stats_print, stats_output, singleAck_activated, folder, p1_01, p1_11, p2_01, p2_11);
        }
        
        // 3D PLOTING OF THE MATRICES
        if(_3D_plot_activated){
            double[] x_axis = new double[SIZE];
            double[] y_axis = new double[SIZE];

            for(int i = 0; i < SIZE; i++){
                x_axis[i] = (double)i/(double)decimals;
                y_axis[i] = (double)i/(double)decimals;
            }


            /* ======================== some   test =============================
            double [][] myopic_test = new double[SIZE][SIZE];
            for(int i = 0; i < SIZE; i++){
                for(int j = 0; j < SIZE; j++){
                    if(i>=j) {
                        myopic_test[i][j] = 1.0;
                    } else{
                        myopic_test[i][j] = 0.0;
                    }
                }
            }
            /* ======================== end of test =============================*/

            Plot3DPanel _3dpanel = new Plot3DPanel();
            _3dpanel.addGridPlot("3D Representation of Bellman Error", x_axis, y_axis, whittle_3DMatrix);           

            JFrame myframe = new JFrame("3D");
            myframe.setContentPane(_3dpanel);
            myframe.setSize(500, 500);
            myframe.setDefaultCloseOperation(myframe.EXIT_ON_CLOSE);
            myframe.setVisible(true);
        }
            
        return action_matrix;
    }    
    
    void print_stats(){
        stats_print = true;
    }
    
    void output_stats(){
        stats_output = true;
    }
    
    void activate_stats(){
        stats_activated = true;
    }
    void activate_3D_plot(){
        _3D_plot_activated = true;
    }
    void activate_singleAck(){
        singleAck_activated = true;
    }
    void output_matrix_values(){
        output_values_activated = true;
    }
    void output_matrix_structure(){
        output_structure_activated = true;
    }
        
    static double calculate_V1(double w_1, double w_2){
        //V1(w1, w2) = w1 + Gamma * ( w1 * V_t(p_11,T(w2)) + (1-w1) * V_t(p_01,T(w2))) 
        //V1(w1, w2) = w1 + Gamma * ( s1 + s2 )
        double s1 = w_1 * matrix[(int)(p1_11 * decimals)][(int)(round(tao(w_2, p2_11, p2_01)) * decimals)];
        double s2 = (1.0 - w_1) * matrix[(int)(p1_01 * decimals)][(int)(round(tao(w_2, p2_11, p2_01)) * decimals)];
        double value = w_1 + discount * ( s1 + s2 );
        
        return value;
    }
    
    static double calculate_V2(double w_1, double w_2){
        //V2(w1, w2) = w2 + Gamma * ( w2 * V_t(T(w1),p_11) + (1-w2) * V_t(T(w1),p_01))
        //V2(w1, w2) = w2 + Gamma * ( s1 + (1-w2) * s2)
        double s1 = w_2 * matrix[(int)(round(tao(w_1, p1_11, p1_01)) * decimals)][(int)(p2_11 * decimals)];
        double s2 = (1.0 - w_2) * matrix [(int)(round(tao(w_1, p1_11, p1_01)) * decimals)][(int)(p2_01 * decimals)];
        double value = w_2 + discount * ( s1 + s2 );
        return value;
    }
    
    static double tao(double w, double p11, double p01){
        return w * p11 + (1-w) * p01;
    }
    
    static double tao_pow(double pow, double w, double p11, double p01){
        double rec = w;
        for(int i = 0; i < pow; i++){
            rec = tao(rec, p11, p01);
        }
        return rec;
    }
  
    
    static boolean stop_bellman_recursion(){
        if(clock<2){
            return false;
        }
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                //if one element has a difference of 2 successive value > bellman_error, return false
                if(Math.abs(matrix[i][j] - next_step_matrix[i][j]) > bellman_error){
                    return false;
                }
            }
        }
        return true;
    }
    
    static double round(double v){
        return Math.round(v * (double)decimals)/(double)decimals;
    }
    
    static void initialization(double b1, double a1, double b2, double a2){
        //INITIALIZATION
        clock = 0;
        discount = 0.95;
        bellman_error = -1.0;
        myopic_bellman_error = -2.0;                                         /* TEST CASE FOR 20% DISCREPENCY ?*/
//      p1_11 = 0.8; p1_10 = 0.3; //p1_11 = 0.6; p1_10 = 0.4;                /*           p1_11 = 0.8          */
//      p1_01 = 0.65; p1_00 = 0.4; //p1_01 = 0.7; p1_00 = 0.3;               /*           p1_01 = 0.65         */
//      p2_11 = 0.93; p2_10 = 0.3; //p2_11 = 0.7; p2_10 = 0.3;               /*           p2_11 = 0.93         */
//      p2_01 = 0.1; p2_00 = 0.4; //p2_01 = 0.6; p2_00 = 0.4;                /*           p2_01 = 0.1          */
                
        p1_11 = b1;
        p1_01 = a1;
        p2_11 = b2;
        p2_01 = a2;        
        
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE ; j++){
                matrix[i][j] = 0.0;
                next_step_matrix[i][j] = 0.0;
                myopic_matrix[i][j] = 0.0;
                next_step_myopic_matrix[i][j] = 0.0;
                temp_matrix[i][j] = 0.0;
                action_matrix[i][j] = 0;
                //3D REPRESENTATION MATRIX ALL WITH zeros
                _3DMatrix[i][j] = 0.0;
                myopic_3DMatrix[i][j] = 0.0;
            }
        }
    }
    
}