package com.slayfx.logic.tiles;

public class PeasantItem extends BuyableItem {

    public static int m_cost = 2; // cost 2 coins
    public static int m_count = 0;

    public PeasantItem(Point coords, String id, String owner){
        super(owner, BuyableItem.peasant_img, coords);
        setID(id);

        // Init fields:
        setMovementCount(1);    // moves every 1 block
        setDamage(0);           // they're friendly people
    }

    @Override
    public boolean move(Hex hex){
        // Peasants can only occupy empty hex tiles
        if(hex.getState().equals(HexState.EMPTY)){
            this.setCoords(new Point( hex.getCoords().getX() - 13, hex.getCoords().getY() - 13 ));
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
