package pomdp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Amine Benabdeljalil
 */
public class Stats {
    
    int SIZE_SIMPLE;
    int SIZE_COMPLEX;
    
    int[][][][] optimal;
    int[][][][] myopic;
    int[][][][] p1;
    int[][][][] p2;
    int[][][][] p3 ;
    int[][][][] p4;
    
    double[][] optimal_simple;
    double[][] myopic_simple;
    double[][] whittle_simple;
    
    public Stats(){
        
    }
    
    void compare1ACK(int[][][][] _4D, int[][] _2D){
        FileWriter fw = null;
        try {
            double w11, w12, w21, w22;
            double w1, w2;
            int m, n;
            Map<String,ArrayList<Integer>> comp = new HashMap<>();
            //key: value: []
            // (0.2, 0.3) : [1,1,0,0,0,1,1,1,1,1,0,0,0,0]
            for(int i = 0; i < 11; i++){
                w11 = (double)i/10.0;
                for(int j = 0; j < 11; j++){
                    w12 = (double)j/10.0;
                    for(int k = 0; k < 11; k++){
                        w21 = (double)k/10.0;
                        for(int l = 0; l < 11; l++){
                            w22 = (double)l/10.0;
                            DecimalFormat df = new DecimalFormat("#.##");
                            //System.out.print("w11: " + w11 + " w12: " + w12 + " w21: " + w21 + " w22: " + w22);
                            w1 = w11 * w12;
                            w2 = w21 * w22;
                            //System.out.print(" / w1: " + w1 + " w2: " + w2);
                            //System.out.println(" / w1: " + df.format(w1) + " w2: " + df.format(w2));
                            m = (int)(w1 * 100);
                            n = (int)(w2 * 100);
                            int x = _4D[i][j][k][l];
                            int y = _2D[m][n];
                            String key = df.format(w1) + "-" + df.format(w2);
                            if(!comp.containsKey(key)){
                                ArrayList<Integer> a = new ArrayList<>();
                                a.add(y);
                                a.add(x);
                                comp.put(key, a);
                            } else{
                                comp.get(key).add(x);
                            }
                        }
                    }
                }
            }   double total = 0;
            int line_total = 0;
            int count = 0;
            int count_line = 0;
            int count2 = 0;
            double total2 = 0;
            int individual_total = 0;
            Iterator<String> iterator = comp.keySet().iterator();
            while(iterator.hasNext()){
                line_total = 0;
                count_line = 0;
                count++;
                String ink = iterator.next();
                //System.out.print("(" + ink + "): ");
                Iterator<Integer> it = comp.get(ink).iterator();
                int s2d = it.next();
                //System.out.print("(" + s2d + ") [");
                while(it.hasNext()) {
                    count_line++;
                    int f = it.next();
                    //System.out.print(f+",");
                    if(s2d == f){
                        line_total++;
                    }
                }
                individual_total += line_total;
                double line_avg = 100.0 * (double)line_total/(double)count_line;
                //System.out.println("], avg: " + line_avg + "% (" + count_line + ")");
                total += line_avg * count_line;
                count += count_line;
                total2 += line_avg;
                count2++;
            }   
            double weighted_mean = total/(double)count;
            double mean = total2 / (double)count2;
            DecimalFormat df = new DecimalFormat("#.##");
            //System.out.println("===============================================================");
//System.out.println("Weighted Arithmetic Avg:" + weighted_mean + " % - Arithmetic Avg:" + mean + " %");
            //System.out.println("===============================================================");
            File file2 = new File("comparison.txt");
            fw = new FileWriter(file2.getAbsolutePath(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(df.format(weighted_mean) + "\t" + df.format(mean) + "\n");
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void analyze_2D_4D_file(String filepath){
        try {
            double sum_myo = 0, sum_myo_p = 0, sum_myo_n = 0, sum_myo_0 = 0, sum_myo_1 = 0, sum_myo_2 = 0, sum_myo_3 = 0, sum_myo_4 = 0;
            double sum_wam = 0, sum_wam_p = 0, sum_wam_n = 0, sum_wam_0 = 0, sum_wam_1 = 0, sum_wam_2 = 0, sum_wam_3 = 0, sum_wam_4 = 0;
            double sum_am  = 0, sum_am_p  = 0, sum_am_n  = 0, sum_am_0  = 0, sum_am_1  = 0, sum_am_2 = 0, sum_am_3  = 0, sum_am_4  = 0;            
            int count = 0, count_p = 0, count_n = 0, count_0 = 0, count_1 = 0, count_2 = 0, count_3 = 0, count_4 = 0;
            int count_treshhold1 = 0;
            String line;
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            while ((line = br.readLine()) != null) {
                int positive_channels = 0;
                count++;
                StringTokenizer st = new StringTokenizer(line.replaceAll(",", "."));
                //differences of parameters
                String params = st.nextToken().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", " ");
                StringTokenizer stp = new StringTokenizer(params);
                double sum_params = 0;
                while(stp.hasMoreTokens()){
                    double d = Double.parseDouble(stp.nextToken());
                    sum_params += d;
                    if(d>0){
                        positive_channels++;
                    }
                }
                //percentage Myopic 4D   
                double t = Double.parseDouble(st.nextToken());
                sum_myo += t;
                if(sum_params>=0){
                    sum_myo_p += t;
                    count_p++;
                } else{
                    sum_myo_n += t;
                    count_n++;
                }
                if(positive_channels == 0){
                    sum_myo_0 += t;
                    count_0++;
                } else if(positive_channels == 1){
                    sum_myo_1 += t;
                    count_1++;
                } else if(positive_channels == 2){
                    sum_myo_2 += t;
                    count_2++;
                } else if(positive_channels == 3){
                    sum_myo_3 += t;
                    count_3++;
                } else{
                    sum_myo_4 += t;
                    count_4++;
                }
                //test ranges in which performances are above a certain treshold
                if(t > 80){
                    System.out.println(params + " : " + t);
                    count_treshhold1++;
                }
                //differences of approx. parameters
                st.nextToken();
                //weighted arithmetic mean percentage 2D
                t = Double.parseDouble(st.nextToken());
                sum_wam += t;
                if(sum_params>=0){
                    sum_wam_p += t;
                } else{
                    sum_wam_n += t;
                }
                if(positive_channels == 0){
                    sum_wam_0 += t;
                } else if(positive_channels == 1){
                    sum_wam_1 += t;
                } else if(positive_channels == 2){
                    sum_wam_2 += t;
                } else if(positive_channels == 3){
                    sum_wam_3 += t;
                } else{
                    sum_wam_4 += t;
                }
                //arithmetic mean percentage 2D
                t = Double.parseDouble(st.nextToken());
                sum_am += t;
                if(sum_params>=0){
                    sum_am_p += t;
                } else{
                    sum_am_n += t;
                }
                if(positive_channels == 0){
                    sum_am_0 += t;
                } else if(positive_channels == 1){
                    sum_am_1 += t;
                } else if(positive_channels == 2){
                    sum_am_2 += t;
                } else if(positive_channels == 3){
                    sum_am_3 += t;
                } else{
                    sum_am_4 += t;
                }
            }
            System.out.println("Over Treshhold: " + count_treshhold1);
            System.out.println("====================================");
            System.out.println("Avg Myopic 4D = " + (sum_myo/(double)count) + "\tWeighted Avg 2D = " + (sum_wam/(double)count) + "\tAvg 2D = " + (sum_am/(double)count));
            System.out.println("====================================");
            System.out.print("Positive: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_p/(double)count_p) + "\tWeighted Avg 2D = " + (sum_wam_p/(double)count_p) + "\tAvg 2D = " + (sum_am_p/(double)count_p));
            System.out.print("Negative: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_n/(double)count_n) + "\tWeighted Avg 2D = " + (sum_wam_n/(double)count_n) + "\tAvg 2D = " + (sum_am_n/(double)count_n));
            System.out.println("====================================");
            System.out.print("0 Positive Channels: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_0/(double)count_0) + "\tWeighted Avg 2D = " + (sum_wam_0/(double)count_0) + "\tAvg 2D = " + (sum_am_0/(double)count_0));
            System.out.print("1 Positive Channels: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_1/(double)count_1) + "\tWeighted Avg 2D = " + (sum_wam_1/(double)count_1) + "\tAvg 2D = " + (sum_am_1/(double)count_1));
            System.out.print("2 Positive Channels: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_2/(double)count_2) + "\tWeighted Avg 2D = " + (sum_wam_2/(double)count_2) + "\tAvg 2D = " + (sum_am_2/(double)count_2));
            System.out.print("3 Positive Channels: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_3/(double)count_3) + "\tWeighted Avg 2D = " + (sum_wam_3/(double)count_3) + "\tAvg 2D = " + (sum_am_3/(double)count_3));
            System.out.print("4 Positive Channels: ");
            System.out.println("Avg Myopic 4D = " + (sum_myo_4/(double)count_4) + "\tWeighted Avg 2D = " + (sum_wam_4/(double)count_4) + "\tAvg 2D = " + (sum_am_4/(double)count_4));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void analyze_full_correlation_4D(String filepath, int typefile){
        //prepare ranges
        /*double[] positive_ranges = {0.0, 0.1, 0.3, 0.5, 0.7, 1.0};
        double[] negative_ranges = {-1.0, -0.7, -0.5, -0.3, -0.1, 0.0};*/
        //Read file PPPP or NNNN        
        try {
            String line;
            double sum1_1 = 0; double sum2_1 = 0; double sum3_1 = 0; double sum4_1 = 0; double sum5_1 = 0;
            double sum1_2 = 0; double sum2_2 = 0; double sum3_2 = 0; double sum4_2 = 0; double sum5_2 = 0;
            double sum1_3 = 0; double sum2_3 = 0; double sum3_3 = 0; double sum4_3 = 0; double sum5_3 = 0;
            double sum1_4 = 0; double sum2_4 = 0; double sum3_4 = 0; double sum4_4 = 0; double sum5_4 = 0;
            double sum1_5 = 0; double sum2_5 = 0; double sum3_5 = 0; double sum4_5 = 0; double sum5_5 = 0;
            int count1 = 0; int count2 = 0; int count3 = 0; int count4 = 0; int count5 = 0;
            
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            br.readLine();
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                //Token 1: diff. params /*prepare treshold ranges */
                String params = st.nextToken().replaceAll(",", ".");
                StringTokenizer pt = new StringTokenizer(params, "/");
                double d11, d12, d21, d22;
                d11 = Double.parseDouble(pt.nextToken());
                d12 = Double.parseDouble(pt.nextToken());
                d21 = Double.parseDouble(pt.nextToken());
                d22 = Double.parseDouble(pt.nextToken());
                //Tokens: remove %
                double myo = Double.parseDouble(st.nextToken().replaceAll(",", ".").replaceAll("%", ""));
                double p1  = Double.parseDouble(st.nextToken().replaceAll(",", ".").replaceAll("%", ""));
                double p2  = Double.parseDouble(st.nextToken().replaceAll(",", ".").replaceAll("%", ""));
                double p3  = Double.parseDouble(st.nextToken().replaceAll(",", ".").replaceAll("%", ""));
                double p4  = Double.parseDouble(st.nextToken().replaceAll(",", ".").replaceAll("%", ""));
                
                if(typefile == 1){
                    if(d11 <= 0.3 && d12 <= 0.3 && d21 <= 0.3 && d22 <= 0.3){
                        sum1_1 += myo; sum2_1 += p1; sum3_1 += p2; sum4_1 += p3; sum5_1 += p4;
                        count1++;
                    } else if((0.2 < d11 && d11 <= 0.4)&&(0.2 < d12 && d12 <= 0.4)&&(0.2 < d21 && d21 <= 0.4)&&(0.2 < d22 && d22 <= 0.4)){
                        sum1_2 += myo; sum2_2 += p1; sum3_2 += p2; sum4_2 += p3; sum5_2 += p4;
                        count2++;
                    } else if((0.35 < d11 && d11 <= 0.7)&&(0.35 < d12 && d12 <= 0.7)&&(0.35 < d21 && d21 <= 0.7)&&(0.35 < d22 && d22 <= 0.7)){
                        sum1_3 += myo; sum2_3 += p1; sum3_3+= p2; sum4_3 += p3; sum5_3 += p4;
                        count3++;
                    }/* else if((0.5 < d11 && d11 <= 0.7)&&(0.5 < d12 && d12 <= 0.7)&&(0.5 < d21 && d21 <= 0.7)&&(0.5 < d22 && d22 <= 0.7)){
                        sum1_4 += myo; sum2_4 += p1; sum3_4 += p2; sum4_4 += p3; sum5_4 += p4;
                        count4++;
                    } else if(0.7 < d11 && 0.7 < d12 && 0.7 < d21 && 0.7 < d22){
                        sum1_5 += myo; sum2_5 += p1; sum3_5 += p2; sum4_5 += p3; sum5_5 += p4;
                        count5++;
                    }*/ else if(0.4 < d11 && 0.4 < d12 && 0.4 < d21 && 0.4 < d22){
                        sum1_5 += myo; sum2_5 += p1; sum3_5 += p2; sum4_5 += p3; sum5_5 += p4;
                        count5++;
                    }
                } else if(typefile == -1){
                    //TODO: NNNN FILE RANGES AND STUFF
                }
            }
            DecimalFormat df = new DecimalFormat("#.##");
            double d1, d2, d3, d4, d5;
            d1 = sum1_1/(double)count1;
            d2 = sum2_1/(double)count1;
            d3 = sum3_1/(double)count1;
            d4 = sum4_1/(double)count1;
            d5 = sum5_1/(double)count1;
            System.out.print("Range 1: ");
            System.out.println("(Count:" + count1 + ") My:" + df.format(d1) + "\tP1:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tP2:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)\tP3:" + df.format(d4) + " (" + df.format(percentage_differance(d1, d4)) + "%)\tP4:" + df.format(d5) + "(" + df.format(percentage_differance(d1, d5)) + "%)");
            System.out.println("=================================");
            d1 = sum1_2/(double)count2;
            d2 = sum2_2/(double)count2;
            d3 = sum3_2/(double)count2;
            d4 = sum4_2/(double)count2;
            d5 = sum5_2/(double)count2;
            System.out.print("Range 2: ");
            System.out.println("(Count:" + count2 + ") My:" + df.format(d1) + "\tP1:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tP2:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)\tP3:" + df.format(d4) + " (" + df.format(percentage_differance(d1, d4)) + "%)\tP4:" + df.format(d5) + "(" + df.format(percentage_differance(d1, d5)) + "%)");
            System.out.println("=================================");
            d1 = sum1_3/(double)count3;
            d2 = sum2_3/(double)count3;
            d3 = sum3_3/(double)count3;
            d4 = sum4_3/(double)count3;
            d5 = sum5_3/(double)count3;
            System.out.print("Range 3: ");
            System.out.println("(Count:" + count3 + ") My:" + df.format(d1) + "\tP1:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tP2:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)\tP3:" + df.format(d4) + " (" + df.format(percentage_differance(d1, d4)) + "%)\tP4:" + df.format(d5) + "(" + df.format(percentage_differance(d1, d5)) + "%)");
            System.out.println("=================================");
            /*d1 = sum1_4/(double)count4;
            d2 = sum2_4/(double)count4;
            d3 = sum3_4/(double)count4;
            d4 = sum4_4/(double)count4;
            d5 = sum5_4/(double)count4;
            System.out.print("Range 4: ");
            System.out.println("(Count:" + count4 + ") My:" + df.format(d1) + "\tP1:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tP2:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)\tP3:" + df.format(d4) + " (" + df.format(percentage_differance(d1, d4)) + "%)\tP4:" + df.format(d5) + "(" + df.format(percentage_differance(d1, d5)) + "%)");
            System.out.println("=================================");*/
            d1 = sum1_5/(double)count5;
            d2 = sum2_5/(double)count5;
            d3 = sum3_5/(double)count5;
            d4 = sum4_5/(double)count5;
            d5 = sum5_5/(double)count5;
            System.out.print("Range 5: ");
            System.out.println("(Count:" + count5 + ") My:" + df.format(d1) + "\tP1:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tP2:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)\tP3:" + df.format(d4) + " (" + df.format(percentage_differance(d1, d4)) + "%)\tP4:" + df.format(d5) + "(" + df.format(percentage_differance(d1, d5)) + "%)");
            System.out.println("=================================");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    void analyze_full_correlation_2D(String filepath, int typefile){
        BufferedReader br = null;
        try {
            //Read file PP or NN
            String line;
            double sum1_1 = 0; double sum2_1 = 0; double sum3_1 = 0;
            double sum1_2 = 0; double sum2_2 = 0; double sum3_2 = 0;
            double sum1_3 = 0; double sum2_3 = 0; double sum3_3 = 0;
            double sum1_4 = 0; double sum2_4 = 0; double sum3_4 = 0;
            double sum1_5 = 0; double sum2_5 = 0; double sum3_5 = 0;
            int count1 = 0; int count2 = 0; int count3 = 0; int count4 = 0; int count5 = 0;
            int count = 0;
            br = new BufferedReader(new FileReader(filepath));
            boolean allPositive = true;
            while ((line = br.readLine()) != null) {
                allPositive = true;
                int positive_channels = 0;
                count++;
                StringTokenizer st = new StringTokenizer(line.replaceAll(",", "."));
                //differences of parameters
                String params = st.nextToken().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", " ");
                StringTokenizer stp = new StringTokenizer(params);
                double d11=0, d12=0, d21=0, d22=0;
                while(stp.hasMoreTokens()){
                    d11 = Double.parseDouble(stp.nextToken());
                    d12 = Double.parseDouble(stp.nextToken());
                    d21 = Double.parseDouble(stp.nextToken());
                    d22 = Double.parseDouble(stp.nextToken());
                    if(d11<0 || d12<0 || d21<0 || d22<0){
                        allPositive = false;
                        break;
                    }
                }
                if(allPositive){
                    double myo = Double.parseDouble(st.nextToken());
                    st.nextToken();
                    double opt_wam = Double.parseDouble(st.nextToken());
                    double opt_am = Double.parseDouble(st.nextToken());
                    
                    if(d11 <= 0.2 && d12 <= 0.2 && d21 <= 0.2 && d22 <= 0.2){
                        sum1_1 += myo; sum2_1 += opt_wam; sum3_1 += opt_am;
                        count1++;
                    } else if((0.2 < d11 && d11 <= 0.4)&&(0.2 < d12 && d12 <= 0.4)&&(0.2 < d21 && d21 <= 0.4)&&(0.2 < d22 && d22 <= 0.4)){
                        sum1_2 += myo; sum2_2 += opt_wam; sum3_2 += opt_am;
                        count2++;
                    } /*else if((0.3 < d11 && d11 <= 0.8)&&(0.3 < d12 && d12 <= 0.8)&&(0.3 < d21 && d21 <= 0.8)&&(0.3 < d22 && d22 <= 0.8)){
                        sum1_3 += myo; sum2_3 += opt_wam; sum3_3+= opt_am;
                        count3++;
                    } else if((0.5 < d11 && d11 <= 0.7)&&(0.5 < d12 && d12 <= 0.7)&&(0.5 < d21 && d21 <= 0.7)&&(0.5 < d22 && d22 <= 0.7)){
                        sum1_4 += myo; sum2_4 += opt_wam; sum3_4 += opt_am;
                        count4++;
                    } */else if(0.3 < d11 && 0.3 < d12 && 0.3 < d21 && 0.3 < d22){
                        sum1_5 += myo; sum2_5 += opt_wam; sum3_5 += opt_am;
                        count5++;
                    } /*else if(0.4 < d11 && 0.4 < d12 && 0.4 < d21 && 0.4 < d22){
                        sum1_5 += myo; sum2_5 += opt_wam; sum3_5 += opt_am;
                        count5++;
                    }*/
                }
            }
            DecimalFormat df = new DecimalFormat("#.##");
            double d1, d2, d3, d4, d5;
            d1 = sum1_1/(double)count1;
            d2 = sum2_1/(double)count1;
            d3 = sum3_1/(double)count1;            
            System.out.print("Range 1: ");
            System.out.println("(Count:" + count1 + ") My:" + df.format(d1) + "\tOPT_WAM:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tOP_AM:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)");
            System.out.println("=================================");
            d1 = sum1_2/(double)count2;
            d2 = sum2_2/(double)count2;
            d3 = sum3_2/(double)count2;
            System.out.print("Range 2: ");
            System.out.println("(Count:" + count2 + ") My:" + df.format(d1) + "\tOPT_WAM:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tOP_AM:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)");
            System.out.println("=================================");
            d1 = sum1_3/(double)count3;
            d2 = sum2_3/(double)count3;
            d3 = sum3_3/(double)count3;
            System.out.print("Range 3: ");
            System.out.println("(Count:" + count3 + ") My:" + df.format(d1) + "\tOPT_WAM:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tOP_AM:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)");
            System.out.println("=================================");
            d1 = sum1_4/(double)count4;
            d2 = sum2_4/(double)count4;
            d3 = sum3_4/(double)count4;
            System.out.print("Range 4: ");
            System.out.println("(Count:" + count4 + ") My:" + df.format(d1) + "\tOPT_WAM:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tOP_AM:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)");
            System.out.println("=================================");
            d1 = sum1_5/(double)count5;
            d2 = sum2_5/(double)count5;
            d3 = sum3_5/(double)count5;
            System.out.print("Range 5: ");
            System.out.println("(Count:" + count5 + ") My:" + df.format(d1) + "\tOPT_WAM:" + df.format(d2) + " (" + df.format(percentage_differance(d1, d2)) + "%)\tOP_AM:" + df.format(d3) + " (" + df.format(percentage_differance(d1, d3)) + "%)");
            System.out.println("=================================");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {                
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void analyze_simple_file(String filepath){
        BufferedReader br = null;
        int myopic_count = 0, whittle_count = 0;
        int total = 0;
        double myopic_sum = 0, whittle_sum = 0, diff_sum = 0;
        try {
            String line;
            br = new BufferedReader(new FileReader(filepath));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                //line = line.replaceAll(",", ".");
                total++;
                StringTokenizer st = new StringTokenizer(line);
                //the parameters token
                st.nextToken();
                //the percentages tokens                
                StringTokenizer inner_st = new StringTokenizer(st.nextToken().replaceAll(",", "."), "%");
                double myo = Double.parseDouble(inner_st.nextToken());                  
                myopic_sum += myo;
                inner_st = new StringTokenizer(st.nextToken().replaceAll(",", "."), "%");
                double whittle = Double.parseDouble(inner_st.nextToken());
                whittle_sum += whittle;
                double diff = myo - whittle;
                diff_sum += diff;
                if(myo > whittle){
                    myopic_count++;
                } else{
                    whittle_count++;
                }              
            }
            DecimalFormat df = new DecimalFormat("#.##");            
            System.out.println("Myopic: " + myopic_count + " (" + df.format((double)myopic_count*100/(double)total) + "%) Avg.: " + (double)myopic_sum/(double)total + " - Whittle: " + whittle_count + " (" + df.format((double)whittle_count*100/(double)total) + "%) Avg.: " + (double)whittle_sum/(double)total);
            String winner = "";
            if(diff_sum>0) winner = "MYOPIC";
            else winner = "WHITTLE";
            System.out.println("Avg. Diff: " + diff_sum/(double)total + "(" + winner + ")");
        } catch (IOException e) {
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
            }
        }
    }
    
    void analyze_complex_file(String filepath){
        BufferedReader br = null;
        int myopic_count = 0, p1_count = 0, p2_count = 0, p3_count = 0, p4_count = 0;
        int total = 0;
        double myopic_sum = 0, p1_sum = 0, p2_sum = 0, p3_sum = 0, p4_sum = 0;
        try {
            String line;
            br = new BufferedReader(new FileReader(filepath));
            line = br.readLine();            
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(",", ".");
                total++;
                StringTokenizer st = new StringTokenizer(line);
                st.nextToken();
                StringTokenizer inner_st = new StringTokenizer(st.nextToken(), "%");
                double myo = Double.parseDouble(inner_st.nextToken());                  
                myopic_sum += myo;
                inner_st = new StringTokenizer(st.nextToken(), "%");
                double p1 = Double.parseDouble(inner_st.nextToken());
                p1_sum += p1;
                inner_st = new StringTokenizer(st.nextToken(), "%");
                double p2 = Double.parseDouble(inner_st.nextToken());
                p2_sum += p2;
                inner_st = new StringTokenizer(st.nextToken(), "%");
                double p3 = Double.parseDouble(inner_st.nextToken());
                p3_sum += p3;
                inner_st = new StringTokenizer(st.nextToken(), "%");
                double p4 = Double.parseDouble(inner_st.nextToken());
                p4_sum += p4;
                if(myo == Math.max(myo, Math.max(p1, Math.max(p2, Math.max(p3, p4))))){
                    myopic_count++;
                } else if(p1 == Math.max(myo, Math.max(p1, Math.max(p2, Math.max(p3, p4))))){
                    p1_count++;
                } else if(p2 == Math.max(myo, Math.max(p1, Math.max(p2, Math.max(p3, p4))))){
                    p2_count++;
                } else if(p3 == Math.max(myo, Math.max(p1, Math.max(p2, Math.max(p3, p4))))){
                    p3_count++;
                } else{
                    p4_count++;
                }           
            }
            DecimalFormat df = new DecimalFormat("#.##");            
            System.out.println("Myopic: " + myopic_count + " (" + df.format((double)myopic_count*100/(double)total) + "%) Avg.: " + (double)myopic_sum/(double)total + " - P1: " + p1_count + " (" + df.format((double)p1_count*100/(double)total) + "%) Avg.: " + (double)p1_sum/(double)total + " - P2: " + p2_count + " (" + df.format((double)p2_count*100/(double)total) + "%) Avg.: " + (double)p2_sum/(double)total + " - P3: " + p3_count + " (" + df.format((double)p3_count*100/(double)total) + "%) Avg.: " + (double)p3_sum/(double)total + " - P4: " + p4_count + " (" + df.format((double)p4_count*100/(double)total) + "%) Avg.: " + (double)p4_sum/(double)total);
        } catch (IOException e) {
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
            }
        }
    }
    
    void prepare_simple(int s, double[][] opt, double[][] myp, double[][] w){
        SIZE_SIMPLE = s;
        optimal_simple = opt;
        myopic_simple = myp;
        whittle_simple = w;        
    }
    
    void prepare_complex(int s, int[][][][] opt, int[][][][] myo, int[][][][] a,int[][][][] b, int[][][][] c, int[][][][] d){
        SIZE_COMPLEX = s;
        optimal = opt;
        myopic = myo;
        p1 = a;
        p2 = b;
        p3 = c;
        p4 = d;                
    }
    
    void calculate_scores_simple(int type, boolean print, boolean output, boolean singleAck, String folder, double a1, double b1, double a2, double b2){
        
        int scoreM, scoreW;
        scoreM = scoreW = 0;
        
        for(int i = 0; i < SIZE_SIMPLE; i++){
            for(int j = 0; j < SIZE_SIMPLE; j++){
                if(optimal_simple[i][j] == myopic_simple[i][j]){
                    scoreM++;
                }
                if(optimal_simple[i][j] == whittle_simple[i][j]){
                    scoreW++;
                }
            }            
        }
        
        double total = Math.pow(SIZE_SIMPLE, 2);
        double perM = (double)(scoreM * 100)/total;
        double perW = (double)(scoreW * 100)/total;
        DecimalFormat df = new DecimalFormat("#.##");
        
        if(print){
            System.out.println();
            System.out.println("----------------------------------------------------");
            System.out.println("            SCORES COMPARISON: SIMPLE           ");
            System.out.println("Total  : " + (int)total);
            System.out.println("Score Myopic : " + scoreM + " - Percentage = " + df.format(perM) + "%");
            System.out.println("Score Whittle: " + scoreW + " - Percentage = " + df.format(perW) + "%");
            System.out.println("----------------------------------------------------");
        }
        
        if(output){
            boolean NEW = false;
            String newLine = System.getProperty("line.separator");
            try{
                String filename;
                String ext;
                if(type == 2){
                    ext = "PP";
                } else if(type == -2){
                    ext = "NN";
                } else if(type == 1){
                    ext = "PN";
                } else{
                    ext = "NP";
                }
                if(!singleAck){
                    filename = folder + "STATS_SIMPLE_" + ext + ".txt";
                } else{
                    filename = folder + "STATS_SIMPLE_1ACK_" + ext + ".txt";
                }
                File file = new File(filename);
    		if(!file.exists()){
                    file.createNewFile();
                    NEW = true;
    		}
                
                FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
                try (BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
                    if(NEW){
                        String header = "PARAMS\t\tMYOPIC\tWHITTLE" + newLine;
                        bufferWritter.write(header);
                    }
                    String line = "α1:" + a1 + ",1-ß1:" + b1 +",α2:" + a2 + ",1-ß2:" + b2 + ",\t" + df.format(perM) + "%\t" + df.format(perW) + "%" + newLine;
                    bufferWritter.write(line);
                    bufferWritter.close();
                    fileWritter.close();
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    void calculate_scores_complex(double diff11, double diff12, double diff21, double diff22, int type1, int type2, boolean print, boolean output, boolean singleAck, String folder){
        int score1, score2, score3, score4, scoreMyopic;
        score1 = score2 = score3 = score4 = scoreMyopic = 0;
        
        for(int i = 0; i < SIZE_COMPLEX; i++){
            for(int j = 0; j < SIZE_COMPLEX; j++){
                for(int k = 0; k < SIZE_COMPLEX; k++){
                    for(int l = 0; l < SIZE_COMPLEX; l++){
                        if(optimal[i][j][k][l] == myopic[i][j][k][l]){
                            scoreMyopic++;
                        }
                        if(optimal[i][j][k][l] == p1[i][j][k][l]){
                            score1++;
                        }
                        if(optimal[i][j][k][l] == p2[i][j][k][l]){
                            score2++;
                        }
                        if(optimal[i][j][k][l] == p3[i][j][k][l]){
                            score3++;
                        }
                        if(optimal[i][j][k][l] == p4[i][j][k][l]){
                            score4++;
                        }
                    }
                }
            }
        }
        
        double total = Math.pow(SIZE_COMPLEX, 4);
        double perM = (double)(scoreMyopic * 100)/total;
        double per1 = (double)(score1 * 100)/total;
        double per2 = (double)(score2 * 100)/total;
        double per3 = (double)(score3 * 100)/total;
        double per4 = (double)(score4 * 100)/total;
        DecimalFormat df = new DecimalFormat("#.##");
        
        if(print){
            System.out.println();
            System.out.println("----------------------------------------------------");
            System.out.println("            SCORES COMPARISON: COMPLEX           ");
            System.out.println("Total  : " + (int)total);
            System.out.println("Score M: " + scoreMyopic + " - Percentage = " + df.format(perM) + "%");
            System.out.println("Score 1: " + score1 + " - Percentage = " + df.format(per1) + "%");
            System.out.println("Score 2: " + score2 + " - Percentage = " + df.format(per2) + "%");
            System.out.println("Score 3: " + score3 + " - Percentage = " + df.format(per3) + "%");
            System.out.println("Score 4: " + score4 + " - Percentage = " + df.format(per4) + "%");  
            System.out.println("----------------------------------------------------");
        }
    
        if(output){
            boolean NEW = false;
            String newLine = System.getProperty("line.separator");
            try{
                String filename;
                String ext1, ext2;
                if(type1 == 2){
                    ext1 = "PP";
                } else if(type1 == -2){
                    ext1 = "NN";
                } else if(type1 == 1){
                    ext1 = "PN";
                } else{
                    ext1 = "NP";
                }
                if(type2 == 2){
                    ext2 = "PP";
                } else if(type2 == -2){
                    ext2 = "NN";
                } else if(type2 == 1){
                    ext2 = "PN";
                } else{
                    ext2 = "NP";
                }
                if(!singleAck){
                    filename = folder + "STATS_COMPLEX_" + ext1 + ext2 + ".txt";
                } else{
                    filename = folder + "STATS_COMPLEX_1ACK_" + ext1 + ext2 + ".txt";
                }
                File file = new File(filename);
    		if(!file.exists()){
                    file.createNewFile();
                    NEW = true;
    		}
                
                FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
                try (BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
                    if(NEW){
                        String header = "MYOP\tP1\tP2\tP3\tP4" + newLine;
                        bufferWritter.write(header);
                    }
                    String line = "" + df.format(diff11) + "/" + df.format(diff12) + "/" + df.format(diff21) + "/" + df.format(diff22) + "\t" + df.format(perM) + "%\t" + df.format(per1) + "%\t" + df.format(per2) + "%\t" + df.format(per3) + "%\t" + df.format(per4) + "%" + newLine;
                    bufferWritter.write(line);
                    bufferWritter.close();
                    fileWritter.close();
                }
                File file2 = new File("comparison.txt");
                FileWriter fw = new FileWriter(file2.getAbsolutePath(),true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(df.format(perM));
                bw.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }        
    }
    
    double percentage_differance(double a, double b){
        return (Math.abs(a-b)/((a+b)/2.0))*100.0;
    }
    
}
