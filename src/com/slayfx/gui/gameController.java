package com.slayfx.gui;

import com.slayfx.libs.DateAndTime.*;
import com.slayfx.logic.GameBoard;
import com.slayfx.logic.player.Player;
import com.slayfx.logic.tiles.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Button;
import java.util.*;
import java.util.stream.Collectors;

public class gameController {
    private GameBoard gameBoard;                          // Game board
    private Map<String, Polygon> polygons;                // Holds drawn polygons
    private Map<String, BuyableItem> drawnGameObjects;
    private Polygon activePolygon;                        // Polygon that user has clicked with the mouse
    private Polygon oldactivePolygon;
    private boolean hadUserClickedOnHex = false;

    // Timer:
    private static Timer timer;
    private ControlDateAndTime timeElapsed;
    @FXML private Label timeLabel;

    // Variables that refer to GUI elements
    @FXML private Pane drawingArea;
    @FXML private Button btnNextTurn;
    @FXML private Label playerLabel;
    @FXML private Label moneyLabel;
    @FXML private Button towerBtn;
    @FXML private Button soldier1Btn;
    @FXML private Button soldier2Btn;
    @FXML private Button soldier3Btn;

    @FXML
    public void initialize(){
        // Build time elapsed
        DateAndTimeBuilder builder = new BuildDataAndTime();
        timeElapsed = builder.buildMinutes().buildSeconds().buildDate();

        // Initialize game board and map
        gameBoard = new GameBoard(500, 500);
        //if(!gameBoard.isOkToContinueGame()){
            //System.out.println("Quitting the game...");
          //  Main.getInstance().stop();
        //}

        polygons = new HashMap<String, Polygon>();
        drawnGameObjects  = new HashMap<String, BuyableItem>();
        drawHexMap();   // draws map
        setUpTimer();

        // By default make activePolygon point to the first polygon object
        Map.Entry<String, Polygon> entry = polygons.entrySet().iterator().next();  // iterator
        activePolygon = oldactivePolygon = entry.getValue();                       // get first value

        updateLabels(); // updates labels

        // Initialize mouse click event for each polygon (Hexagon)
        for(final Map.Entry<String, Polygon> hex : polygons.entrySet()) {
            hex.getValue().setOnMouseClicked(event -> {      // Run once a player clicks on any hex tile
                // Find corresponding Hex tile and change its color
                Hex hexTile = findHex(hex.getKey());         // Get Hex object
                Polygon polygon = hex.getValue();            // Get Polygon from hex map

                activePolygon.setStroke(Color.BLACK);      // reset stroke of the previous polygon
                activePolygon = polygon;                   // we get a new active corresponding polygon

                highlighActiveHex();                       // highlight it

                if (hadUserClickedOnHex) {
                    oldactivePolygon.setStroke(Color.GREENYELLOW);

                    // Get that previous hex:
                    Hex oldHex = findHex(getKeyFromPolygonsMap(oldactivePolygon));

                    if (oldHex == null) {
                        System.out.println("oldHex: null");
                    } else {
                        System.out.println("oldHex: " + oldHex.getOwner() + " " + oldHex.getState() + " key=" + oldHex.getID());
                    }

                    // Get that previous item:
                    BuyableItem oldItem = drawnGameObjects.get(oldHex.getID());
                    if (oldItem == null) {
                        System.out.println("oldItem: null");                            // TODO: you get NULL !!!
                    } else {
                        System.out.println("oldItem: " + oldItem.getOwner());
                    }

                    // Get item which we want to conquer
                    BuyableItem itemToBeConquered = drawnGameObjects.get(hexTile.getID());
                    if (itemToBeConquered == null) {
                        System.out.println("itemToBeConquered: null");
                    } else {
                        System.out.println("itemToBeConquered: " + itemToBeConquered.getOwner());
                    }

                    System.out.println("\n");


                    /* NEW CODE */
                    /*if (oldHex != null && oldItem != null && oldHex.getOwner().equals(getCurrentPlayerObj().getName())
                            && oldItem.getOwner().equals(getCurrentPlayerObj().getName())) {
                        // Checks if it's a valid move
                        if (oldItem.getCurrMovementCount() > 0 && canMoveDiagonally(oldHex, hexTile) && oldItem.move(hexTile)) { // <-- move it where activePolygon points to
                            oldItem.decreaseCurrMovementCount();

                            // Remove that item from the game board
                            if (itemToBeConquered != null) {
                                if (itemToBeConquered instanceof HouseItem) {                              // we just conquered player's home tile
                                    if (gameBoard.getActivePlayers().remove(itemToBeConquered.getOwner())) { // eliminate that player

                                        for (int i = 0; i < gameBoard.getPlayersList().size(); i++)
                                            if (gameBoard.getPlayersList().get(i).getName().equals(itemToBeConquered.getOwner())) {
                                                gameBoard.getPlayersList().get(i).endLife();
                                                gameBoard.getPlayersList().remove(i);
                                                break;
                                            }

                                        System.out.println("Player " + itemToBeConquered.getOwner() + " just got eliminated!");
                                    }
                                }
                                popFromDrawnGameObjects(itemToBeConquered); // pop it from of drawnGameObjects map
                            }

                            oldItem.getLabel().toFront();

                            // reassign attributes to the new hex:
                            hexTile.changeState(oldHex.getState());                                           // change state of a hex
                            oldHex.changeState(HexState.EMPTY);
                            hexTile.changeOwner(getCurrentPlayerObj().getName());
                            hexTile.changeColor(getCurrentPlayerObj().getColor());              // change color
                            paintPolygon(activePolygon, getCurrentPlayerObj().getColor());    // apply new color

                            oldactivePolygon = activePolygon;
                        }
                    }*/

                    if (oldHex != null && oldItem != null && oldHex.getOwner().equals(getCurrentPlayerObj().getName()) && oldItem.getOwner().equals(getCurrentPlayerObj().getName())) {
                        // Checks if it's a valid move
                        if (oldItem.getCurrMovementCount() > 0 && canMoveDiagonally(oldHex, hexTile) && oldItem.move(hexTile)) { // <-- move it where activePolygon points to
                            oldItem.decreaseCurrMovementCount();

                            // Remove that item from the game board
                            if (itemToBeConquered != null) {
                                popFromDrawnGameObjects(itemToBeConquered); // pop it from of drawnGameObjects map
                                // TODO: you cant conquire your own items
                            }

                            oldItem.getLabel().toFront();

                            // reassign attributes to the new hex:
                            hexTile.changeState(oldHex.getState());                                           // change state of a hex
                            oldHex.changeState(HexState.EMPTY);
                            hexTile.changeOwner(getCurrentPlayerObj().getName());
                            hexTile.changeColor(getCurrentPlayerObj().getColor());              // change color
                            paintPolygon(activePolygon, getCurrentPlayerObj().getColor());    // apply new color

                            oldactivePolygon = activePolygon;
                        }
                    }


                    hadUserClickedOnHex = false;
                }

                // If this isn't an empty hex, then you can move its content
                if (!hexTile.getState().equals(HexState.EMPTY)) {
                    oldactivePolygon = activePolygon;
                    oldactivePolygon.setStroke(Color.GREENYELLOW);
                    hadUserClickedOnHex = true;
                }
            });
        }
    }

    // Gets key from Polygon's map by providing a value _value
    private String getKeyFromPolygonsMap(Polygon _value){
        Set<Map.Entry<String, Polygon>> mapSet = polygons.entrySet();
        for(Map.Entry<String, Polygon> entry : mapSet){
            if(entry.getValue().equals(_value)){
                System.out.println("Found key : " + entry.getKey());
                return entry.getKey();
            }
        }
        System.out.println("Key not found");
        return null;
    }

    private void setUpTimer(){
        // Create a timer and schedule it to run every 1 second
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if(timeElapsed.getMinutes().equals("00") && timeElapsed.getSeconds().equals("00"))
                                    onNextTurnBtnClicked();
                                else{
                                    timeElapsed.subSeconds(1);
                                    timeLabel.setText(timeElapsed.getMinutes() + ":" + timeElapsed.getSeconds());
                                }
                            }
                        });
                    }
                },
                100,      // run first occurrence after 100 ms
                1000);  // run every seconds
    }

    static void cancelTimer(){
        if(timer != null)
            timer.cancel();
    }

    //  Diagonal hex based on defaultHex is formed like the following:
    //           (x-18; y-31)     (x+18; y-31)
    //   (x-36; y)          (x; y)          (x+36; y)
    //           (x-18; y+31)     (x+18; y+31)
    private boolean canMoveDiagonally(Hex hexFrom, Hex hexTo){
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

    private boolean checkIDsOfHexs(Hex hexFrom, Hex hexTo, double x, double y){
        String hexID = Double.toString( hexFrom.getCoords().getX() + x) + '_' + Double.toString( hexFrom.getCoords().getY() + y);
        return (hexTo.getID().equals(hexID));
    }

    // Basically removes this object from the game
    private void popFromDrawnGameObjects(BuyableItem itemToPop){
        // Decrease its total quantity in the game
        itemToPop.decreaseCount();

        // Remove item from the game:
        itemToPop.getLabel().toBack();                          // just in case if something goes horribly wrong
        drawingArea.getChildren().remove(itemToPop.getLabel()); // remove item from drawingArea
        drawnGameObjects.remove(itemToPop.getID());                     // remove item from drawnGameObjects map
    }

    private void highlighActiveHex(){
        activePolygon.setStroke(Color.FLORALWHITE);
    }

    private void updateLabels(){
        playerLabel.setText("Turn: " + getCurrentPlayerObj().getName());
        moneyLabel.setText("Money: " + Integer.toString( getCurrentPlayerObj().getMoney() ));

        // Color the player label to help distinguish which player's turn this is
        switch (getCurrentPlayerObj().getColor()){
            case GREEN: playerLabel.setStyle("-fx-background-color: green;");
                break;
            case RED: playerLabel.setStyle("-fx-background-color: red;");
                break;
            case BLUE: playerLabel.setStyle("-fx-background-color: blue;");
                break;
            case PINK: playerLabel.setStyle("-fx-background-color: pink;");
                break;
            case YELLOW: playerLabel.setStyle("-fx-background-color: yellow;");
                break;
        }

        // Update items prices:
        towerBtn.setText("= " + Integer.toString(TowerItem.m_cost));
        soldier1Btn.setText("= " + Integer.toString(PeasantItem.m_cost));
        soldier2Btn.setText("= " + Integer.toString(SoldierItem.m_cost));
        soldier3Btn.setText("= " + Integer.toString(WarriorItem.m_cost));
    }

    @FXML
    private void onNextTurnBtnClicked(){
        System.out.println("Next turn!");

        onNextTurnKillUnits();
        onNextTurnWarriorsLocation();
        onNextTurnMoneyInCirculation();

        getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() + 10 );
        gameBoard.changeCurrPlayerIndex(gameBoard.getCurrPlayerIndex() + 1);

        GameBoard.m_turnCount++;
        updateLabels();

        // Increase game difficulty every 3 turns if certain conditions are met
        if(GameBoard.m_turnCount % 3 == 0)
            increaseDifficulty();

        // Reset active polygon selection
        polygons.values().parallelStream().forEach(e -> e.setStroke(Color.BLACK));

        // Reset movement count of all game items
        drawnGameObjects.values().parallelStream().forEach(e -> e.resetMovementCountToDefault());

        // Reset timer:
        timeElapsed.setMinutes(1);
        timeElapsed.setSeconds(0);
    }

    // Kill all player's units if they cant be fed anymore
    private void onNextTurnKillUnits(){
        List<Player> playersStream = gameBoard.getPlayersList().stream()     // convert list to stream
                .filter(player -> player.getMoney() <= 0)                    // a player has got no money
                .collect(Collectors.toList());                               // collect the output and convert streams to a List

        playersStream.forEach( this::killAllPlayersUnits );
    }

    private void onNextTurnWarriorsLocation(){
        // Using parallel stream to find location of warriors (the most powerful unit)
        // if the game board consists of 1000 hex tiles, it's a fast approach to process such data
        gameBoard.getHexMap().parallelStream()
                .filter(e -> e.getState() == HexState.SOLDIER_3)
                .forEach(e -> System.out.println("warrior found at : " + e.getID()));
    }

    private void onNextTurnMoneyInCirculation(){
        // stream().map() lets you convert an object to something else
        // stream().reduce() takes a sequence of input elements and combines them into a single result
        // Money of all players combined:
        Integer moneyOfAllPLayers = gameBoard.getPlayersList().stream()
                .map(e -> e.getMoney())
                .reduce(0, Integer::sum); // calculate money from 0
        System.out.println("Money circulation in the game: " + moneyOfAllPLayers);
    }

    private void killAllPlayersUnits(Player player){
        // Kill all the units if they were bought under same player name
        // drawnGameObjects.entrySet().removeIf(e -> e.getValue().getOwner().equals(player.getName()));
        for(Iterator<BuyableItem> it = drawnGameObjects.values().iterator(); it.hasNext();){
            BuyableItem itemToPop = it.next();

            if(itemToPop.getOwner().equals(player.getName()) && itemToPop.getMaxMovementCount() > 0) {
                itemToPop.decreaseCount();
                itemToPop.getLabel().toBack();
                drawingArea.getChildren().remove(itemToPop.getLabel());

                Hex hex = findHex( itemToPop.getID() );
                if(hex != null)
                    hex.changeState(HexState.EMPTY);

                it.remove();
            }
        }
    }

    // Increase game difficulty by increasing prices
    private void increaseDifficulty(){
        if(TowerItem.m_count % 5 == 0) // every 5*tower count increase its cost for all players
            TowerItem.m_cost++;

        if(PeasantItem.m_count % 4 == 0)
            PeasantItem.m_cost++;

        if(SoldierItem.m_count % 3 == 0)
            SoldierItem.m_cost++;

        if(WarriorItem.m_count % 2 == 0)
            WarriorItem.m_cost += 5;    //  +5 to cost cuz its powerful unit
    }

    private void addDrawnObjectToMap(HexState whatItem){
        HexState state = HexState.EMPTY;

        // Determine what state is that
        switch (whatItem){
            case HOUSE: state = HexState.HOUSE;
                break;
            case TOWER: state = HexState.TOWER;
                break;
            case SOLDIER_1: state = HexState.SOLDIER_1;
                break;
            case SOLDIER_2: state = HexState.SOLDIER_2;
                break;
            case SOLDIER_3: state = HexState.SOLDIER_3;
                break;
        }

        // Find hex that activePolygon points to
        Hex hex = findHex(getKeyFromPolygonsMap(activePolygon));

        if(hex != null && !state.equals(HexState.EMPTY)){
            // Change hex tile attributes to new ones:
            hex.changeState(state);                                           // change state of a hex
            hex.changeOwner(getCurrentPlayerObj().getName());                 // change owner
            hex.changeColor( getCurrentPlayerObj().getColor() );              // change color
            paintPolygon(activePolygon, getCurrentPlayerObj().getColor());    // apply new color

            BuyableItem item = null;
            if(state.equals(HexState.HOUSE))
                item = new HouseItem(hex.getCoords(), hex.getID(), getCurrentPlayerObj().getName());
            else if(state.equals(HexState.TOWER))
                item = new TowerItem(hex.getCoords(), hex.getID(), getCurrentPlayerObj().getName());
            else if(state.equals(HexState.SOLDIER_1))
                item = new PeasantItem(hex.getCoords(), hex.getID(), getCurrentPlayerObj().getName());
            else if(state.equals(HexState.SOLDIER_2))
                item = new SoldierItem(hex.getCoords(), hex.getID(), getCurrentPlayerObj().getName());
            else if(state.equals(HexState.SOLDIER_3))
                item = new WarriorItem(hex.getCoords(), hex.getID(), getCurrentPlayerObj().getName());

            // Add that object to the drawnObjects map:
            drawnGameObjects.put(hex.getID(), item);            // add game object to map
            drawingArea.getChildren().addAll(item.getLabel());  // add it to pane
        }
    }

    // Finds and returns a hex with the provided coordinates
    private Hex getHexWithTheseCoordinates(double _x, double _y){
        ArrayList<Hex> hexMap = gameBoard.getHexMap();
        for(Hex hex : hexMap){
            if(hex.getCoords().getX() == _x && hex.getCoords().getY() == _y)
                return hex;
        }
        return null;
    }

    // Check if such neighbor exists and if it has the same color as present player
    private boolean checkNeighborAndColor(double _x, double _y){
        Hex neighbor = getHexWithTheseCoordinates(_x, _y);

        // if distance between neighbor hex and current hex is equal to 1 hex unit,
        // then it's a viable move
        return neighbor != null && neighbor.getColor().equals( getCurrentPlayerObj().getColor() );
    }

    // You've got currHex
    // We'll check if whether a single one of its neighbors has the same color
    private boolean doDiagonalNeighborsHaveTheSameColor(Hex currHex){
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

    @FXML
    private void onBuyTowerBtnClicked(ActionEvent event){
        Hex currHex = findHex(getKeyFromPolygonsMap(activePolygon));

        if(getCurrentPlayerObj().getMoney() >= TowerItem.m_cost && currHex.getState().equals(HexState.EMPTY) && doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.TOWER);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - TowerItem.m_cost );
            updateLabels();
            TowerItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(TowerItem.m_cost));

            // Store history of bought items:
            getCurrentPlayerObj().getItemsHistory().add("Tower"); // the user has just bought a tower
        }
    }

    @FXML
    private void onBuyPeasantBtnClicked(ActionEvent event){
        Hex currHex = findHex(getKeyFromPolygonsMap(activePolygon));

        if(getCurrentPlayerObj().getMoney() >= PeasantItem.m_cost && currHex.getState().equals(HexState.EMPTY) && doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.SOLDIER_1);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - PeasantItem.m_cost );
            updateLabels();
            PeasantItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(PeasantItem.m_cost));

            // Store history of bought items:
            getCurrentPlayerObj().getItemsHistory().add("Peasant"); // the user has just bought a peasant unit
        }
    }

    @FXML
    private void onBuySlodierBtnClicked(ActionEvent event){
        Hex currHex = findHex(getKeyFromPolygonsMap(activePolygon));

        if(getCurrentPlayerObj().getMoney() >= SoldierItem.m_cost && currHex.getState().equals(HexState.EMPTY) && doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.SOLDIER_2);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - SoldierItem.m_cost );
            updateLabels();
            SoldierItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(SoldierItem.m_cost));

            // Store history of bought items:
            getCurrentPlayerObj().getItemsHistory().add("Soldier"); // the user has just bought a soldier unit
        }
    }

    @FXML
    private void onBuyWarriorBtnClicked(ActionEvent event){
        Hex currHex = findHex(getKeyFromPolygonsMap(activePolygon));

        if(getCurrentPlayerObj().getMoney() >= WarriorItem.m_cost && currHex.getState().equals(HexState.EMPTY) && doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.SOLDIER_3);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - WarriorItem.m_cost );
            updateLabels();
            WarriorItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(WarriorItem.m_cost));

            // Store history of bought items:
            getCurrentPlayerObj().getItemsHistory().add("Warrior"); // the user has just bought a warrrior unit
        }
    }

    private Player getCurrentPlayerObj(){ return gameBoard.getPlayersList().get( gameBoard.getCurrPlayerIndex() ); }

    // Finds Hex tile with associated string key
    private Hex findHex(String key){
        ArrayList<Hex> hexMap = gameBoard.getHexMap();
        for(Hex m_Hex : hexMap){
            if(m_Hex.getID().equals(key))
                return m_Hex;
        }
        return null;
    }

    private void drawHexMap(){
        ArrayList<Hex> map = gameBoard.getHexMap();

        // Draw hexagon polygons:
        for(Hex m_hex : map){
            Polygon m_polygon = new Polygon();

            // Go though vertices and add them to polygon object
            ArrayList<Point> vertices = m_hex.getVertices();
            for(Point vertex : vertices){
                m_polygon.getPoints().addAll(vertex.getX(), vertex.getY());
            }

            // Polygon's visual appeal
            paintPolygon(m_polygon);
            m_polygon.setStroke(Color.BLACK);
            m_polygon.setStrokeWidth(1.1);

            polygons.put(m_hex.getID(), m_polygon);
            drawingArea.getChildren().addAll(m_polygon);

            // Primary players' hex tiles
            if(!m_hex.getOwner().equals("abandoned")){
                paintPolygon(m_polygon, m_hex.getColor());

                // Do stuff WHEN the game starts (initializes default players)
                if(m_hex.getState().equals(HexState.HOUSE)){
                    // Add game object to map
                    BuyableItem hut = new HouseItem(m_hex.getCoords(), m_hex.getID(), getCurrentPlayerObj().getName());  // create a hut image
                    drawnGameObjects.put(m_hex.getID(), hut);                                                            // add game object to map
                    drawingArea.getChildren().addAll(hut.getLabel());                                                    // add it to pane
                }
            }
        }
    }

    private void paintPolygon(Polygon polygon){
        polygon.setFill(Color.LIGHTGRAY);
    }

    private void paintPolygon(Polygon polygon, HexColor color){
        switch (color){
            case YELLOW:
                polygon.setFill(Color.YELLOW);
                break;
            case GREEN:
                polygon.setFill(Color.GREEN);
                break;
            case RED:
                polygon.setFill(Color.RED);
                break;
            case PINK:
                polygon.setFill(Color.PINK);
                break;
            case BLUE:
                polygon.setFill(Color.BLUE);
                break;
            default:
                polygon.setFill(Color.LIGHTGRAY);
                break;
        }
    }
}
