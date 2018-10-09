package com.slayfx.logic.tiles;

public class WarriorItem extends BuyableItem {
    public WarriorItem(Point coords, String id, String owner){
        super(owner, BuyableItem.warrior_img, coords);
        setID(id);

        // Init fields:
        m_cost = 18;            // cost 18 coins
        setMovementCount(5);    // moves every 5 blocks
        setDamage(10);          // can kill peasants and soldiers, destroys towers
    }

    @Override
    public void move(int x, int y){
        m_label.relocate(x, y);
    }
}
