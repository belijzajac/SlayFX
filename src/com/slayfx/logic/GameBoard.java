package com.slayfx.logic;

import com.slayfx.logic.player.Player;
import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;
import com.slayfx.logic.tiles.HexState;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard implements Board {
    private final int m_width;
    private final int m_height;
    private ArrayList<Hex> hexMap;
    private ArrayList<Player> m_players; // holds players' data

    // Constructor
    public GameBoard(int width, int height){
        hexMap = new ArrayList<Hex>();
        m_players = new ArrayList<Player>();

        this.m_width = width;
        this.m_height = height;

        newGame();
    }

    @Override
    public void newGame(){
        rhombusMap();           // Creates the map
        createPlayers(2); 	// Initializes the players
        randomlySpawnPlayers(); // randomly spawn players
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

    private HexColor getRandomColor(){
        return HexColor.values()[new Random().nextInt(HexColor.values().length)]; // random color
    }

    private boolean hasAnoteherPlayerHaveThisColor(int initializedPlayersSoFar, HexColor color){
        for(int pl_id = 0; pl_id<initializedPlayersSoFar; pl_id++){ // iterate though initialized players
            if(m_players.get(pl_id).getColor().equals(color)){      // the color appears to be used by another player
                return true;
            }
        }
        return false;
    }

    private HexColor getTotallyRandomColor(int initializedPlayersSoFar) {
        HexColor color = getRandomColor();

        if(initializedPlayersSoFar == 0)
            return color;

        // Loop as long as we find unique color
        // TODO: only works with up to 5 players (we have 5 different colors)
        while(hasAnoteherPlayerHaveThisColor(initializedPlayersSoFar, color))
            color = getRandomColor();

        return color;
    }

    private void createPlayers(int count){
        for(int player_count = 0; player_count < count; player_count++){
            m_players.add(new Player("player_" + String.valueOf(player_count), getTotallyRandomColor(player_count)));
        }
    }

    private void randomlySpawnPlayers(){
        int hexCount = hexMap.size();
        Random rand_num = new Random();

        for(Player player : m_players){ // iterate through players
            Hex editedHex = hexMap.get( rand_num.nextInt(hexCount) ); // get the instance of a random hex
            editedHex.changeOwner(player.getName());                  // this hex is owned by player
            editedHex.changeColor(player.getColor());                 // so this its color
            editedHex.changeState(HexState.HOUSE);                    // this is the home hex for the player
        }
    }

    public ArrayList<Hex> getHexMap(){ return hexMap; }
}
