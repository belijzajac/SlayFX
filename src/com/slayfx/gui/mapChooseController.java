package com.slayfx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

public class mapChooseController {
    @FXML private Slider playersSlider;
    @FXML private Button map_1;
    @FXML private Button mapCreatorBtn;

    private static int numPlayers = 2;

    public static int getPlayers(){
        return numPlayers;
    }

    @FXML
    private void onMap1BtnClicked(ActionEvent event){
        try {
            numPlayers = (int) playersSlider.getValue(); // get players count from the slider
            Main.gotoGameStage();                        // show the game's stage
        } catch (Exception ex) {
            System.out.println("Caught an exception: " + ex);
        }
    }

    @FXML
    private void onMapCreatorBtnClicked(ActionEvent event){
        System.out.println("Feature not implemented");
    }
}
