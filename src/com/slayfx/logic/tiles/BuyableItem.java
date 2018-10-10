package com.slayfx.logic.tiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class BuyableItem extends GameObject {
    private String m_owner;  // player who owns this item
    private int m_moveCount; // how far diagonally can the item be moved
    private int m_damage;    // damage the item deals
    @FXML Label m_label;
    @FXML Image m_image;

    public static final String house_img = "../res/images/hut.png";
    public static final String tower_img = "../res/images/tower.png";
    public static final String peasant_img = "../res/images/soldier_1.png";
    public static final String soldier_img = "../res/images/soldier_2.png";
    public static final String warrior_img = "../res/images/soldier_3.png";

    public BuyableItem(String owner, String image_location, Point coords){
        super();

        setCoords(coords);
        m_owner = owner;

        String imageLoc = "";
        switch (image_location){
            case house_img : imageLoc = house_img;
                break;
            case tower_img : imageLoc = tower_img;
                break;
            case peasant_img : imageLoc = peasant_img;
                break;
            case soldier_img : imageLoc = soldier_img;
                break;
            case warrior_img : imageLoc = warrior_img;
                break;
        }

        // Set up image
        m_label = new Label();
        m_image = new Image(new File(imageLoc).toURI().toString());

        m_label.relocate(getCoords().getX() - 13, getCoords().getY() - 13);
        ImageView imageView = new ImageView(m_image);

        imageView.setFitWidth(25);
        imageView.setFitHeight(25);

        m_label.setGraphic(imageView);
        m_label.setDisable(true);
    }

    public boolean move(Hex hex){ return true; }

    public int getMovementCount(){ return m_moveCount; }
    public void setMovementCount(int count){ m_moveCount = count; }

    public int getDamage(){ return m_damage; }
    public void setDamage(int dmg){ m_damage = dmg; }

    public String getOwner(){ return m_owner; }
    public void setOwner(String ownr){ m_owner = ownr; }

    public Label getLabel(){ return m_label; }
}
