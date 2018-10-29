package com.slayfx.gui;

import com.slayfx.logic.GameBoard;
import com.slayfx.logic.player.Player;
import com.slayfx.logic.tiles.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class gameController {
    private GameBoard gameBoard;           // Game board
    private Map<Polygon, String> polygons; // Holds drawn polygons
    private Map<BuyableItem, String> drawnGameObjects;
    private Polygon activePolygon; // Polygon that user has clicked with the mouse
    private Polygon oldactivePolygon;
    private boolean hadUserClickedOnHex = false;

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
        // Initialize game board and map
        gameBoard = new GameBoard(500, 500);
        polygons = new HashMap<Polygon, String>();
        drawnGameObjects  = new HashMap<BuyableItem, String>();
        drawHexMap();   // draws map

        // By default make activePolygon point to the first polygon object
        Map.Entry<Polygon,String> entry = polygons.entrySet().iterator().next(); // iterator
        activePolygon = oldactivePolygon = entry.getKey();                       // get first value

        updateLabels(); // updates labels

        // Initialize mouse event for each polygon (Hexagon)
        for(final Map.Entry<Polygon, String> hex : polygons.entrySet()){
            hex.getKey().setOnMouseClicked(event -> {      // Run once a player clicks on any hex tile
                // Find corresponding Hex tile and change its color
                Hex hexTile = findHex(hex.getValue());     // Get Hex object
                Polygon polygon = hex.getKey();            // Get Polygon from hex map

                activePolygon.setStroke(Color.BLACK);      // reset stroke of the previous polygon
                activePolygon = polygon;                   // we get a new active corresponding polygon

                highlighActiveHex();                       // highlight it

                if(hadUserClickedOnHex){
                    oldactivePolygon.setStroke(Color.GREENYELLOW);

                    // Get that previous hex:
                    Hex oldHex = findHex( polygons.get( oldactivePolygon ) );
                    if(oldHex == null){
                        System.out.println("oldHex: null");
                    }else{
                        System.out.println("oldHex: " + oldHex.getOwner() + " " + oldHex.getState() + " key=" + oldHex.getID());
                    }

                    // Get that previous item:
                    BuyableItem oldItem = findBuyableItem( oldHex.getID() );
                    if(oldItem == null){
                        System.out.println("oldItem: null");
                    }else{
                        System.out.println("oldItem: " + oldItem.getOwner());
                    }

                    // Get item which we want to conquer
                    BuyableItem itemToBeConquered = findBuyableItem( hexTile.getID() );
                    if(itemToBeConquered == null){
                        System.out.println("itemToBeConquered: null");
                    }else{
                        System.out.println("itemToBeConquered: " + itemToBeConquered.getOwner());
                    }

                    System.out.println("\n");

                    if(oldHex != null && oldItem != null && oldHex.getOwner().equals( getCurrentPlayerObj().getName() ) && oldItem.getOwner().equals( getCurrentPlayerObj().getName() )){
                        // Checks if it's a valid move
                        if( oldItem.getCurrMovementCount() > 0 && canMoveDiagonally(oldHex, hexTile) && oldItem.move( hexTile ) ){ // <-- move it where activePolygon points to
                            oldItem.decreaseCurrMovementCount();

                            // Remove that item from the game board
                            if(itemToBeConquered != null){
                                popFromDrawnGameObjects(itemToBeConquered); // pop it from of drawnGameObjects map
                            }

                            oldItem.getLabel().toFront();

                            // reassign attributes to the new hex:
                            hexTile.changeState( oldHex.getState() );                                           // change state of a hex
                            oldHex.changeState( HexState.EMPTY );
                            hexTile.changeOwner( getCurrentPlayerObj().getName() );
                            hexTile.changeColor( getCurrentPlayerObj().getColor() );              // change color
                            paintPolygon(activePolygon, getCurrentPlayerObj().getColor());    // apply new color

                            oldactivePolygon = activePolygon;
                        }
                    }
                    hadUserClickedOnHex = false;
                }

                // If this isn't an empty hex, then you can move its content
                if(!hexTile.getState().equals(HexState.EMPTY)){
                    oldactivePolygon = activePolygon;
                    oldactivePolygon.setStroke(Color.GREENYELLOW);
                    hadUserClickedOnHex = true;
                }
            });
        }
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
        drawnGameObjects.remove(itemToPop);                     // remove item from drawnGameObjects map
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
    }

    @FXML
    private void onNextTurnBtnClicked(ActionEvent event){
        System.out.println("Next turn!");
        getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() + 10 );
        gameBoard.changeCurrPlayerIndex(gameBoard.getCurrPlayerIndex() + 1);

        GameBoard.m_turnCount++;

        //oldactivePolygon = activePolygon;

        updateLabels();

        // Increase game difficulty every 3 turns if certain conditions are met
        if(GameBoard.m_turnCount % 3 == 0)
            increaseDifficulty();

        // Reset active polygon selection
        for(final Map.Entry<Polygon, String> pol : polygons.entrySet())
            pol.getKey().setStroke(Color.BLACK); // reset stroke of the previous polygon

        // Reset movement count of all game items
        for(final Map.Entry<BuyableItem, String> drawnObj : drawnGameObjects.entrySet()){
            drawnObj.getKey().resetMovementCountToDefault();
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
        Hex hex = findHex( polygons.get( activePolygon ) ); // polygons.get( activePolygon ) returns String m_ID

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
            drawnGameObjects.put(item, hex.getID());            // add game object to map
            drawingArea.getChildren().addAll(item.getLabel());  // add it to pane
        }
    }

    @FXML
    private void onBuyTowerBtnClicked(ActionEvent event){
        Hex currHex = findHex( polygons.get( activePolygon ) );

        if(getCurrentPlayerObj().getMoney() >= TowerItem.m_cost && currHex.getState().equals(HexState.EMPTY)){
            addDrawnObjectToMap(HexState.TOWER);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - TowerItem.m_cost );
            updateLabels();
            TowerItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(TowerItem.m_cost));
        }
    }

    @FXML
    private void onBuyPeasantBtnClicked(ActionEvent event){
        Hex currHex = findHex( polygons.get( activePolygon ) );

        if(getCurrentPlayerObj().getMoney() >= PeasantItem.m_cost && currHex.getState().equals(HexState.EMPTY)){
            addDrawnObjectToMap(HexState.SOLDIER_1);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - PeasantItem.m_cost );
            updateLabels();
            PeasantItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(PeasantItem.m_cost));
        }
    }

    @FXML
    private void onBuySlodierBtnClicked(ActionEvent event){
        Hex currHex = findHex( polygons.get( activePolygon ) );

        if(getCurrentPlayerObj().getMoney() >= SoldierItem.m_cost && currHex.getState().equals(HexState.EMPTY)){
            addDrawnObjectToMap(HexState.SOLDIER_2);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - SoldierItem.m_cost );
            updateLabels();
            SoldierItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(SoldierItem.m_cost));
        }
    }

    @FXML
    private void onBuyWarriorBtnClicked(ActionEvent event){
        Hex currHex = findHex( polygons.get( activePolygon ) );

        if(getCurrentPlayerObj().getMoney() >= WarriorItem.m_cost && currHex.getState().equals(HexState.EMPTY)){
            addDrawnObjectToMap(HexState.SOLDIER_3);
            getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() - WarriorItem.m_cost );
            updateLabels();
            WarriorItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(WarriorItem.m_cost));
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

    private BuyableItem findBuyableItem(String key){
        // Go through map of drawn game objects and search for key
        for(final Map.Entry<BuyableItem, String> drawnObj : drawnGameObjects.entrySet()){
            if(drawnObj.getKey().getID().equals(key)){ // we found key
                return drawnObj.getKey();              // return object
            }
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

            polygons.put(m_polygon, m_hex.getID());
            drawingArea.getChildren().addAll(m_polygon);

            // Primary players' hex tiles
            if(!m_hex.getOwner().equals("abandoned")){
                paintPolygon(m_polygon, m_hex.getColor());

                // Do stuff WHEN the game starts (initializes default players)
                if(m_hex.getState().equals(HexState.HOUSE)){
                    // Add game object to map
                    BuyableItem hut = new HouseItem(m_hex.getCoords(), m_hex.getID(), getCurrentPlayerObj().getName());  // create a hut image
                    drawnGameObjects.put(hut, m_hex.getID());                                                            // add game object to map
                    drawingArea.getChildren().addAll(hut.getLabel());                                                    // add it to pane
                    m_hex.changeOwner(getCurrentPlayerObj().getName());    // change owner
                    m_hex.changeColor( getCurrentPlayerObj().getColor() ); // change color
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
