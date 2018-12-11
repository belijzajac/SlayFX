package com.slayfx.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class mainWindowController {
    @FXML private Button singlePlayerBtn;

    @FXML
    private void onSinglePlayerBtnClicked(){
        try {
            Main.gotoLevelChoosingStage();
        } catch (Exception ex) {
            System.out.println("Caught an exception: " + ex);
        }
    }
}
