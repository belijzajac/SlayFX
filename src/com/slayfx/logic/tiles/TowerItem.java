package com.slayfx.logic.tiles;

public class TowerItem extends BuyableItem {

    public static int m_cost = 15; // cost 15 coins
    public static int m_count = 0;

    public TowerItem(Point coords, String id, String owner){
        super(owner, BuyableItem.tower_img, coords);
        setID(id);

        // Init fields:
        setMovementCount(0); // towers cant move
        setDamage(0);        // towers do no damage
    }

    @Override
    public boolean move(Hex hex){
        return false; // you can't move this thing
    }

    @Override
    public void decreaseCount(){
        if(m_count > 0)
            m_count--;
    }
}
