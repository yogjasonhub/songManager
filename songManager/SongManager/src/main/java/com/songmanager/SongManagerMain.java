package com.songmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SongManagerMain extends Application {

    @Override
    public void start(Stage stage) throws IOException
    {
        //FXMLLoader fxmlLoader = new FXMLLoader(SongManagerMain.class.getResource("AddSongsView.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("BrowseSongsView.fxml"));
        Scene scene = new Scene(root, 1366, 768);
        stage.setScene(scene);
        stage.setTitle("Song Manager");
        stage.show();
    }
    public static void main(String[] args) throws Exception {launch();}
}