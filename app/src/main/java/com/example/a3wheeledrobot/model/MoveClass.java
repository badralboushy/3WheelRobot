package com.example.a3wheeledrobot.model;

public class  MoveClass {

    public static double[] motorVal3w(double x , double y , double theta , double R , double r){
        double A[][] =  {
                {-1/3.0, Math.sqrt(3)/3, R},
                {-1/3.0, Math.sqrt(3)/3, R},
                {2/3.0, 0, R}
        };

        double res[] =new double[3];
        res[0] = (A[0][0]*x + A[0][1]*y + A[0][2]*theta)/r;
        res[1] = (A[1][0]*x + A[1][1]*y + A[0][2]*theta)/r ;
        res[2] = (A[2][0]*x + A[2][1]*y + A[2][2]*theta)/r ;
        return res ;

    }
    public static double[] motorVal4w(double x , double y , double theta , double R , double r){
        double A[][] =  {
                {0, 1, R},
                {-1,0, R},
                {0,-1, R},
                {1,0, R}
        };
        double res[] =new double[4];
        res[0] = (A[0][0]*x + A[0][1]*y + A[0][2]*theta)/r;
        res[1] = (A[1][0]*x + A[1][1]*y + A[1][2]*theta)/r ;
        res[2] = (A[2][0]*x + A[2][1]*y + A[2][2]*theta)/r ;
        res[3] = (A[3][0]*x + A[3][1]*y + A[3][2]*theta)/r ;
        return res ;

    }
    public static double[] motorVal2w(double x , double y , double R , double r){
    double res[] = new double[2];
    res[0] = (y+R*x)/r ;
    res[1] = (y-R*x)/r ;
    return res ;

    }

}
