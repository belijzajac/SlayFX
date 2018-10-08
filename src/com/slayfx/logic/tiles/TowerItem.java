package com.slayfx.logic.tiles;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class TowerItem extends BuyableItem {
    private final String tower_img = "../res/images/tower.png";

    public TowerItem(Point coords, String id, String owner){
        super(owner);

        setCoords(coords);
        setID(id);

        // Init fields:
        m_cost = 15;
        setMovementCount(0); // towers cant move
        setDamage(0);        // towers do no damage

        // Set up image
        m_label = new Label();
        m_image = new Image(new File(tower_img).toURI().toString());

        m_label.relocate(getCoords().getX() - 13, getCoords().getY() - 13);
        ImageView imageView = new ImageView(m_image);

        imageView.setFitWidth(25);
        imageView.setFitHeight(25);

        m_label.setGraphic(imageView);
        m_label.setDisable(true);
    }

    // TODO: oldCoords <==> newCoords
    // TODO: dog - bark, cow - moo ====> DIFF BIHAVIOUR!!!
    // TODO: move the item and do specific things ONLY that item can do (like kill enemy)?????

    @Override
    public void move(int x, int y){
        m_label.relocate(x, y);
    }
}
