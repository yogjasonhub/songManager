package com.songmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavBarController implements Initializable
{
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private Button homeBtn;
    @FXML private Button browseSongsBtn;
    @FXML private Button addRemoveSongsBtn;

    @FXML protected void homeBtnClick(ActionEvent event) throws IOException
    {
        switchToHomePageView(event);
    }

    @FXML protected void addRemoveSongsBtnClick(ActionEvent event) throws IOException
    {
        switchToAddRemoveSongsView(event);
    }

    @FXML protected void browseSongsBtnClick(ActionEvent event) throws IOException
    {
        switchToBrowseSongsView(event);
    }

    public void switchToHomePageView(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("HomePageView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root,1366,768);
        stage.setScene(scene);
        stage.show();
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
