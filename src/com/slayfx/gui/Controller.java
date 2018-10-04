package com.slayfx.gui;

import com.slayfx.logic.GameBoard;
import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;
import com.slayfx.logic.tiles.HexState;
import com.slayfx.logic.tiles.Point;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private GameBoard gameBoard;           // Game board
    private Map<Polygon, String> polygons; // Holds drawn polygons

    // Variables that refer to GUI elements
    @FXML private Pane drawingArea;
    @FXML private Button btnNextTurn;
    @FXML private Label playerLabel;
    @FXML private Label moneyLabel;

    @FXML
    public void initialize(){
        // Initialize game board and map
        gameBoard = new GameBoard(500, 500);
        polygons = new HashMap<Polygon, String>();
        drawHexMap();   // draws map
        updateLabels(); // updates labels

        // Initialize mouse event for each polygon (Hexagon)
        for(final Map.Entry<Polygon, String> hex : polygons.entrySet()){
            hex.getKey().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { // Run once a player clicks on any hex tile
                    // Find corresponding Hex tile and change its color
                    Hex hexTile = findHex(hex.getValue());     // Get Hex object
                    Polygon polygon = hex.getKey();            // Get Polygon from hex map

                    // TODO: IF STATEMENTS WHETHER IT'S A VALID MOVE TO CONQUIRE A HEX TILE
                    if(!hexTile.getOwner().equals(gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getName())) {
                        // Display the attribute values of the hex tile
                        System.out.println(hexTile.toString());

                        paintPolygon(polygon, gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getColor()); // Change its color on click
                        hexTile.changeOwner(gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getName());
                    }
                }
            });
        }

        // TODO: main game loop?
        // ...???
    }

    private void updateLabels(){
        playerLabel.setText("Turn: " + gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getName());
        moneyLabel.setText("Money: " + Integer.toString( gameBoard.getPlayersList().get(gameBoard.getCurrPlayerIndex()).getMoney() ));
    }

    @FXML
    private void onNextTurnBtnClicked(ActionEvent event){ // TODO:
        System.out.println("Next turn!");
        gameBoard.changeCurrPlayerIndex(gameBoard.getCurrPlayerIndex() + 1);
        updateLabels();
    }

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
        Image hut_img = new Image(new File("res/images/tower.png").toURI().toString());

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

                if(m_hex.getState().equals(HexState.HOUSE)){
                    // TODO: implement drawing via canvas, preferably via polymorphism
                    //m_polygon.setFill(new ImagePattern(hut_img));
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
