package com.slayfx.gui;

import com.slayfx.libs.DateAndTime.*;
import com.slayfx.logic.GameBoard;
import com.slayfx.logic.player.Player;
import com.slayfx.logic.tiles.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Button;
import java.util.*;
import java.util.stream.Collectors;

public class gameController {
    private GameBoard gameBoard;                          // Game board
    private Map<String, Polygon> polygons;                // Holds drawn polygons
    private Polygon activePolygon;                        // Polygon that user has clicked with the mouse
    private Polygon oldactivePolygon;
    private boolean hadUserClickedOnHex = false;
    private boolean hasGameEnded = false;

    // Timer:
    private static Timer timer;
    private ControlDateAndTime timeElapsed;
    @FXML private Label timeLabel;

    // Variables that refer to GUI elements
    @FXML private Pane drawingArea;
    @FXML private Button btnNextTurn;
    @FXML private Label playerLabel;
    @FXML private Label moneyLabel;
    @FXML private Button towerBtn;
    @FXML private Button soldier1Btn;
    @FXML private Button soldier2Btn;
    @FXML private Button soldier3Btn;
    @FXML private ListView<String> playersInfoList;

    @FXML
    public void initialize(){
        // Build time elapsed
        DateAndTimeBuilder builder = new BuildDataAndTime();
        timeElapsed = builder.buildMinutes().buildSeconds().buildDate();

        // Initialize game board and map
        gameBoard = GameBoard.getInstance();
        //if(!gameBoard.isOkToContinueGame()){
            //System.out.println("Quitting the game...");
          //  Main.getInstance().stop();
        //}

        polygons = new HashMap<String, Polygon>();
        drawHexMap();   // draws map
        setUpTimer();

        // By default make activePolygon point to the first polygon object
        Map.Entry<String, Polygon> entry = polygons.entrySet().iterator().next();  // iterator
        activePolygon = oldactivePolygon = entry.getValue();                       // get first value

        updateLabels(); // updates labels

        // @param oldItem and oldHex are what current player has in possession
        // @param itemToBeConquired - is what we step on with OldItem
        // Initialize mouse click event for each polygon (Hexagon)
        for(final Map.Entry<String, Polygon> hex : polygons.entrySet()) {
            hex.getValue().setOnMouseClicked(event -> {      // Run once a player clicks on any hex tile
                // Find corresponding Hex tile and change its color
                Hex hexTile = gameBoard.findHex(hex.getKey()); // Get Hex object
                Polygon polygon = hex.getValue();              // Get Polygon from hex map

                activePolygon.setStroke(Color.BLACK);      // reset stroke of the previous polygon
                activePolygon = polygon;                   // we get a new active corresponding polygon

                highlighActiveHex();                       // highlight it

                if (hadUserClickedOnHex) {
                    oldactivePolygon.setStroke(Color.GREENYELLOW);

                    // Get that previous hex:
                    Hex oldHex = gameBoard.findHex(getKeyFromPolygonsMap(oldactivePolygon));

                    // Get that previous item:
                    BuyableItem oldItem = gameBoard.getDrawnGameObjects().get(oldHex.getID());

                    // Get item which we want to conquer
                    BuyableItem itemToBeConquered = gameBoard.getDrawnGameObjects().get(hexTile.getID());

                    // Abstract checks
                    if (oldHex != null && oldItem != null && oldHex.getOwner().equals(gameBoard.getCurrentPlayerObj().getName()) && oldItem.getOwner().equals(gameBoard.getCurrentPlayerObj().getName())) {

                        // Checks if it's a valid move
                        if (oldItem.getCurrMovementCount() > 0 && gameBoard.canMoveDiagonally(oldHex, hexTile)) {
                            boolean killItselfScenario = false;

                            // Own player can't kill itself
                            if(itemToBeConquered != null && itemToBeConquered.getOwner().equals( oldItem.getOwner() ))
                                killItselfScenario = true;

                            if(!killItselfScenario && oldItem.move(hexTile)){ // <-- move it where activePolygon points to

                                oldItem.decreaseCurrMovementCount();

                                // Update an element in HashMap (delete and put)
                                // Because we're about to change its associative key
                                gameBoard.getDrawnGameObjects().remove(oldHex.getID());

                                // Remove that item from the game board
                                if (itemToBeConquered != null) {
                                    if (itemToBeConquered instanceof HouseItem) {                              // we just conquered player's home tile
                                        for(Player playerToBeDeleted : gameBoard.getPlayersList()){
                                            if (!playerToBeDeleted.isDead() && playerToBeDeleted.getName().equals(itemToBeConquered.getOwner())) {
                                                System.out.println("~~ Killing " + playerToBeDeleted.getName() + "'s units...... ~~");
                                                killAllPlayersUnits(playerToBeDeleted);
                                                playerToBeDeleted.endLife();
                                                gameBoard.getActivePlayers().remove(playerToBeDeleted.getName());
                                                //gameBoard.getPlayersList().remove(playerToBeDeleted);
                                                break;
                                            }
                                        }
                                    }
                                    popFromDrawnGameObjects(itemToBeConquered); // pop it from of drawnGameObjects map
                                }

                                gameBoard.getDrawnGameObjects().put(oldItem.getID(), oldItem);
                                oldItem.getLabel().toFront();

                                // reassign attributes to the new hex:
                                hexTile.changeState(oldHex.getState());                                           // change state of a hex
                                oldHex.changeState(HexState.EMPTY);
                                oldHex.changeColor(oldHex.getColor());
                                hexTile.changeOwner(oldHex.getOwner());
                                hexTile.changeColor(oldHex.getColor());              // change color
                                paintPolygon(activePolygon, oldHex.getColor());    // apply new color

                                oldactivePolygon = activePolygon;

                                // Game win scenario:
                                if(gameBoard.getActivePlayers().size() == 1 && !hasGameEnded){
                                    System.out.println(oldItem.getOwner() + " WON! ");
                                    gameWonMessage( oldItem.getOwner() );

                                    hasGameEnded = true; // will not show the message again
                                }
                            }
                        }
                    }


                    hadUserClickedOnHex = false;
                }

                // If this isn't an empty hex, then you can move its content
                if (!hexTile.getState().equals(HexState.EMPTY)) {
                    oldactivePolygon = activePolygon;
                    oldactivePolygon.setStroke(Color.GREENYELLOW);
                    hadUserClickedOnHex = true;
                }
            });
        }
    }

    // Gets key from Polygon's map by providing a value _value
    private String getKeyFromPolygonsMap(Polygon _value){
        Set<Map.Entry<String, Polygon>> mapSet = polygons.entrySet();
        for(Map.Entry<String, Polygon> entry : mapSet){
            if(entry.getValue().equals(_value)){
                return entry.getKey();
            }
        }
        return null;
    }

    private void setUpTimer(){
        // Create a timer and schedule it to run every 1 second
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if(timeElapsed.getMinutes().equals("00") && timeElapsed.getSeconds().equals("00"))
                                    onNextTurnBtnClicked();
                                else{
                                    timeElapsed.subSeconds(1);
                                    timeLabel.setText(timeElapsed.getMinutes() + ":" + timeElapsed.getSeconds());
                                }
                            }
                        });
                    }
                },
                100,      // run first occurrence after 100 ms
                1000);  // run every seconds
    }

    static void cancelTimer(){
        if(timer != null)
            timer.cancel();
    }

    // Basically removes this object from the game
    private void popFromDrawnGameObjects(BuyableItem itemToPop){
        // Decrease its total quantity in the game
        itemToPop.decreaseCount();

        // Remove item from the game:
        itemToPop.getLabel().toBack();                              // just in case if something goes horribly wrong
        drawingArea.getChildren().remove(itemToPop.getLabel());     // remove item from drawingArea
        gameBoard.getDrawnGameObjects().remove(itemToPop.getID());  // remove item from drawnGameObjects map
        itemToPop = null;                                           // call a garbage collector on it
    }

    private void highlighActiveHex(){
        activePolygon.setStroke(Color.FLORALWHITE);
    }

    private void updateLabels(){
        playerLabel.setText("Turn: " + gameBoard.getCurrentPlayerObj().getName());
        moneyLabel.setText("Money: " + Integer.toString( gameBoard.getCurrentPlayerObj().getMoney() ));

        // Color the player label to help distinguish which player's turn this is
        switch (gameBoard.getCurrentPlayerObj().getColor()){
            case GREEN: playerLabel.setStyle("-fx-background-color: green;");
                break;
            case RED: playerLabel.setStyle("-fx-background-color: red;");
                break;
            case BLUE: playerLabel.setStyle("-fx-background-color: blue;");
                break;
            case PINK: playerLabel.setStyle("-fx-background-color: pink;");
                break;
            case YELLOW: playerLabel.setStyle("-fx-background-color: yellow;");
                break;
        }

        // Update items prices:
        towerBtn.setText("= " + Integer.toString(TowerItem.m_cost));
        soldier1Btn.setText("= " + Integer.toString(PeasantItem.m_cost));
        soldier2Btn.setText("= " + Integer.toString(SoldierItem.m_cost));
        soldier3Btn.setText("= " + Integer.toString(WarriorItem.m_cost));

        // Update playersInfoList's content
        updatePlayersInfo();
    }

    @FXML
    private void onNextTurnBtnClicked(){
        System.out.println("Next turn!");

        onNextTurnKillUnits();
        onNextTurnWarriorsLocation();
        onNextTurnMoneyInCirculation();

        gameBoard.changeCurrPlayerIndex(gameBoard.getCurrPlayerIndex() + 1);
        gameBoard.getCurrentPlayerObj().setMoney( gameBoard.getCurrentPlayerObj().getMoney() + 10 );

        GameBoard.m_turnCount++;
        updateLabels();

        // Increase game difficulty every 3 turns if certain conditions are met
        if(GameBoard.m_turnCount % 3 == 0)
            gameBoard.increaseDifficulty();

        // Reset active polygon selection
        polygons.values().parallelStream().forEach(e -> e.setStroke(Color.BLACK));

        // Reset movement count of all game items
        gameBoard.getDrawnGameObjects().values().parallelStream().forEach(e -> e.resetMovementCountToDefault());

        // Reset timer:
        timeElapsed.setMinutes(1);
        timeElapsed.setSeconds(0);

        // Does AI stuff: buys items and moves units around
        // TODO: do this until there's at least player_ alive or >= 2 AI_ alive
        if(gameBoard.getCurrentPlayerObj().getName().contains("AI")){
            gameBoard.getCurrentPlayerObj().buyItems();
            updateBoughtItems();
            onNextTurnBtnClicked(); // next turn
        }
    }

    private void updateBoughtItems(){
        // 1) add bought item
        // 2) remove bought item
        for(Hex hex : gameBoard.getHexMap()){
            String key = hex.getID();
            HexState hexState = hex.getState();

            BuyableItem item = gameBoard.getDrawnGameObjects().get(key);
            
            // 1) You bought an item and need for it to appear in the game board
            if(!hexState.equals(HexState.EMPTY) && item == null){
                paintPolygon(polygons.get(key), hex.getColor());    // paint polygon
                addDrawnObjectToMap(hex.getState(), hex);           // add that new item to the game board
            }
            // 2) You conquered an item and it needs to be removed from the game board
            // TODO

        }
    }

    // Kill all player's units if they cant be fed anymore
    private void onNextTurnKillUnits(){
        List<Player> playersStream = gameBoard.getPlayersList().stream()     // convert list to stream
                .filter(player -> player.getMoney() <= 0)                    // a player has got no money
                .collect(Collectors.toList());                               // collect the output and convert streams to a List

        playersStream.forEach( this::killAllPlayersUnits );
    }

    private void onNextTurnWarriorsLocation(){
        // Using parallel stream to find location of warriors (the most powerful unit)
        // if the game board consists of 1000 hex tiles, it's a fast approach to process such data
        gameBoard.getHexMap().parallelStream()
                .filter(e -> e.getState() == HexState.SOLDIER_3)
                .forEach(e -> System.out.println("warrior found at : " + e.getID()));
    }

    private void onNextTurnMoneyInCirculation(){
        // stream().map() lets you convert an object to something else
        // stream().reduce() takes a sequence of input elements and combines them into a single result
        // Money of all players combined:
        Integer moneyOfAllPLayers = gameBoard.getPlayersList().stream()
                .map(e -> e.getMoney())
                .reduce(0, Integer::sum); // calculate money from 0
        System.out.println("Money circulation in the game: " + moneyOfAllPLayers);
    }

    private void killAllPlayersUnits(Player player){
        // Kill all the units if they were bought under same player name
        // drawnGameObjects.entrySet().removeIf(e -> e.getValue().getOwner().equals(player.getName()));
        for(Iterator<BuyableItem> it = gameBoard.getDrawnGameObjects().values().iterator(); it.hasNext();){
            BuyableItem itemToPop = it.next();

            if(itemToPop.getOwner().equals(player.getName()) && itemToPop.getMaxMovementCount() > 0) {
                itemToPop.decreaseCount();
                itemToPop.getLabel().toBack();
                drawingArea.getChildren().remove(itemToPop.getLabel());

                Hex hex = gameBoard.findHex( itemToPop.getID() );
                if(hex != null)
                    hex.changeState(HexState.EMPTY);

                it.remove();
            }
        }
    }

    private void addDrawnObjectToMap(HexState whatItem, Hex toWhichHexPlaceItem){
        HexState state = HexState.EMPTY;

        // Determine what state is that
        switch (whatItem){
            case HOUSE: state = HexState.HOUSE;
                break;
            case TOWER: state = HexState.TOWER;
                break;
            case SOLDIER_1: state = HexState.SOLDIER_1;
                break;
            case SOLDIER_2: state = HexState.SOLDIER_2;
                break;
            case SOLDIER_3: state = HexState.SOLDIER_3;
                break;
        }

        if(toWhichHexPlaceItem != null && !state.equals(HexState.EMPTY)){
            // Change hex tile attributes to new ones:
            toWhichHexPlaceItem.changeState(state);                                                     // change state of a hex
            toWhichHexPlaceItem.changeOwner(gameBoard.getCurrentPlayerObj().getName());                 // change owner
            toWhichHexPlaceItem.changeColor( gameBoard.getCurrentPlayerObj().getColor() );              // change color

            BuyableItem item = null;
            if(state.equals(HexState.HOUSE))
                item = new HouseItem(toWhichHexPlaceItem.getCoords(), toWhichHexPlaceItem.getID(), gameBoard.getCurrentPlayerObj().getName());
            else if(state.equals(HexState.TOWER))
                item = new TowerItem(toWhichHexPlaceItem.getCoords(), toWhichHexPlaceItem.getID(), gameBoard.getCurrentPlayerObj().getName());
            else if(state.equals(HexState.SOLDIER_1))
                item = new PeasantItem(toWhichHexPlaceItem.getCoords(), toWhichHexPlaceItem.getID(), gameBoard.getCurrentPlayerObj().getName());
            else if(state.equals(HexState.SOLDIER_2))
                item = new SoldierItem(toWhichHexPlaceItem.getCoords(), toWhichHexPlaceItem.getID(), gameBoard.getCurrentPlayerObj().getName());
            else if(state.equals(HexState.SOLDIER_3))
                item = new WarriorItem(toWhichHexPlaceItem.getCoords(), toWhichHexPlaceItem.getID(), gameBoard.getCurrentPlayerObj().getName());

            // Add that object to the drawnObjects map:
            gameBoard.getDrawnGameObjects().put(toWhichHexPlaceItem.getID(), item); // add game object to map
            drawingArea.getChildren().addAll(item.getLabel());                      // add it to pane
        }
    }

    @FXML
    private void onBuyTowerBtnClicked(){
        Hex currHex = gameBoard.findHex(getKeyFromPolygonsMap(activePolygon));

        if(gameBoard.getCurrentPlayerObj().getMoney() >= TowerItem.m_cost && currHex.getState().equals(HexState.EMPTY) && gameBoard.doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.TOWER, gameBoard.findHex(getKeyFromPolygonsMap(activePolygon)));
            paintPolygon(activePolygon, gameBoard.getCurrentPlayerObj().getColor());
            gameBoard.getCurrentPlayerObj().setMoney( gameBoard.getCurrentPlayerObj().getMoney() - TowerItem.m_cost );
            updateLabels();
            TowerItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(gameBoard.getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(TowerItem.m_cost));

            // Store history of bought items:
            gameBoard.getCurrentPlayerObj().getItemsHistory().add("Tower"); // the user has just bought a tower
        }
    }

    @FXML
    private void onBuyPeasantBtnClicked(){
        Hex currHex = gameBoard.findHex(getKeyFromPolygonsMap(activePolygon));

        if(gameBoard.getCurrentPlayerObj().getMoney() >= PeasantItem.m_cost && currHex.getState().equals(HexState.EMPTY) && gameBoard.doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.SOLDIER_1, gameBoard.findHex(getKeyFromPolygonsMap(activePolygon)));
            paintPolygon(activePolygon, gameBoard.getCurrentPlayerObj().getColor());
            gameBoard.getCurrentPlayerObj().setMoney( gameBoard.getCurrentPlayerObj().getMoney() - PeasantItem.m_cost );
            updateLabels();
            PeasantItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(gameBoard.getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(PeasantItem.m_cost));

            // Store history of bought items:
            gameBoard.getCurrentPlayerObj().getItemsHistory().add("Peasant"); // the user has just bought a peasant unit
        }
    }

    @FXML
    private void onBuySlodierBtnClicked(){
        Hex currHex = gameBoard.findHex(getKeyFromPolygonsMap(activePolygon));

        if(gameBoard.getCurrentPlayerObj().getMoney() >= SoldierItem.m_cost && currHex.getState().equals(HexState.EMPTY) && gameBoard.doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.SOLDIER_2, gameBoard.findHex(getKeyFromPolygonsMap(activePolygon)));
            paintPolygon(activePolygon, gameBoard.getCurrentPlayerObj().getColor());
            gameBoard.getCurrentPlayerObj().setMoney( gameBoard.getCurrentPlayerObj().getMoney() - SoldierItem.m_cost );
            updateLabels();
            SoldierItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(gameBoard.getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(SoldierItem.m_cost));

            // Store history of bought items:
            gameBoard.getCurrentPlayerObj().getItemsHistory().add("Soldier"); // the user has just bought a soldier unit
        }
    }

    @FXML
    private void onBuyWarriorBtnClicked(){
        Hex currHex = gameBoard.findHex(getKeyFromPolygonsMap(activePolygon));

        if(gameBoard.getCurrentPlayerObj().getMoney() >= WarriorItem.m_cost && currHex.getState().equals(HexState.EMPTY) && gameBoard.doDiagonalNeighborsHaveTheSameColor(currHex)){
            addDrawnObjectToMap(HexState.SOLDIER_3, gameBoard.findHex(getKeyFromPolygonsMap(activePolygon)));
            paintPolygon(activePolygon, gameBoard.getCurrentPlayerObj().getColor());
            gameBoard.getCurrentPlayerObj().setMoney( gameBoard.getCurrentPlayerObj().getMoney() - WarriorItem.m_cost );
            updateLabels();
            WarriorItem.m_count++;
            System.out.println("Curr money: " + Integer.toString(gameBoard.getCurrentPlayerObj().getMoney()) + "  Cost: " + Integer.toString(WarriorItem.m_cost));

            // Store history of bought items:
            gameBoard.getCurrentPlayerObj().getItemsHistory().add("Warrior"); // the user has just bought a warrrior unit
        }
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

            polygons.put(m_hex.getID(), m_polygon);
            drawingArea.getChildren().addAll(m_polygon);

            // Primary players' hex tiles
            if(!m_hex.getOwner().equals("abandoned")){
                paintPolygon(m_polygon, m_hex.getColor());

                // Do stuff WHEN the game starts (initializes default players)
                if(m_hex.getState().equals(HexState.HOUSE)){
                    // Add game object to map
                    BuyableItem hut = new HouseItem(m_hex.getCoords(), m_hex.getID(), m_hex.getOwner());  // create a hut image
                    gameBoard.getDrawnGameObjects().put(m_hex.getID(), hut);                              // add game object to map
                    drawingArea.getChildren().addAll(hut.getLabel());                                     // add it to pane
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

    private void gameWonMessage(final String userId){
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("The game has ended");
        alertDialog.setHeaderText(null);
        alertDialog.setContentText(userId + " has won!");
        alertDialog.show();
    }

    private void updatePlayersInfo(){
        playersInfoList.getItems().removeAll();
        playersInfoList.refresh();

        List<String> values = new ArrayList<>();

        for(Player pl : gameBoard.getPlayersList()){
            // calculate hex count owned by Player pl
            long hexOwnerCount = gameBoard.getHexMap().stream()
                    .filter(hex -> hex.getOwner().equals(pl.getName()))
                    .count();

            // calculate percentage:
            int percentage = (int) (((double) hexOwnerCount / (double) gameBoard.getHexMap().size()) * 100);

            String playerStatus = pl.getName() + " | " + (!pl.isDead()? "Alive" : "Dead ") + " | " + percentage + "%";
            values.add(playerStatus);
        }

        playersInfoList.setItems(FXCollections.observableList(values));
        playersInfoList.refresh();
    }
}
