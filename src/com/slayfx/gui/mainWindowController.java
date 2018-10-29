package com.slayfx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class mainWindowController {
    @FXML private Button singlePlayerBtn;
    @FXML private Button onlineBtn;

    @FXML
    private void onSinglePlayerBtnClicked(ActionEvent event){
        try {
            Main.gotoLevelChoosingStage();
        } catch (Exception ex) {
            System.out.println("Caught an exception: " + ex);
        }
    }

    @FXML
    private void onOnlineBtnClicked(ActionEvent event){
        System.out.println("Feature not implemented");
    }
}
