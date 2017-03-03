/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
 * @author thegoodhen
 */
public class FloatPoint {

    double x = 0;
    double y = 0;

    public FloatPoint(double x, double y) {
	this.x = x;
	this.y = y;
    }

    public static FloatPoint getLeftMostPoint(FloatPoint... fpa) {
	double minX=Double.POSITIVE_INFINITY;
	FloatPoint returnPoint=null;
	for (FloatPoint fp : fpa) {
		if(fp.x<minX)
		{
		    minX=fp.x;
		    returnPoint=fp;
		}
	}
	return returnPoint;
    }


    public static FloatPoint getRightMostPoint(FloatPoint... fpa) {
	double maxX=Double.NEGATIVE_INFINITY;
	FloatPoint returnPoint=null;
	for (FloatPoint fp : fpa) {
		if(fp.x>maxX)
		{
		    maxX=fp.x;
		    returnPoint=fp;
		}
	}
	return returnPoint;
    }


    public static FloatPoint getUpMostPoint(FloatPoint... fpa) {
	double minY=Double.POSITIVE_INFINITY;
	FloatPoint returnPoint=null;
	for (FloatPoint fp : fpa) {
		if(fp.y<minY)
		{
		    minY=fp.y;
		    returnPoint=fp;
		}
	}
	return returnPoint;
    }


    public static FloatPoint getDownMostPoint(FloatPoint... fpa) {
	double maxY=Double.NEGATIVE_INFINITY;
	FloatPoint returnPoint=null;
	for (FloatPoint fp : fpa) {
		if(fp.y>maxY)
		{
		    maxY=fp.y;
		    returnPoint=fp;
		}
	}
	return returnPoint;
    }

    FloatPoint(FloatPoint p2) {
	this.x=p2.x;
	this.y=p2.y;
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }
}
