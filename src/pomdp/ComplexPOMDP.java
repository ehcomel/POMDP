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
//import org.math.plot.*;

/**
 *
 * @author Amine Benabdeljalil
 */
public class ComplexPOMDP {
    
    //bellman_error to compute in the first step for N steps    
    static double bellman_error;
    static double myopic_bellman_error;
    static double bellman_error_final;
    //N = {1000, 3000, 7000, 10000}
    static int STEP_1000  = 100;
    static int STEP_3000  = 3000;
    static int STEP_7000  = 7000;
    static int STEP_10000 = 10000;
    
    
    //DISCRETE CUTTING OF CONTINOUS RANGE OF BELIEF
    static int SIZE = 11;
    static int decimals = 10; //each zero represents one decimal digit to round to
    //Create 2D Value Vector updatable
        //Optimal
    static double [][][][] matrix = new double[SIZE][SIZE][SIZE][SIZE];
    static double [][][][] next_step_matrix = new double[SIZE][SIZE][SIZE][SIZE];
        //Myopic
    static double [][][][] myopic_matrix = new double[SIZE][SIZE][SIZE][SIZE];
    static double [][][][] next_step_myopic_matrix = new double[SIZE][SIZE][SIZE][SIZE];
        //Create 2D Action Vector updatable
    static int [][][][] action_matrix = new int[SIZE][SIZE][SIZE][SIZE];
    static int [][][][] myopic_action_matrix = new int[SIZE][SIZE][SIZE][SIZE];   
        //whittle policies action matrices
    static int [][][][] p1_whittle_action_matrix = new int[SIZE][SIZE][SIZE][SIZE];
    static int [][][][] p2_whittle_action_matrix = new int[SIZE][SIZE][SIZE][SIZE];
    static int [][][][] p3_whittle_action_matrix = new int[SIZE][SIZE][SIZE][SIZE];
    static int [][][][] p4_whittle_action_matrix = new int[SIZE][SIZE][SIZE][SIZE];
    
    //3D BINARY REPRESENTATION MATRIX
    /*static double [][] _3DMatrix = new double [SIZE][SIZE];
    static double [][] myopic_3DMatrix = new double[SIZE][SIZE];
    static double [][] whittle_3DMatrix = new double[SIZE][SIZE];
    */
    //ACTIONS
    static int SEND_1 = 1; //Send Path 1
    static int SEND_2 = 2; //Send Path 2
    //PATH 1 transition probabilities
        //CHANNEL 1.1
    static double p11_01;
    static double p11_11;
        //CHANNEL 1.2
    static double p12_01;
    static double p12_11;
    //PATH 2 transition probabilities
        //CHANNEL 2.1
    static double p21_01;
    static double p21_11;
        //CHANNEL 2.2
    static double p22_01;
    static double p22_11;
    //Channel beliefs
    //static double w1, w2;
    //clock t
    static int clock = 0;    
    //discount
    static double discount;
    
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
    static int type1;
    static int type2;
    static double diff11, diff12;
    static double diff21, diff22;
    
    public ComplexPOMDP(double a, double b, double c, double d, double e, double f, double g, double h, int size, int dec){
        SIZE = size;
        decimals = dec;
        //a11, a12, a21, a22, b11, b12, b21, b22
        initialization(e, f, g, h, a, b, c, d);
        //type1 = channel 1
        diff11 = a - e; diff12 = b - f;
        diff21 = c - g; diff22 = d - h;
        if(a > e && b > f){
            type1 = PP;
        } else if(a > e && b < f){
            type1 = PN;
        } else if(a < e && b > f){
            type1 = NP;
        } else{
            type1 = NN;
        }
        //type2 = channel 2
        if(c > g && d > h){
           type2 = PP; 
        } else if(c > g && d < h){
            type2 = PN;
        } else if(c < g && d > h){
            type2 = NP;
        } else{
            type2 = NN;
        }
    }
    
    public int[][][][] execute(int testNum, String folder) throws IOException {
        
        double w11, w12, w21, w22;        
        
        for(; clock < STEP_1000; clock++){
            //System.out.println("Clock = " + clock);
            for(int i = 0; i < SIZE; i++){
                for(int j = 0; j < SIZE; j++){
                    for(int k = 0; k < SIZE; k++){
                        System.arraycopy(next_step_matrix[i][j][k], 0, matrix[i][j][k], 0, next_step_matrix[i][j][k].length);
                        System.arraycopy(next_step_myopic_matrix[i][j][k], 0, myopic_matrix[i][j][k], 0, next_step_myopic_matrix[i][j][k].length);

                    }
                }                
            }
            //DO VALUE ITERATION ALGORITHM
            
            for(int i = 0; i < SIZE; i++){
                w11 = (double)i/(double)decimals;
                for(int j = 0; j < SIZE; j++){
                    w12 = (double)j/(double)decimals;
                    for(int x = 0; x < SIZE; x++){
                        w21 = (double)x/(double)decimals;
                        for(int y = 0; y < SIZE; y++){
                            w22 = (double)y/(double)decimals;
                            
                            //OPTIMAL POLICY
                            double V1;
                            double V2;                            
                            if(!singleAck_activated){
                                V1 = calculate_V1(w11, tao(w12, p12_11, p12_01), w21, tao(w22, p22_11, p21_01));
                                V2 = calculate_V2(w11, tao(w12, p12_11, p12_01), w21, tao(w22, p22_11, p21_01));
                            } else{
                                V1 = calculate_singleAck_V1(w11, tao(w12, p12_11, p12_01), w21, tao(w22, p22_11, p21_01));
                                V2 = calculate_singleAck_V2(w11, tao(w12, p12_11, p12_01), w21, tao(w22, p22_11, p21_01));
                            }                            
                            
                            double max;
                            
                            if(V1 >= V2){
                                max = V1;                                
                                next_step_matrix[i][j][x][y] = max;
                                action_matrix[i][j][x][y] = SEND_1;
                            } else{
                                max = V2;
                                next_step_matrix[i][j][x][y] = max;
                                action_matrix[i][j][x][y] = SEND_2;
                            }
                            
                            
                            //MYOPIC POLICY
                            
                            if(w11 * w12 >= w21 * w22){
                                next_step_myopic_matrix[i][j][x][y] = V1;
                                myopic_action_matrix[i][j][x][y] = SEND_1;
                            } else{
                                next_step_myopic_matrix[i][j][x][y] = V2;
                                myopic_action_matrix[i][j][x][y] = SEND_2;
                            }                                                       
                            
                        }
                        
                    }                    
                }
            }            
        }
        
        //AFTER END OF ITERATIONS, CALCULATE BELLMAN ERROR
        double frame, myopic_frame;
        Whittle whittle11 = new Whittle(p11_11, p11_01, discount);
        double wt11;
        Whittle whittle12 = new Whittle(p12_11, p12_01, discount);
        double wt12;
        Whittle whittle21 = new Whittle(p21_11, p21_01, discount);
        double wt21;
        Whittle whittle22 = new Whittle(p22_11, p22_01, discount);
        double wt22;
        
        String filename_struct = folder + "empty.txt";
        String filename_vals = folder + "empty.txt";
        
        if(output_structure_activated || output_values_activated){
            if(!singleAck_activated){
            filename_struct = folder + "MATRICES_COMPLEX_STRUCTURE_" + testNum + ".txt";
            filename_vals = folder + "MATRICES_COMPLEX_VALUES_" + testNum + ".txt";
            } else{
                filename_struct = folder + "MATRICES_COMPLEX_1ACK_STRUCTURE_" + testNum + ".txt";
                filename_vals = folder + "MATRICES_COMPLEX_1ACK_VALUES_" + testNum + ".txt";
            }
        }
        
        FileWriter fw_struct = new FileWriter(filename_struct);        
        FileWriter fw_vals = new FileWriter(filename_vals);        
        String newLine = System.getProperty("line.separator");
        DecimalFormat df = new DecimalFormat("#.##");
        
        if(output_structure_activated){
            fw_struct.write("PARAMETERS: α11 = " + p11_01 + ", 1-ß11 = " + p11_11 + ", α12 = " + p12_01 + ", 1-ß12 = " + p12_11 + ", α21 = " + p21_01 + ", 1-ß21 = " + p21_11 + ", α22 = " + p22_01 + ", 1-ß22 = " + p22_11 + newLine);
            fw_struct.write("(w11, w12), (w21, w22): OPT - MYO - P_1 - P_2 - P_3 - P_4 " + newLine);        
        }
        if(output_values_activated){
            fw_vals.write("PARAMETERS: α11 = " + p11_01 + ", 1-ß11 = " + p11_11 + ", α12 = " + p12_01 + ", 1-ß12 = " + p12_11 + ", α21 = " + p21_01 + ", 1-ß21 = " + p21_11 + ", α22 = " + p22_01 + ", 1-ß22 = " + p22_11 + newLine);
            fw_vals.write("(w11, w12), (w21, w22): OPT\tMYO" + newLine);        
        }
        
                        
        for(int i = 0; i < SIZE; i++){
            double belief11 = (double)i/(double)decimals;
            wt11 = whittle11.calculate_index(belief11);
            for(int j = 0; j < SIZE; j++){
                double belief12 = (double)j/(double)decimals;
                wt12 = whittle12.calculate_index(belief12);
                for(int x = 0; x < SIZE; x++){
                    double belief21 = (double)x/(double)decimals;
                    wt21 = whittle21.calculate_index(belief21);
                    for(int y = 0; y < SIZE; y++){
                        double belief22 = (double)y/(double)decimals ;
                        wt22 = whittle22.calculate_index(belief22);
                        
                        //System.out.println("(w1: " + (double)i/(double)decimals + ", w2: " + (double)j/(double)decimals + "), Value: " + next_step_matrix[i][j]+ ", ACTION: " + action_matrix[i][j]);
                        //System.out.println("(w1: " + (double)i/(double)decimals + ", w2: " + (double)j/(double)decimals + "),NON-MYOPIC: Value: " + next_step_matrix[i][j]+ ", ACTION: " + action_matrix[i][j] + " - MYOPIC: Value: " + next_step_myopic_matrix[i][j] + ", ACTION: " + myopic_action_matrix[i][j]);                       

                        //whittle policies
                            //policy 1:   Wt11 + Wt12  <>  Wt21 + Wt22
                        if(wt11 + wt12 >= wt21 + wt22){
                            p1_whittle_action_matrix[i][j][x][y] = SEND_1;
                        } else{
                            p1_whittle_action_matrix[i][j][x][y] = SEND_2;
                        }
                        //policy 2:   Wt11 * Wt12  <>  Wt 21 * Wt22
                        if(wt11 * wt12 >= wt21 * wt22){
                            p2_whittle_action_matrix[i][j][x][y] = SEND_1;
                        } else{
                            p2_whittle_action_matrix[i][j][x][y] = SEND_2;
                        }
                        //policy 3:  |Wt11 - Wt12| <> |Wt21 - Wt22|  /*to elaborate more*/
                        if(Math.abs(wt11 - wt12) <= Math.abs(wt21 - wt22)){
                            p3_whittle_action_matrix[i][j][x][y] = SEND_1;
                        } else{
                            p3_whittle_action_matrix[i][j][x][y] = SEND_2;
                        }
                        //policy 4:         1                         1
                        //           -----------------     <>  -----------------
                        //            1/Wt11  + 1/Wt12         1/Wt21  + 1/Wt22                        
                        if(1/((1/wt11) + (1/wt12)) >= 1/((1/wt21) + (1/wt22))){
                            p4_whittle_action_matrix[i][j][x][y] = SEND_1;
                        } else{
                            p4_whittle_action_matrix[i][j][x][y] = SEND_2;
                        }
                                                                        
                        //fw.write("(w_11, w_12), (w_21, w_22): OPT - MYO - P_1 - P_2 - P_3 - P_4 " + newLine);        
                        if(output_structure_activated){
                            fw_struct.write("(" + belief11+ ", " + belief12 + "), (" + belief21 + ", " + belief22 + "):  " + action_matrix[i][j][x][y] + "  -  " + myopic_action_matrix[i][j][x][y] + "  -  " + p1_whittle_action_matrix[i][j][x][y] + "  -  " + p2_whittle_action_matrix[i][j][x][y] + "  -  " + p3_whittle_action_matrix[i][j][x][y] + "  -  " + p4_whittle_action_matrix[i][j][x][y] + newLine);
                        }
                        if(output_values_activated){
                            fw_vals.write("(" + belief11+ ", " + belief12 + "), (" + belief21 + ", " + belief22 + "):  " + df.format(next_step_matrix[i][j][x][y]) + "\t" + df.format(next_step_myopic_matrix[i][j][x][y]) + newLine);
                        }
                        
                        frame = Math.abs(next_step_matrix[i][j][x][y] - matrix[i][j][x][y]);
                        myopic_frame = Math.abs(next_step_myopic_matrix[i][j][x][y] - myopic_matrix[i][j][x][y]);
                        if(frame > bellman_error){
                            bellman_error = frame;
                        }
                        if(myopic_frame > myopic_bellman_error){
                            myopic_bellman_error = myopic_frame;
                        }
                    }
                }                
            }
        }
        
        //SAVE: ACTION_MATRIX, MYOPIC_ACTION_MATRIX, P1/P2/P3_WHITTLE_ACTION_MATRIX for later use
        fw_struct.close();
        fw_vals.close();
         
        if(stats_print){
            System.out.println("Computed Bellman Error: " + bellman_error);
            System.out.println("Computed Myopic Bellman Error: " + myopic_bellman_error);
        }
        
        if(stats_activated){
            Stats stats = new Stats();
            stats.prepare_complex(SIZE, action_matrix, myopic_action_matrix, p1_whittle_action_matrix, p2_whittle_action_matrix, p3_whittle_action_matrix, p4_whittle_action_matrix);
            stats.calculate_scores_complex(diff11, diff12, diff21, diff22, type1, type2, stats_print, stats_output, singleAck_activated, folder);
        }
        
        return action_matrix;
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
    void activate_stats(){
        stats_activated = true;
    }    
    void output_stats(){
        stats_output = true;
    }    
    void print_stats(){
        stats_print = true;
    }
    static double getV(double w11, double w12, double w21, double w22){        
        return matrix[(int)(round(w11) * decimals)][(int)(round(w12) * decimals)][(int)(round(w21) * decimals)][(int)(round(w22) * decimals)];      
    }
    
    static double calculate_V1(double w11, double w12, double w21, double w22){
        double value;
        
        value = w11 * tao(w12, p12_11, p12_01) + 
                discount * (
                    w11 * tao(w12, p12_11, p12_01)          * getV(tao(p11_11, p11_11, p11_01), tao(p12_11, p12_11, p12_01),    tao_pow(2, w21, p21_11, p21_01), tao_pow(3, w22, p22_11, p22_01))
                  + w11 * (1.0 - tao(w12, p12_11, p12_01))  * getV(tao(p11_11, p11_11, p11_01), tao(p12_01, p12_11, p12_01),    tao_pow(2, w21, p21_11, p21_01), tao_pow(3, w22, p22_11, p22_01))
                  + (1.0 - w11)                             * getV(tao(p11_01, p11_11, p11_01), tao_pow(3, w12, p12_11, p12_01),tao_pow(2, w21, p21_11, p21_01), tao_pow(3, w22, p22_11, p22_01))
                );
        
        return value;
    }
    
    static double calculate_V2(double w11, double w12, double w21, double w22){        
        double value;
        
        value = w21 * tao(w22, p22_11, p22_01) + 
                discount * (
                    w21 * tao(w22, p22_11, p22_01)          * getV(tao_pow(2, p11_11, p11_11, p11_01), tao_pow(3, p12_11, p12_11, p12_01), tao(p21_11, p21_11, p21_01),tao(p22_11, p22_11, p22_01))
                  + w21 * (1.0 - tao(w22, p22_11, p22_01))  * getV(tao_pow(2, p11_11, p11_11, p11_01), tao_pow(3, p12_01, p12_11, p12_01), tao(p21_11, p21_11, p21_01),tao(p22_01, p22_11, p22_01))
                  + (1.0 - w21)                             * getV(tao_pow(2, p11_01, p11_11, p11_01), tao_pow(3, w12, p12_11, p12_01),    tao(p21_01, p21_11, p21_01),tao_pow(3, w22, p22_11, p22_01))
                );
        
        return value;
    }
    
    static double calculate_singleAck_V1(double w11, double w12, double w21, double w22){
        double value;
        
        value = w11 * tao(w12, p12_11, p12_01) + 
                discount * (
                    w11 * tao(w12, p12_11, p12_01)          * getV(tao(p11_11, p11_11, p11_01), tao(p12_11, p12_11, p12_01),    tao_pow(2, w21, p21_11, p21_01), tao_pow(3, w22, p22_11, p22_01))
                  + (1.0 - w11 * tao(w12, p12_11, p12_01))  * getV(tao_pow(2, w11, p11_11, p11_01), tao_pow(3, w12, p12_11, p12_01),tao_pow(2, w21, p21_11, p21_01), tao_pow(3, w22, p22_11, p22_01))
                );
        
        return value;
    }
    
    static double calculate_singleAck_V2(double w11, double w12, double w21, double w22){
        double value;
        
        value = w21 * tao(w22, p22_11, p22_01) + 
                discount * (
                    w21 * tao(w22, p22_11, p22_01)          * getV(tao_pow(2, p11_11, p11_11, p11_01), tao_pow(3, p12_11, p12_11, p12_01), tao(p21_11, p21_11, p21_01),tao(p22_11, p22_11, p22_01))                  
                  + (1.0 - w21 * tao(w22, p22_11, p22_01))  * getV(tao_pow(2, p11_01, p11_11, p11_01), tao_pow(3, w12, p12_11, p12_01),    tao_pow(2, w21, p21_11, p21_01),tao_pow(3, w22, p22_11, p22_01))
                );        
        
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
                for(int x = 0; x < SIZE; x++){
                    for(int y = 0; y < SIZE; y++){
                        //if one element has a difference of 2 successive value > bellman_error, return false
                        if(Math.abs(matrix[i][j][x][y] - next_step_matrix[i][j][x][y]) > bellman_error){
                            return false;
                        }
                    }
                }                
            }
        }
        return true;
    }
    
    static double round(double v){
        return Math.round(v * (double)decimals)/(double)decimals;
    }
    
    static void initialization(double a11, double a12, double a21, double a22, double b11, double b12, double b21, double b22){
        //INITIALIZATION
        clock = 0;
        discount = 0.95;
        bellman_error = -1.0;
        myopic_bellman_error = -2.0;  
        bellman_error_final = 0.001;
        
        //p11_11 = 0.8;  p11_01 = 0.65; 
        //p12_11 = 0.8;  p12_01 = 0.65; 
        //p21_11 = 0.93; p21_01 = 0.1;       
        //p22_11 = 0.93; p22_01 = 0.1;
        p11_11 = b11; p11_01 = a11; 
        p12_11 = b12; p12_01 = a12; 
        p21_11 = b21; p21_01 = a21;       
        p22_11 = b22; p22_01 = a22;  
        
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE ; j++){
                for(int x = 0; x < SIZE; x++){
                    for(int y = 0; y < SIZE; y++){
                        matrix[i][j][x][y] = 0.0;
                        next_step_matrix[i][j][x][y] = 0.0;
                        myopic_matrix[i][j][x][y] = 0.0;
                        next_step_myopic_matrix[i][j][x][y] = 0.0;
                        action_matrix[i][j][x][y] = 0; 
                    }
                }
               
            }
        }
    }
    
}