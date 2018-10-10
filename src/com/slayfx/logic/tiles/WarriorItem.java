package com.slayfx.logic.tiles;

public class WarriorItem extends BuyableItem {

    public static int m_cost = 18; // cost 18 coins

    public WarriorItem(Point coords, String id, String owner){
        super(owner, BuyableItem.warrior_img, coords);
        setID(id);

        // Init fields:
        setMovementCount(5);    // moves every 5 blocks
        setDamage(10);          // can kill peasants and soldiers, destroys towers
    }

    @Override
    public boolean move(Hex hex){
        // Warriors can occupy empty hex tiles, or kill peasants, or kill soldiers, or kill towers
        if(hex.getState().equals(HexState.EMPTY) || hex.getState().equals(HexState.SOLDIER_1)
                || hex.getState().equals(HexState.SOLDIER_2) || hex.getState().equals(HexState.TOWER) ){
            m_label.relocate(hex.getCoords().getX() - 13, hex.getCoords().getY() - 13); // move this item to hex
            return true;
        }
        return false;
    }
}
