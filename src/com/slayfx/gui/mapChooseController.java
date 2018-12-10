package com.slayfx.gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class mapChooseController {
    @FXML private ListView<String> listView;
    @FXML private Slider playersSlider;
    @FXML private Button startBtn;
    @FXML private Button mapCreatorBtn;

    private static int numPlayers = 2;
    private static String chosenMap = "rhombus.map";

    public static int getPlayers(){
        return numPlayers;
    }

    public static String getChosenMap(){
        return chosenMap;
    }

    @FXML
    public void initialize() {

        // ArrayList containing map names
        List<String> values = new ArrayList<>();

        try {
            Files.walk(Paths.get("../res/maps/"))
                    .filter(Files::isRegularFile)
                    .forEach(e-> values.add(e.getFileName().toString().replace(".map", "")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Populate listView with values
        listView.setItems(FXCollections.observableList(values));
    }

    @FXML
    private void startBtnClicked(){
        try {
            // get players count from the slider
            numPlayers = (int) playersSlider.getValue();

            // get chosen map
            if(listView.getSelectionModel().getSelectedItem() != null)
                chosenMap = listView.getSelectionModel().getSelectedItem() + ".map";

            // show the game's stage
            Main.gotoGameStage();
        } catch (Exception ex) {
            System.out.println("Caught an exception: " + ex);
        }
    }

    @FXML
    private void onMapCreatorBtnClicked(ActionEvent event){
        try{
            Main.gotoMapCreator();
        } catch (Exception ex){
            System.out.println("Caught an exception: " + ex);
        }
    }
}
