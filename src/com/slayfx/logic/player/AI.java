package com.slayfx.logic.player;

import com.slayfx.logic.GameBoard;
import com.slayfx.logic.tiles.*;

import java.util.ArrayList;
import java.util.Random;

public class AI extends Player {
    // Constructor
    public AI(String id, HexColor color){
        super(id, color);
    }

    private int moveableUnitsCount = 0;

    @Override
    public void moveUnits(Hex hex){ // move --> conquer
        GameBoard board = GameBoard.getInstance();
        BuyableItem item = board.getDrawnGameObjects().get(hex.getID());
        Hex tempHex = hex;

        int failCount2 = 0;
        while(item != null && item.getCurrMovementCount() > 0){
            ArrayList<Hex> neighbors = board.getNeighbors(tempHex);            // get list of neighbors

            // step only on empty hex tiles
            int failCount = 0;
            tempHex = neighbors.get(new Random().nextInt(neighbors.size()));   // pick random neighbor
            while(!tempHex.getState().equals(HexState.EMPTY)){
                tempHex = neighbors.get(new Random().nextInt(neighbors.size()));
                failCount++;

                if(failCount == 5)
                    break;
            }

            if(item.move(tempHex)){ // move to tempHex
                // reassign attributes to the new hex:
                tempHex.changeState(HexState.SOLDIER_2);
                tempHex.changeOwner(hex.getOwner());
                tempHex.changeColor(hex.getColor());
                hex.changeState(HexState.EMPTY);

                item.decreaseCurrMovementCount();
            }else{
                tempHex = hex; // you didn't move anywhere
            }

            failCount2++;
            if(failCount2 == 5)
                break;
        }
    }

    @Override
    public void buyItems(){ // purchase items on the gameBoard
        // GameBoard's instance
        GameBoard board = GameBoard.getInstance();

        // buy a unit in a random hex around the homeHex tile
        Hex currHex = this.getHomeHex();
        ArrayList<Hex> neighbors = board.getNeighbors(currHex);

        if(moveableUnitsCount <= 4){
            // TODO: buy random item
            if(neighbors.size() > 0){
                // Peasant item
                if((this.getMoney() - SoldierItem.m_cost) > 0){ // <--- put the body into a function or sth
                    // buy a peasant item on a random neighbor hex tile
                    this.setMoney( this.getMoney() - SoldierItem.m_cost );
                    Hex randomNeighborHex = neighbors.get(new Random().nextInt(neighbors.size()));

                    // reassign attributes to the new hex:
                    randomNeighborHex.changeState(HexState.SOLDIER_2);
                    randomNeighborHex.changeOwner(currHex.getOwner());
                    randomNeighborHex.changeColor(currHex.getColor());
                    moveableUnitsCount++;

                    moveUnits(randomNeighborHex);
                }
            }
        }/*(else{
            // only move!!!!
            for(Hex neighbor : neighbors){
                if(neighbor.getOwner().equals(this.getName()) && !neighbor.getState().equals(HexState.EMPTY)){
                    moveUnits(neighbor);
                    break;
                }
            }
        }*/
    }
}
