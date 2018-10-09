package com.slayfx.logic.tiles;

public class PeasantItem extends BuyableItem {
    public PeasantItem(Point coords, String id, String owner){
        super(owner, BuyableItem.peasant_img, coords);
        setID(id);

        // Init fields:
        m_cost = 2;             // cost 2 coins
        setMovementCount(1);    // moves every 1 block
        setDamage(0);           // they're friendly people
    }

    @Override
    public void move(int x, int y){
        m_label.relocate(x, y);
    }
}
