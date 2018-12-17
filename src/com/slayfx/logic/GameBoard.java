package com.slayfx.logic;

import com.slayfx.logic.map.MapReader;
import com.slayfx.logic.player.AI;
import com.slayfx.logic.player.Player;
import com.slayfx.logic.tiles.*;
import com.slayfx.gui.mapChooseController;

import java.util.*;

// A singleton class
public class GameBoard implements Board {

    private static final GameBoard instance = new GameBoard();

    private ArrayList<Hex> hexMap;
    private Map<String, BuyableItem> drawnGameObjects;
    private ArrayList<Player> m_players; // holds players' data
    private HashSet<String> m_activePlayers; // to keep track of active players
    private int m_currectPlaerNum; // index of the current player
    public static int m_turnCount = 0;
    private boolean isOkToContinueGame;

    // To avoid client initialization
    private GameBoard(){
        hexMap = new ArrayList<Hex>();
        drawnGameObjects  = new HashMap<String, BuyableItem>();
        m_players = new ArrayList<Player>();
        m_activePlayers = new HashSet<String>();

        m_currectPlaerNum = 0;
        isOkToContinueGame = true;

        newGame();
    }

    public Map<String, BuyableItem> getDrawnGameObjects(){ return drawnGameObjects; }

    public static GameBoard getInstance(){
        return instance;
    }

    @Override
    public void newGame(){
        loadMap();                                          // Loads the map
        createPlayers( mapChooseController.getPlayers() ); 	// Initializes the players
        randomlySpawnPlayers();                             // randomly spawn players
    }

    private void loadMap(){
        String mapLocation = "../res/maps/" + mapChooseController.getChosenMap();
        hexMap = MapReader.read(mapLocation);
    }

    private HexColor getRandomColor(){
        return HexColor.values()[new Random().nextInt(HexColor.values().length - 1)]; // random color; omitting HexColor.EMPTY
    }

    private boolean hasAnoteherPlayerHaveThisColor(HexColor color){
        return m_players.stream().anyMatch(player -> player.getColor().equals(color));
    }

    private HexColor getTotallyRandomColor(int initializedPlayersSoFar) {
        HexColor color = getRandomColor();

        if(initializedPlayersSoFar == 0)
            return color;

        // Loop as long as we find unique color
        // NOTE: only works with up to 5 players (we have 5 different colors)
        while(hasAnoteherPlayerHaveThisColor(color))
            color = getRandomColor();

        return color;
    }

    private void createPlayers(int count){
        for(int player_count = 0; player_count < count; player_count++){
            if(mapChooseController.includeAIPlayers() && player_count >= 1)
                m_players.add(new AI("AI_" + String.valueOf(player_count), getTotallyRandomColor(player_count)));
            else
                m_players.add(new Player("player_" + String.valueOf(player_count), getTotallyRandomColor(player_count)));

            // add created players to the players list:
            m_activePlayers.add( m_players.get(player_count).getName() );
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

            // assign editedHex properties to the homeHex (needed for the AI)
            player.setHomeHex(editedHex);
        }
    }

    public ArrayList<Hex> getHexMap(){ return hexMap; }

    public int getCurrPlayerIndex(){
        if(m_currectPlaerNum >= m_players.size())
            changeCurrPlayerIndex(0);

        return m_currectPlaerNum; }

    public void changeCurrPlayerIndex(int index){
        if (index >= m_players.size())
            index = 0;
        m_currectPlaerNum = index;

        // Check whether such player hasn't been removed from active players list
        if (m_players.get(m_currectPlaerNum).isDead() /*&& !m_activePlayers.contains(m_players.get(m_currectPlaerNum).getName())*/) {
            changeCurrPlayerIndex(index + 1);
        }
    }

    public ArrayList<Player> getPlayersList(){ return m_players; }

    public boolean isOkToContinueGame() {
        return isOkToContinueGame;
    }

    public HashSet<String> getActivePlayers(){
        return m_activePlayers;
    }

    //  Diagonal hex based on defaultHex is formed like the following:
    //           (x-18; y-31)     (x+18; y-31)
    //   (x-36; y)          (x; y)          (x+36; y)
    //           (x-18; y+31)     (x+18; y+31)
    public boolean canMoveDiagonally(Hex hexFrom, Hex hexTo){
        // 1) top-left hex
        if(checkIDsOfHexs(hexFrom, hexTo, -18.0, -31.0))
            return true;

            // 2 top-right hex
        else if(checkIDsOfHexs(hexFrom, hexTo, 18.0, -31.0))
            return true;

            // 3) left hex
        else if(checkIDsOfHexs(hexFrom, hexTo, -36.0, 0.0))
            return true;

            // 4) right hex
        else if(checkIDsOfHexs(hexFrom, hexTo, 36.0, 0.0))
            return true;

            // 5) bottom-left hex
        else if(checkIDsOfHexs(hexFrom, hexTo, -18.0, 31.0))
            return true;

            // 6) bottom-right hex
        else return checkIDsOfHexs(hexFrom, hexTo, 18.0, 31.0);
    }

    private Hex getNeigborHex(Hex startingHex, double x_, double y_){
        return getHexWithTheseCoordinates(startingHex.getCoords().getX() + x_, startingHex.getCoords().getY() + y_);
    }

    // Return a List of hex of all possible reachable neighbor hex tiles
    public ArrayList<Hex> getNeighbors(Hex startingHex){
        ArrayList<Hex> neighbors = new ArrayList<>();

        // 1)
        Hex neighbor_1 = getNeigborHex(startingHex, -18.0, -31.0);
        if(neighbor_1 != null)
            neighbors.add(neighbor_1);

        // 2)
        Hex neighbor_2 = getNeigborHex(startingHex, 18.0, -31.0);
        if(neighbor_2 != null)
            neighbors.add(neighbor_2);

        // 3)
        Hex neighbor_3 = getNeigborHex(startingHex, -36.0, 0.0);
        if(neighbor_3 != null)
            neighbors.add(neighbor_3);

        // 4)
        Hex neighbor_4 = getNeigborHex(startingHex, 36.0, 0.0);
        if(neighbor_4 != null)
            neighbors.add(neighbor_4);

        // 5)
        Hex neighbor_5 = getNeigborHex(startingHex, -18.0, 31.0);
        if(neighbor_5 != null)
            neighbors.add(neighbor_5);

        // 6)
        Hex neighbor_6 = getNeigborHex(startingHex, 18.0, 31.0);
        if(neighbor_6 != null)
            neighbors.add(neighbor_6);

        return neighbors;
    }

    public boolean checkIDsOfHexs(Hex hexFrom, Hex hexTo, double x, double y){
        String hexID = Double.toString( hexFrom.getCoords().getX() + x) + '_' + Double.toString( hexFrom.getCoords().getY() + y);
        return (hexTo.getID().equals(hexID));
    }

    // Increase game difficulty by increasing prices
    public void increaseDifficulty(){
        if(TowerItem.m_count % 5 == 0) // every 5*tower count increase its cost for all players
            TowerItem.m_cost++;

        if(PeasantItem.m_count % 4 == 0)
            PeasantItem.m_cost++;

        if(SoldierItem.m_count % 3 == 0)
            SoldierItem.m_cost++;

        if(WarriorItem.m_count % 2 == 0)
            WarriorItem.m_cost += 5;    //  +5 to cost cuz its powerful unit
    }

    // Finds and returns a hex with the provided coordinates
    public Hex getHexWithTheseCoordinates(double _x, double _y){
        ArrayList<Hex> hexMap = this.getHexMap();
        for(Hex hex : hexMap){
            if(hex.getCoords().getX() == _x && hex.getCoords().getY() == _y)
                return hex;
        }
        return null;
    }

    // Check if such neighbor exists and if it has the same color as present player
    public boolean checkNeighborAndColor(double _x, double _y){
        Hex neighbor = getHexWithTheseCoordinates(_x, _y);

        // if distance between neighbor hex and current hex is equal to 1 hex unit,
        // then it's a viable move
        return neighbor != null && neighbor.getColor().equals( getCurrentPlayerObj().getColor() );
    }

    // You've got currHex
    // We'll check if whether a single one of its neighbors has the same color
    public boolean doDiagonalNeighborsHaveTheSameColor(Hex currHex){
        // Neighbor 1:
        if(checkNeighborAndColor(currHex.getCoords().getX() - 18, currHex.getCoords().getY() - 31))
            return true;
        // Neighbor 2:
        if(checkNeighborAndColor(currHex.getCoords().getX() + 18, currHex.getCoords().getY() - 31))
            return true;
        // Neighbor 3:
        if(checkNeighborAndColor(currHex.getCoords().getX() - 36, currHex.getCoords().getY()))
            return true;
        // Neighbor 4:
        if(checkNeighborAndColor(currHex.getCoords().getX() + 36, currHex.getCoords().getY()))
            return true;
        // Neighbor 5:
        if(checkNeighborAndColor(currHex.getCoords().getX() - 18, currHex.getCoords().getY() + 31))
            return true;
        // Neighbor 6:
        return checkNeighborAndColor(currHex.getCoords().getX() + 18, currHex.getCoords().getY() + 31);
    }

    public Player getCurrentPlayerObj(){ return this.getPlayersList().get( this.getCurrPlayerIndex() ); }

    // Finds Hex tile with associated string key
    public Hex findHex(String key){
        ArrayList<Hex> hexMap = this.getHexMap();
        for(Hex m_Hex : hexMap){
            if(m_Hex.getID().equals(key))
                return m_Hex;
        }
        return null;
    }
}
