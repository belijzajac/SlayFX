package com.slayfx.logic.player;

import com.slayfx.logic.tiles.HexColor;

public class Player {
    private String m_name;
    private int money;
    private HexColor m_color;

    public Player(String id, HexColor color){
        m_name = id;
        money = 0;
        m_color = color;
    }

    public String getName(){ return m_name; }
    public HexColor getColor(){ return m_color; }
}
