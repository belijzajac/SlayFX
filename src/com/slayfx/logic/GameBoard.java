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
        hexMap = new ArrayList<Hex>();
        this.m_width = width;
        this.m_height = height;

        newGame();
    }

    @Override
    public void newGame(){
        rhombusMap();
    }

    private void rhombusMap(){
        // to shift rows in order to form rhombus (offset)
        int row_diff = 0;

        // Creates a map shaped like rhombus
        for(int col = 0; col<m_width / 50; col++){
            for(int row = 0; row<m_height / 50; row++){
                hexMap.add(new Hex(HexColor.GREEN, 36 * col + row_diff, 31 * row));
                row_diff += 18;

            }
            row_diff = 0;
        }
    }

    public ArrayList<Hex> getHexMap(){ return hexMap; }
}
