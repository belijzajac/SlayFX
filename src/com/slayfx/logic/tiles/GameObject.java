package com.slayfx.logic.tiles;

public class GameObject {
    private String m_ID;    // this helps identifying which tile is which
    private Point m_coords; // coordinates

    public GameObject(){
        m_coords = new Point(0, 0);
    }

    public Point getCoords(){ return m_coords; }
    public void setCoords(Point point){ m_coords = point; }
    public String getID(){ return m_ID; }
    public void setID(String id){ m_ID = id; }
}