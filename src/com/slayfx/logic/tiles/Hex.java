package com.slayfx.logic.tiles;

import java.util.ArrayList;

public class Hex implements GameObject {
    private String m_ID;                                // this helps identifying which hex is which
    private Point m_center_coords;              // coordinates
    private ArrayList<Point> m_vertices_coords; // holds vertices coordinates
    private HexColor m_color;                   // hex color
    private int m_radius;                       // radius

    public Hex(HexColor color, int x_pos, int y_pos){
        m_center_coords = new Point(x_pos, y_pos);
        m_ID = String.valueOf(m_center_coords.getX()) + '_' + String.valueOf(m_center_coords.getY());
        m_vertices_coords = new ArrayList<Point>();
        m_color = color;
        m_radius = 20;

        // Calculate vertices coordinates
        calculateVertices();
    }

    private void calculateVertices(){
        for(int i = 0; i<6; i++){
            double x = m_center_coords.getX() + m_radius * Math.cos(2 * Math.PI * i / 6 + 33);
            double y = m_center_coords.getY() + m_radius * Math.sin(2 * Math.PI * i / 6 + 33);

            m_vertices_coords.add(new Point(x, y));
        }
    }

    public Point getCenterCoords(){
        return m_center_coords;
    }

    public String getID(){ return m_ID; }

    public HexColor getColor(){
        return m_color;
    }

    public void changeColor(HexColor color){
        m_color = color;
    }

    public int getRadius(){ return m_radius; }

    public ArrayList<Point> getVertices(){ return m_vertices_coords; }
}
