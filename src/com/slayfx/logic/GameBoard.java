package com.slayfx.logic;

import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;
import java.util.ArrayList;

public class GameBoard implements Board {

    private final int m_width;
    private final int m_height;
    private ArrayList<Hex> hexMap;

    // Constructor
    public GameBoard(int width, int height){
        this.m_width = width;
        this.m_height = height;

        newGame();
    }

    @Override
    public void newGame(){
        // Testing:
        hexMap.add(new Hex(HexColor.GREY, 0, 0));
    }
}
