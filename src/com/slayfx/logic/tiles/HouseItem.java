package com.slayfx.logic.tiles;

public class HouseItem extends BuyableItem {

    public static int m_cost = 0; // houses are free

    public HouseItem(Point coords, String id, String owner){
        super(owner, BuyableItem.house_img, coords);
        setID(id);

        // Init fields:
        setMovementCount(0); // houses cant move
        setDamage(0);        // houses do no damage
    }

    @Override
    public boolean move(Hex hex){
       return false; // you can't move this thing
    }
}
