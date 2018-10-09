package com.slayfx.logic.tiles;

public class HouseItem extends BuyableItem {
    public HouseItem(Point coords, String id, String owner){
        super(owner, BuyableItem.house_img, coords);
        setID(id);

        // Init fields:
        m_cost = 0;          // houses are free
        setMovementCount(0); // houses cant move
        setDamage(0);        // houses do no damage
    }

    @Override
    public void move(int x, int y){
       ; // you can't move this thing
    }
}
