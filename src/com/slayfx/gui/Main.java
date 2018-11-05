package com.slayfx.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;
    private static Main instance;

    public Main(){
        instance = this;
    }

    public static Main getInstance(){
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try{
            stage = primaryStage;
            gotoMainStage();
            primaryStage.setTitle("SlayFX");
            primaryStage.show();
        } catch (Exception ex){
            System.out.println("Caught an exception: " + ex);
        }
    }

    // Close all threads (running timers, etc.) when all the stages are closed
    @Override
    public void stop(){
        // Terminate timer in gameStage on window close
        gameController.cancelTimer();
        Platform.exit();
    }

    private void gotoMainStage(){
        try{
            replaceSceneContent("mainWindowLayout.fxml");
        }catch (Exception ex){
            System.out.println("Caught an exception: " + ex);
        }
    }

    static void gotoLevelChoosingStage(){
        try{
            replaceSceneContent("mapChooseLayout.fxml");
        }catch (Exception ex){
            System.out.println("Caught an exception: " + ex);
        }
    }

    static void gotoGameStage(){
        try{
            replaceSceneContent("gameLayout.fxml");
        }catch (Exception ex){
            System.out.println("Caught an exception: " + ex);
        }
    }

    private static void replaceSceneContent(final String fxmlFile) throws Exception{
        Parent root = FXMLLoader.load(Main.class.getResource(fxmlFile));
        Scene scene = stage.getScene();
        if(scene == null){
            scene = new Scene(root, 780, 520);
            stage.setScene(scene);
        }else{
            stage.getScene().setRoot(root);
        }
        stage.sizeToScene();
    }
}
