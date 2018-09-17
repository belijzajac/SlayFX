package com.slayfx.gui;

import com.slayfx.logic.GameBoard;
import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.Point;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

public class Controller {
    private GameBoard gameBoard;         // Game board
    private ArrayList<Polygon> polygons; // Holds drawn polygons

    // Variables that refer to GUI elements
    @FXML private Pane drawingArea;

    public void initialize(){
        gameBoard = new GameBoard(500, 500);
        polygons = new ArrayList<Polygon>();
        drawHexMap();
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

            polygons.add(m_polygon);
            drawingArea.getChildren().addAll(m_polygon);
        }
    }
}
