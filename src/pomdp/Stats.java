package pomdp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
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
    
    void analyze_simple_file(String filepath){
        BufferedReader br = null;
        int myopic_count = 0, whittle_count = 0;
        int total = 0;
        double myopic_sum = 0, whittle_sum = 0;
        try {
            String line;
            br = new BufferedReader(new FileReader(filepath));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                total++;
                StringTokenizer st = new StringTokenizer(line);
                StringTokenizer inner_st = new StringTokenizer(st.nextToken(), "%");
                double myo = Double.parseDouble(inner_st.nextToken());                  
                myopic_sum += myo;
                inner_st = new StringTokenizer(st.nextToken(), "%");
                double whittle = Double.parseDouble(inner_st.nextToken());
                whittle_sum += whittle;
                if(myo > whittle){
                    myopic_count++;
                } else{
                    whittle_count++;
                }              
            }
            DecimalFormat df = new DecimalFormat("#.##");            
            System.out.println("Myopic: " + myopic_count + " (" + df.format((double)myopic_count*100/(double)total) + "%) Avg.: " + (double)myopic_sum/(double)total + " - Whittle: " + whittle_count + " (" + df.format((double)whittle_count*100/(double)total) + "%) Avg.: " + (double)whittle_sum/(double)total);
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
                total++;
                StringTokenizer st = new StringTokenizer(line);
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
    
    void calculate_scores_simple(boolean print, boolean output, boolean singleAck, String folder){
        
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
                if(!singleAck){
                    filename = folder + "STATS_SIMPLE.txt";
                } else{
                    filename = folder + "STATS_SIMPLE_1ACK.txt";
                }
                File file =new File(filename);
    		if(!file.exists()){
                    file.createNewFile();
                    NEW = true;
    		}
                
                FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
                try (BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
                    if(NEW){
                        String header = "MYOPIC\tWHITTLE" + newLine;
                        bufferWritter.write(header);
                    }
                    String line = "" + df.format(perM) + "%\t" + df.format(perW) + "%" + newLine;
                    bufferWritter.write(line);
                    bufferWritter.close();
                    fileWritter.close();
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    void calculate_scores_complex(boolean output, boolean singleAck, String folder){
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
    
        if(output){
            boolean NEW = false;
            String newLine = System.getProperty("line.separator");
            try{
                String filename;
                if(!singleAck){
                    filename = folder + "STATS_COMPLEX.txt";
                } else{
                    filename = folder + "STATS_COMPLEX_1ACK.txt";
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
                    String line = "" + df.format(perM) + "%\t" + df.format(per1) + "%\t" + df.format(per2) + "%\t" + df.format(per3) + "%\t" + df.format(per4) + "%" + newLine;
                    bufferWritter.write(line);
                    bufferWritter.close();
                    fileWritter.close();
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }        
    }
}
