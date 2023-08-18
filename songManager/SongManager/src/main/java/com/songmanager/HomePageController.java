package com.songmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController
{
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void switchToBrowseSongsView(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("BrowseSongsView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root,1366,768);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAddRemoveSongsView(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("AddSongsView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root,1366,768);
        stage.setScene(scene);
        stage.show();
    }
}
