package com.slayfx.logic.tiles;

public class SoldierItem extends BuyableItem {

    public static int m_cost = 6; // cost 6 coins
    public static int m_count = 0;

    public SoldierItem(Point coords, String id, String owner){
        super(owner, BuyableItem.soldier_img, coords);
        setID(id);

        // Init fields:
        setMovementCount(3);    // moves every 3 blocks
        setDamage(2);           // can kill peasants
    }

    @Override
    public boolean move(Hex hex){
        // Peasants can occupy empty hex tiles or kill peasants
        if(hex.getState().equals(HexState.EMPTY) || hex.getState().equals(HexState.SOLDIER_1)){
            this.setCoords(new Point( hex.getCoords().getX() - 13, hex.getCoords().getY() - 13 ));  // move this item to hex
            this.setID(hex.getID());
            m_label.relocate( getCoords().getX(), getCoords().getY() );
            return true;
        }
        return false;
    }

    @Override
    public void decreaseCount(){
        if(m_count > 0)
            m_count--;
    }
}
