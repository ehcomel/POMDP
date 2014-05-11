/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pomdp;

/**
 *
 * @author Amine Benabdeljalil
 */
public class Whittle {
    
    public double p11;
    public double p01;
    
    public double s;
    public double I;
    public double alpha;
    public double R;
    
    public Whittle(double p_11, double p_01, double discount/*, double reward*/){
        p11 = p_11;
        p01 = p_01;
        s = p_11 - p_01;
        I = p_01/(1.0-s);
        alpha = discount;
        //R = reward;
        R = 1;
        
    }
    
    public double calculate_index(double p){
        double k = k(p);
        double index = 0.0;
        
        if(s == 0){
            index = p * R;
        } else if(s == 1){
            index = (p * R)/(1 - alpha * (1 - p));
        } else if(s > 0 && s < 1){
            if(p >= p11 || p <= p01){
                index = p * R;
            } else if(I <= p && p < p11){
                index = (p * R)/(1 - alpha * (p11 - p));
            } else if(p01 < p && p < I){
                index = R * (A_k(p)-(1-p)*B_k(p))/(A_k(p)-(1-p)*C_k(p));
            }
        } else if(s == -1){
            if(p >= 1/2){
                index = R * (alpha+p*(1-alpha))/(1+alpha*(1-alpha)*(1-p));
            } else if(p < 1/2){
                index = R * p / (1 - alpha * p);
            }            
        } else if(s > -1 && s < 0){
            if(p >= p01 || p <= p11){
                index = p * R;
            } else if(tao(p11) <= p && p < p01){
                index = R * (p + alpha * (p01 - p)) / (1 + alpha * (p01 - p));
            } else if(I <= p && p < tao(p11)){
                index = R * (p + alpha * (p01 - p)) / (1 + alpha * (1 - alpha) * (p01 - p) - Math.pow(alpha, 2) * p11 * s);
            } else if(p11 < p && p < I){
                index = R * p / (1 - alpha * (p - p11));
            }
        }
                
        return index;
    }
    
    public double k(double p){
        
        double _k = Math.ceil((Math.log(1 - (p/I)))/(Math.log(s))) - 2.0;
        
        return _k;
    }
    
    
    public double A_k(double p){
        double k = k(p);
        double numerator = (1 - alpha * p11) * B_k(p) + Math.pow(alpha, k + 2) * (1 - alpha) * /*not sure if: */ tao_pow(k+1, p01) /* or  f_n(k+1, p01)*/;
        double denominator = 1 - alpha * s;
        
        return numerator / denominator;
    }
    public double B_k(double p){
        return 1.0 - Math.pow(alpha, (k(p) + 2));
    }
    public double C_k(double p){
        return alpha - Math.pow(alpha, (k(p) + 2));
    }
    
    private double tao(double w){
        return w * p11 + (1-w) * p01;
    }
    
    public double tao_pow(double pow, double w){
        double rec = w;
        for(int i = 0; i < pow; i++){
            rec = tao(rec);
        }
        return rec;
    }
    
    public double f_n(double n, double p){
        double result = p * ((1 - Math.pow(s, n+1))/(1 - s));
        return result;
    }
    
}
