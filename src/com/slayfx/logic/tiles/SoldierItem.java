package com.slayfx.logic.tiles;

public class SoldierItem extends BuyableItem {
    public SoldierItem(Point coords, String id, String owner){
        super(owner, BuyableItem.soldier_img, coords);
        setID(id);

        // Init fields:
        m_cost = 6;             // cost 6 coins
        setMovementCount(3);    // moves every 3 blocks
        setDamage(2);           // can kill peasants
    }

    @Override
    public void move(int x, int y){
        m_label.relocate(x, y);
    }
}
