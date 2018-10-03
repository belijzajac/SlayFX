package com.slayfx.gui;

import com.slayfx.logic.GameBoard;
import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;
import com.slayfx.logic.tiles.Point;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private GameBoard gameBoard;           // Game board
    private Map<Polygon, String> polygons; // Holds drawn polygons

    // Variables that refer to GUI elements
    @FXML private Pane drawingArea;

    @FXML
    public void initialize(){
        // Initialize game board and map
        gameBoard = new GameBoard(500, 500);
        polygons = new HashMap<Polygon, String>();
        drawHexMap();

        // Initialize mouse event for each polygon (Hexagon)
        for(final Map.Entry<Polygon, String> hex : polygons.entrySet()){
            hex.getKey().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    // Find corresponding Hex tile and change its color
                    Hex hexTile = findHex(hex.getValue());     // Get Hex object

                    // Display the attribute values of the hex tile
                    System.out.println(hexTile.toString());

                    Polygon polygon = hex.getKey();            // Get Polygon from hex map
                    paintPolygon(polygon, hexTile.getColor()); // Change its color on click
                }
            });
        }
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
            case BLACK:
                polygon.setFill(Color.BLACK);
                break;
            default:
                polygon.setFill(Color.LIGHTGRAY);
                break;
        }
    }
}
