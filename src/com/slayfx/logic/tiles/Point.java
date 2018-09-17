package com.slayfx.logic.tiles;

public class Point {
    private double m_x, m_y;

    Point(double x, double y){
        m_x = x;
        m_y = y;
    }

    public double getX() {
        return m_x;
    }

    public void setX(double x) {
        this.m_x = x;
    }

    public double getY() {
        return m_y;
    }

    public void setY(double y) {
        this.m_y = y;
    }
}
