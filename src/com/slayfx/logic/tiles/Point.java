package com.slayfx.logic.tiles;

public class Point {
    private int m_x, m_y;

    Point(int x, int y){
        m_x = x;
        m_y = y;
    }

    public int getX() {
        return m_x;
    }

    public void setX(int m_x) {
        this.m_x = m_x;
    }

    public int getY() {
        return m_y;
    }

    public void setY(int m_y) {
        this.m_y = m_y;
    }
}
