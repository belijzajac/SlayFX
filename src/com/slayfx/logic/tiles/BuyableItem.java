package com.slayfx.logic.tiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class BuyableItem extends GameObject {
    public static int m_cost;
    private String m_owner;  // player who owns this item
    private int m_moveCount; // how far diagonally can the item be moved
    private int m_damage;    // damage the item deals
    @FXML Label m_label;
    @FXML Image m_image;

    public BuyableItem(String owner){
        super();
        m_owner = owner;
    }

    public void move(int x, int y){}

    public int getMovementCount(){ return m_moveCount; }
    public void setMovementCount(int count){ m_moveCount = count; }

    public int getDamage(){ return m_damage; }
    public void setDamage(int dmg){ m_damage = dmg; }

    public Label getLabel(){ return m_label; }
}
