package com.slayfx.gui;

import com.slayfx.logic.tiles.Hex;
import com.slayfx.logic.tiles.HexColor;
import com.slayfx.logic.tiles.Point;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class mapCreatorController {
    @FXML private Button saveMapBtn;
    @FXML private Button goBackBtn;
    @FXML private Pane drawingArea;

    private ArrayList<Hex> hexMap;                 // all the possible hex objects
    private ArrayList<Hex> hexToBeExported; // hex objects that will be exported
    public static boolean hasUserCreatedMap;

    @FXML
    public void initialize(){
        hexMap = new ArrayList<Hex>();
        hexToBeExported = new ArrayList<Hex>();
        hasUserCreatedMap = false;

        defaultMapLayout();

        // Draw default map (polygons)
        for(Hex m_hex : hexMap){
            Polygon m_polygon = new Polygon();

            m_polygon.setOnMouseClicked(event -> {
                m_polygon.setFill(Color.LIGHTCYAN);

                // check if hexagon with the same property has been added before
                // if not, then add it to the ArrayList
                if (!hexToBeExported.contains(m_hex)) {
                    hexToBeExported.add(m_hex);
                    hasUserCreatedMap = true;
                }
            });

            // Go though vertices and add them to polygon object
            ArrayList<Point> vertices = m_hex.getVertices();
            for(Point vertex : vertices){
                m_polygon.getPoints().addAll(vertex.getX(), vertex.getY());
            }

            // Polygon's visual appeal
            m_polygon.setFill(Color.DIMGREY);
            m_polygon.setStroke(Color.BLACK);
            m_polygon.setStrokeWidth(1.1);
            drawingArea.getChildren().addAll(m_polygon);
        }
    }

    private void defaultMapLayout(){
        // in case the user went back to map choosing layout and came back
        hexToBeExported.clear();

        // to shift rows in order to form rhombus (offset)
        int row_diff = 0;

        // Creates a map shaped like rhombus
        for(int col = 0; col< 14; col++){
            for(int row = 0; row< 14; row++){
                hexMap.add(new Hex(HexColor.GREEN, 36 * col + row_diff + 40, 31 * row + 50));
                row_diff += 18;
            }
            row_diff = 0;
        }
    }

    @FXML
    public void onSaveMapBtnClicked() {
        if(hasUserCreatedMap && hexToBeExported.size() >= 10){
            FileChooser fileChooser = new FileChooser();

            // Set extension filter
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MAP files (*.map)", "*.map");
            fileChooser.getExtensionFilters().add(extensionFilter);

            // Set the title and working directory
            fileChooser.setTitle("Save MAP file");
            fileChooser.setInitialDirectory(new File("../res/maps/"));

            // Show save file dialog
            File file = fileChooser.showOpenDialog(new Stage());

            if(file != null){
                List<String> hexCoords = hexToBeExported.stream()                                              // convert list to stream
                        .map(coords -> Double.toString(coords.getCoords().getX()).replace(".0", "") + ';' + 
                                       Double.toString(coords.getCoords().getY()).replace(".0", ""))           // extract coordinates as string
                        .collect(Collectors.toList());                                                         // collect the output and convert streams to a List

                // save extracted coordinates to file
                saveMapFile(hexCoords, file);
            }
        }else{
            System.out.println("At least 10 hexagons must be chosen");
        }
    }

    private void saveMapFile(final List<String> content, File file){
        try {
            PrintWriter writer = new PrintWriter(file);

            // write each coordinate on a new line
            for(String coord : content){
                writer.println(coord);
            }
            // close the file
            writer.close();
        } catch (IOException ex){
            System.out.println("Error saving file " + file);
        }
    }

    @FXML
    public void onGoBackBtnClicked() {
        Main.gotoLevelChoosingStage();
    }
}
