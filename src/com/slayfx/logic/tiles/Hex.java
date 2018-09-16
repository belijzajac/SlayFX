package com.slayfx.logic.tiles;

public class Hex implements GameObject {
    private Point m_coords;   // coordinates
    private HexColor m_color; // hex color
    
    public Hex(HexColor color, int x_pos, int y_pos){
        m_color = color;
        m_coords = new Point(x_pos, y_pos);
    }

    public Point getCoords(){
        return m_coords;
    }

    public HexColor getColor(){
        return m_color;
    }

    public void changeColor(HexColor color){
        m_color = color;
    }
}
