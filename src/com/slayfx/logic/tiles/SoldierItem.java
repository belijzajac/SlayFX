package com.slayfx.logic.tiles;

public class SoldierItem extends BuyableItem {

    public static int m_cost = 6; // cost 6 coins

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
            m_label.relocate(hex.getCoords().getX() - 13, hex.getCoords().getY() - 13); // move this item to hex
            return true;
        }
        return false;
    }
}
