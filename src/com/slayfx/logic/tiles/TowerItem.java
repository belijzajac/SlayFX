package com.slayfx.logic.tiles;

public class TowerItem extends BuyableItem {
    public TowerItem(Point coords, String id, String owner){
        super(owner, BuyableItem.tower_img, coords);
        setID(id);

        // Init fields:
        m_cost = 15;         // cost 15 coins
        setMovementCount(0); // towers cant move
        setDamage(0);        // towers do no damage
    }

    @Override
    public void move(int x, int y){
        ; // you can't move this thing
    }
}
