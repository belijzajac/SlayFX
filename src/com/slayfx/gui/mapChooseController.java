package com.slayfx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class mapChooseController {
    @FXML private Button map_1;
    @FXML private Button mapCreatorBtn;

    @FXML
    private void onMap1BtnClicked(ActionEvent event){
        try {
            Main.gotoGameStage();
        } catch (Exception ex) {
            System.out.println("Caught an exception: " + ex);
        }
    }

    @FXML
    private void onMapCreatorBtnClicked(ActionEvent event){
        System.out.println("Feature not implemented");
    }
}
