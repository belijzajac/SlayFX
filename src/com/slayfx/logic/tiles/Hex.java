package com.slayfx.logic.tiles;

import java.util.ArrayList;

public class Hex extends GameObject {
    private ArrayList<Point> m_vertices_coords; // holds vertices coordinates
    private HexColor m_color;                   // hex color
    private int m_radius;                       // radius

    private String m_owner;   // player that owns the hex
    private HexState m_state; // building, troop unit or sth

    public Hex(HexColor color, int x_pos, int y_pos){
        super(); // call the parent constructor
        this.setCoords(new Point(x_pos, y_pos));
        this.setID( String.valueOf(this.getCoords().getX()) + '_' + String.valueOf(this.getCoords().getY()) );

        m_owner = "abandoned";    // player that owns the hex
        m_state = HexState.EMPTY; // hex state

        m_vertices_coords = new ArrayList<Point>();
        m_color = color;
        m_radius = 20;

        // Calculate vertices coordinates
        calculateVertices();
    }

    private void calculateVertices(){
        for(int i = 0; i<6; i++){
            double x = this.getCoords().getX() + m_radius * Math.cos(2 * Math.PI * i / 6 + 33);
            double y = this.getCoords().getY() + m_radius * Math.sin(2 * Math.PI * i / 6 + 33);
            m_vertices_coords.add(new Point(x, y));
        }
    }

    //Method to display the attribute values of this Object
    @Override
    public String toString(){
        return "m_ID: " + this.getID() + " color: " + this.m_color.toString();
    }

    public HexColor getColor(){
        return m_color;
    }
    public void changeColor(HexColor color){
        m_color = color;
    }
    public int getRadius(){ return m_radius; }
    public ArrayList<Point> getVertices(){ return m_vertices_coords; }
    public void changeOwner(String id){ m_owner = id; }
    public String getOwner(){ return m_owner; }
    public void changeState(HexState state){ m_state = state; }
    public HexState getState(){ return m_state; }
}