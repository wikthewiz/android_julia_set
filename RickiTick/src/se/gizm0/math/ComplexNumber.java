package se.gizm0.math;


public class ComplexNumber{
	 
    private double a, b;
    public ComplexNumber(double a, double b){
        this.a = a;
        this.b = b;
    }
 
    public ComplexNumber square(){
        return new ComplexNumber(this.a*this.a - this.b*this.b, 2*this.a*this.b);
    }
 
    public ComplexNumber add(ComplexNumber cn){
        return new ComplexNumber(this.a+cn.a, this.b+cn.b);
    }
 
    public double magnitude(){
        return a*a+b*b;
    }
}