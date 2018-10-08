package com.slayfx.logic.player;

import com.slayfx.logic.tiles.HexColor;

public class Player {
    private String m_name;
    private int m_money;
    private HexColor m_color;

    public Player(String id, HexColor color){
        m_name = id;
        m_money = 50;
        m_color = color;
    }

    public String getName(){ return m_name; }
    public HexColor getColor(){ return m_color; }
    public int getMoney(){ return m_money; }
    public void setMoney(int money){ m_money = money; }
}
