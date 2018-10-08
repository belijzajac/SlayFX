package com.slayfx.logic.tiles;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class HouseItem extends BuyableItem {
    private final String house_img = "../res/images/hut.png";

    public HouseItem(Point coords, String id, String owner){
        super(owner);

        setCoords(coords);
        setID(id);

        // Init fields:
        m_cost = 0;
        setMovementCount(0); // towers cant move
        setDamage(0);        // towers do no damage

        // Set up image
        m_label = new Label();
        m_image = new Image(new File(house_img).toURI().toString());

        m_label.relocate(getCoords().getX() - 13, getCoords().getY() - 13);
        ImageView imageView = new ImageView(m_image);

        imageView.setFitWidth(25);
        imageView.setFitHeight(25);

        m_label.setGraphic(imageView);
        m_label.setDisable(true);
    }

    @Override
    public void move(int x, int y){
       ; // you can't move this thing
    }
}
