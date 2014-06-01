/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pomdp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Amine Benabdeljalil
 */
public class POMDP {
        
    public static void main(String[] args) {
        String folder = "results\\";
        String folder_1ACK = "results_1ACK\\";
        //test_simple(folder);
        //test_multiple_simple(folder);
        //test_complex(folder);
        //test_singleAck_channels(folder_1ACK); /*this one*/
        Stats s = new Stats(); 
        //s.analyze_simple_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_SIMPLE_1ACK_PP.TXT");
        /* 
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_NNNN.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_PNNN.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_NPNN.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_NNPN.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_NNNP.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_PPNN.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_PPPN.TXT");
        s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK_PPPP.TXT");
        */ 
        //s.analyze_complex_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\results_1ACK\\STATS_COMPLEX_1ACK.TXT");
        s.analyze_2D_4D_file("C:\\Users\\Amine\\Documents\\NetBeansProjects\\POMDP\\comparison.txt");
        
    }    
    
    static void test_multiple_simple(String folder){
        
        int size = 101;
        int dec = 100;
        
        /*p1_11*/double[] a = {0.5, 0.58, 0.64, 0.47, 0.9, 0.84, 0.77, 0.39, 0.71, 0.2, 0.8};
        /*p1_01*/double[] b = {0.3, 0.45, 0.64, 0.57, 0.7, 0.84, 0.2, 0.51, 0.88, 0.91, 0.61};
        /*p2_11*/double[] c = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        /*p2_01*/double[] d = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        //double a = 0, b = 0, c = 0, d = 0;
        int count = 0;
        
        for(double c1 : a){            
            //b = 0;
            for(double c2 : b){
                //c = 0;
                if(c1==c2) break;                
                for(double c3 : c){
                    //d = 0;
                    for(double c4 :d){
                        if(c3==c4)break;
                        try {
                            if(!((c1 == c3 && c2 == c4) || (c1 == c2 || c3 == c4))){
                                SimplePOMDP simple = new SimplePOMDP(c1, c2, c3, c4, size, dec);
                                //simple.activate_3D_plot();
                                simple.activate_stats();
                                //simple.print_stats();
                                simple.output_stats();
                                simple.output_matrix_structure();
                                simple.output_matrix_values();
                                simple.execute(count++, folder);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(POMDP.class.getName()).log(Level.SEVERE, null, ex);
                        }                
                        //d += 0.01;
                    }
                    //c += 0.01;
                }
                //b += 0.01;
            }
            //a += 0.01;
        }
        
    }
    
    static void test_simple(String folder){                             
                
        int TEST_ITERATIONS = 2;
        int START_TEST = 0;
        int i = START_TEST;
        int size = 101;
        int dec = 100;
        int test_number = -2;
        
        /*(1-B1)p1_11*/ double[] a = {0.8 , 0.7 };
        /*(A1)p1_01*/   double[] b = {0.65, 0.7 };
        /*(1-B2)p2_11*/ double[] c = {0.93, 0.4 };
        /*(A2)p2_01*/   double[] d = {0.1 , 0.99};                 

        for(; i < TEST_ITERATIONS + START_TEST; i++){
            try {
                SimplePOMDP simple = new SimplePOMDP(a[i], b[i], c[i], d[i], size, dec);
                //simple.activate_3D_plot();
                simple.activate_stats();
                simple.output_stats();
                simple.output_matrix_structure();
                simple.output_matrix_values();
                simple.print_stats();
                simple.execute(test_number++, folder);
            } catch (IOException ex) {
                Logger.getLogger(POMDP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    
    static void test_complex(String folder){
        
        int TEST_ITERATIONS = 2;
        int START_TEST = 0;
        int i = START_TEST;
        int size = 11;
        int dec = 10;
        
        double[] a = {0.8 , 0.7};
        double[] b = {0.8 , 0.61};
        double[] c = {0.93, 0.8};
        double[] d = {0.93, 0.34};
        double[] e = {0.65, 0.7};
        double[] f = {0.65, 0.8};
        double[] g = {0.1 , 0.6};
        double[] h = {0.1 , 0.8};
        try {
            for(; i < TEST_ITERATIONS + START_TEST; i++){
                ComplexPOMDP complex = new ComplexPOMDP(a[i], b[i], c[i], d[i], e[i], f[i], g[i], h[i], size, dec);
                complex.activate_stats();
                complex.output_stats();
                complex.execute(i, folder);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(POMDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
    
    static void test_singleAck_channels(String folder){
        
        int size = 11;
        int dec = 10;
        int size_simple = 101;
        int dec_simple = 100;
        
        Stats stat = new Stats();
        
        /*double[] a  = {0.8, 0.57, 0.42};
        double[] b  = {0.65};
        double[] cc = {0.8, 0.6};
        double[] d  = {0.65, 0.92};
        double[] e  = {0.93, 0.34, 0.6};
        double[] ff = {0.1, 0.77};
        double[] g  = {0.93, 0.67};
        double[] h  = {0.1, 0.24};*/

        double[] a  = {0.8, 0.4, 0.65, 0.21};
        double[] b  = {0.65, 0.9, 0.5};
        double[] cc = {0.8, 0.6, 0.44};
        double[] d  = {0.65, 0.92, 0.15};
        double[] e  = {0.93, 0.55, 0.3};
        double[] ff = {0.1, 0.77, 0.5};
        double[] g  = {0.93, 0.67, 0.12};
        double[] h  = {0.1, 0.24, 0.7, 0.66};        
        
        int count = 0;
        
        
        //double p11_11 = 0.8, p11_01 = 0.65;
        //double p12_11 = 0.8, p12_01 = 0.65;
        //double p21_11 = 0.93, p21_01 = 0.1;
        //double p22_11 = 0.93, p22_01 = 0.1;
        
        for(double p11_11 : a){
            for(double p11_01 : b){
                if(p11_11 == p11_01) continue;
                for(double p12_11 : cc){
                    for(double p12_01 : d){
                        if(p12_11 == p12_01) continue;
                        for(double p21_11 : e){
                            for(double p21_01 : ff){
                                if(p21_11 == p21_01) continue;
                                for(double p22_11 : g){
                                    for(double p22_01 : h){
                                        if(p22_11 == p22_01) continue;
                                        try {
                                            if((p11_11 == p21_11 && p11_01 == p21_01) && (p12_11 == p22_11 && p12_01 == p22_01)){
                                                
                                            }
                                            Functions f = new Functions(dec);

                                            /*double p11_11 = 0.8, p11_01 = 0.65;
                                            double p12_11 = 0.8, p12_01 = 0.65;
                                            double p21_11 = 0.93, p21_01 = 0.1;
                                            double p22_11 = 0.93, p22_01 = 0.1;*/

                                            double sc1_11 = f.tao(p11_11, p11_11, p11_01) * f.tao(p12_11, p12_11, p12_01);
                                            double sc1_01 = calcX(dec, p11_11, p11_01, p12_11, p12_01);
                                            double sc2_11 = f.tao(p21_11, p21_11, p21_01) * f.tao(p22_11, p22_11, p22_01);
                                            double sc2_01 = calcX(dec, p21_11, p21_01, p22_11, p22_01);

                                            double diff1 = sc1_11 - sc1_01;
                                            double diff2 = sc2_11 - sc2_01;                                            
                                            double diff11 = p11_11 - p11_01;
                                            double diff12 = p12_11 - p12_01;
                                            double diff21 = p21_11 - p21_01;
                                            double diff22 = p22_11 - p22_01;
                                            
                                            File file = new File("comparison.txt");
                                            if(!file.exists()){
                                                file.createNewFile();
                                            }
                                            FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
                                            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                                            DecimalFormat df = new DecimalFormat("#.##");
                                            bufferWritter.write("("+df.format(diff11)+"/"+df.format(diff12)+"/"+df.format(diff21)+"/"+df.format(diff22)+")\t");                                            
                                            bufferWritter.close();
                                            fileWritter.close();
                                            
                                            ComplexPOMDP c = new ComplexPOMDP(p11_11, p12_11, p21_11, p22_11, p11_01, p12_01, p21_01, p22_01, size, dec);
                                            c.activate_singleAck();
                                            c.activate_stats();
                                            c.output_stats();
                                            //c.print_stats();//c.output_matrix_structure();//c.output_matrix_values();
                                            int[][][][] _4D = c.execute(count, folder);
                                            SimplePOMDP s = new SimplePOMDP(sc1_11, sc1_01, sc2_11, sc2_01, size_simple, dec_simple);
                                            s.activate_singleAck();
                                            s.activate_stats();
                                            s.output_stats();
                                            //s.print_stats();//s.output_matrix_structure();//s.output_matrix_values();
                                            
                                            fileWritter = new FileWriter(file.getAbsolutePath(),true);
                                            bufferWritter = new BufferedWriter(fileWritter);                                            
                                            bufferWritter.write(" ("+df.format(diff1)+"/"+df.format(diff2)+")\t");                                            
                                            bufferWritter.close();
                                            fileWritter.close();
                                            
                                            int[][] _2D = s.execute(count++, folder);                                            
                                            //comparision between 2D approximation and 4D optimal
                                            stat.compare1ACK(_4D, _2D);
                                        } catch (IOException ex) {
                                            Logger.getLogger(POMDP.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            }
                        }                
                    }
                }
            }
        }
                
    }
    
    static void test_multiple_simple2(String folder){
        
        int size = 11;
        int dec = 10;
        
        double a = 0, b = 0, c = 0, d = 0;
        int count = 0;
        
        while(a<=1.0){            
            b = 0;
            while(b<=1.0){
                c = 0;
                if(a==b) continue;                
                while(c<=1.0){
                    d = 0;
                    while(d<=1.0){
                        if(c==d) continue;
                        try {
                            if(!((a == c && b == d) || (a == b || c == d))){
                                SimplePOMDP simple = new SimplePOMDP(a, b, c, d, size, dec);
                                //simple.activate_3D_plot();
                                simple.activate_stats();
                                //simple.print_stats();
                                simple.output_stats();
                                //simple.output_matrix_structure();
                                //simple.output_matrix_values();
                                simple.execute(count++, folder);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(POMDP.class.getName()).log(Level.SEVERE, null, ex);
                        }                
                        d += 0.01;
                    }
                    c += 0.01;
                }
                b += 0.01;
            }
            a += 0.01;
        }
    }
    
    static double calcX(int dec, double p1_11, double p1_01, double p2_11, double p2_01){
        double X;
        Functions f = new Functions(dec);
        X = ((p1_01)/(p1_01 + (1 - p1_11))) * ((p2_01)/(p2_01 + (1 - p2_11))) * (1 - f.tao(p1_11, p1_11, p1_01) * f.tao(p2_11, p2_11, p2_01)) / (1 - ((p1_01)/(p1_01 + (1 - p1_11))) * ((p2_01)/(p2_01 + (1 - p2_11))));        
        return X;
    }
       
}