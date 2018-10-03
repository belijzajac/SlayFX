package com.slayfx.logic.tiles;

public class Point {
    private double m_x, m_y;

    Point(double x, double y){
        setX(x);
        setY(y);
    }

    public double getX() {
        return m_x;
    }

    private void setX(double x) {
        assert( x <= 500.0f && x >= -500.0f );
        this.m_x = x;
    }

    public double getY() {
        return m_y;
    }

    private void setY(double y) {
        assert( y <= 500.0f && y >= -500.0f );
        this.m_y = y;
    }
}
