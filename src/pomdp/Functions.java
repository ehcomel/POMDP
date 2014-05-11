/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pomdp;

/**
 *
 * @author Amine
 */
public class Functions {
    
    int decimals;
    
    public Functions(int dec){
        decimals = dec;
    }
    
    double tao(double w, double p11, double p01){
        return w * p11 + (1-w) * p01;
    }
    
    double tao_pow(double pow, double w, double p11, double p01){
        double rec = w;
        for(int i = 0; i < pow; i++){
            rec = tao(rec, p11, p01);
        }
        return rec;
    }
    
    double round(double v){
        return Math.round(v * (double)decimals)/(double)decimals;
    }
}
