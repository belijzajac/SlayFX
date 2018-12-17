package com.slayfx.logic.player;

import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;
import java.util.HashSet;

public class Player {
    private String m_name;
    private int m_money;
    private HexColor m_color;
    private HashSet<String> m_owned_items; // to keep track of active items
    private boolean m_isDead;
    private Hex homeHex;

    public Player(String id, HexColor color){
        m_name = id;
        m_money = 50;
        m_color = color;
        m_owned_items = new HashSet<String>();
        m_isDead = false;
    }

    public void moveUnits(Hex hex){}
    public void buyItems(){}

    public String getName(){ return m_name; }
    public HexColor getColor(){ return m_color; }
    public int getMoney(){ return m_money; }
    public void setMoney(int money){ m_money = money; }
    public HashSet<String> getItemsHistory(){
        return m_owned_items;
    }
    public boolean isDead(){ return m_isDead; }
    public void endLife(){ m_isDead = true; }
    public void setHomeHex(Hex randomHexFromGameBoard){ homeHex = randomHexFromGameBoard; }
    public Hex getHomeHex(){ return homeHex; }
}
