package com.slayfx.gui;

import com.slayfx.logic.GameBoard;
import com.slayfx.logic.player.Player;
import com.slayfx.logic.tiles.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private GameBoard gameBoard;           // Game board
    private Map<Polygon, String> polygons; // Holds drawn polygons
    private Map<BuyableItem, String> drawnGameObjects = new HashMap<BuyableItem, String>();
    private Polygon activePolygon; // Polygon that user has clicked with the mouse

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
        drawHexMap();   // draws map

        // By default make activePolygon point to the first polygon object
        Map.Entry<Polygon,String> entry = polygons.entrySet().iterator().next(); // iterator
        activePolygon = entry.getKey();                                          // get first value

        updateLabels(); // updates labels

        // Initialize mouse event for each polygon (Hexagon)
        for(final Map.Entry<Polygon, String> hex : polygons.entrySet()){
            hex.getKey().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { // Run once a player clicks on any hex tile
                    // Find corresponding Hex tile and change its color
                    Hex hexTile = findHex(hex.getValue());     // Get Hex object
                    Polygon polygon = hex.getKey();            // Get Polygon from hex map

                    activePolygon.setStroke(Color.BLACK); // reset stroke of the previous polygon
                    activePolygon = polygon;              // we get a new active corresponding polygon
                    highlighActiveHex();                  // highligh it

                    // TODO: IF STATEMENTS WHETHER IT'S A VALID MOVE TO CONQUIRE A HEX TILE
                    if(!hexTile.getOwner().equals( getCurrentPlayerObj().getName()) ) {
                        // Display the attribute values of the hex tile
                        System.out.println(hexTile.toString());
                        // TODO: add soldiers to selected tile / move soldier from selected tile to another tile
                        // oldX <==> newX and oldY <==> newY
                        //paintPolygon(polygon, gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getColor()); // Change its color on click
                        //hexTile.changeOwner(gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getName());
                    }
                }
            });
        }
    }

    private void highlighActiveHex(){
        activePolygon.setStroke(Color.FLORALWHITE);
    }

    private void updateLabels(){
        playerLabel.setText("Turn: " + getCurrentPlayerObj().getName());
        moneyLabel.setText("Money: " + Integer.toString( getCurrentPlayerObj().getMoney() ));
    }

    @FXML
    private void onNextTurnBtnClicked(ActionEvent event){
        // TODO: cia vyktu priskyrimas  oldActivePolygon == null

        System.out.println("Next turn!");
        getCurrentPlayerObj().setMoney( getCurrentPlayerObj().getMoney() + 50 );
        gameBoard.changeCurrPlayerIndex(gameBoard.getCurrPlayerIndex() + 1);

        updateLabels();

        // TEST label stuff
        // Get first label in drawnGameObjects and change its coordinates
        Map.Entry<BuyableItem,String> entry = drawnGameObjects.entrySet().iterator().next(); // iterator
        BuyableItem item = entry.getKey();                                                   // get first value
        item.move(541 - 13, 379 - 13);                                                  // change position
        item.getLabel().toFront();                                                           // move the Node to the front of its sibling nodes
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

        if(hex != null && !state.equals(HexState.EMPTY) && hex.getOwner().equals("abandoned")){
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
        addDrawnObjectToMap(HexState.TOWER);
    }

    @FXML
    private void onBuyPeasantBtnClicked(ActionEvent event){
        addDrawnObjectToMap(HexState.SOLDIER_1);
    }

    @FXML
    private void onBuySlodierBtnClicked(ActionEvent event){
        addDrawnObjectToMap(HexState.SOLDIER_2);
    }

    @FXML
    private void onBuyWarriorBtnClicked(ActionEvent event){
        addDrawnObjectToMap(HexState.SOLDIER_3);
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

            polygons.put(m_polygon, m_hex.getID());
            drawingArea.getChildren().addAll(m_polygon);

            // TODO: move this to a logical part
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
