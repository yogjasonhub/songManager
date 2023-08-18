package com.songmanager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class EditSongsController implements Initializable
{
    private DBManager dbManager;
    private String songToEdit;
    private String songTitleString;
    private String artistString;
    private String genreString;
    private String songLengthString;
    private String yearString;
    private String recordLabelString;
    private String albumString;
    private boolean validTitle;
    private boolean validArtist;
    private boolean validGenre;
    private boolean validRecordLabel;
    private boolean validAlbum;
    private boolean validLength;

    //Song title
    @FXML private Label songTitleLabel;
    @FXML private TextField songTitle;

    //Artist info
    @FXML private Label artistNameLabel;
    @FXML private TextField artistName;

    //Album info
    @FXML private Label albumLabel;
    @FXML private TextField album;

    //Genre
    @FXML private Label genreLabel;
    @FXML private TextField genre;

    //Song length
    @FXML private Label songLengthLabel;
    @FXML private TextField hours;
    @FXML private TextField minutes;
    @FXML private TextField seconds;

    //Record label
    @FXML private Label recordLabelLabel;
    @FXML private TextField recordLabel;

    //Year released
    @FXML private Label yearReleasedLabel;
    @FXML private ComboBox yearReleased;

    //update song button
    @FXML private Button updateSongBtn;

    //Song List VBox
    //song list header
    @FXML private Label songListHeader;

    @FXML private Label titleError;
    @FXML private Label artistError;
    @FXML private Label albumError;
    @FXML private Label genreError;
    @FXML private Label lengthError;
    @FXML private Label recordLabelError;

    //User feedback message
    @FXML private Label feedbackLabel;

    public void setSongToEdit(String songToEdit)
    {
        this.songToEdit = songToEdit;
        System.out.println(songToEdit);

        //TODO: Get all values from DB
        try{
            ResultSet songInfo = dbManager.getSong(songToEdit);
            if(songInfo.next()) {
                ResultSet albumInfo = dbManager.getAlbum(songInfo.getString("AlbumName"));
                this.songTitle.setText(songInfo.getString("SongTitle"));
                this.artistName.setText(dbManager.getArtist(songInfo.getInt("ArtistID")));
                this.album.setText(songInfo.getString("AlbumName"));
                this.genre.setText(songInfo.getString("GenreName"));
                this.recordLabel.setText(songInfo.getString("RecordLabelName"));
                if(albumInfo.next()) {
                    this.yearReleased.setValue(albumInfo.getString("Year"));
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    @FXML protected void updateSongBtnClick()
    {
        if (isValidInput())
        {
            Stage stage = (Stage) updateSongBtn.getScene().getWindow();
            // do what you have to do
            songTitleString = this.songTitle.getText();
            artistString = this.artistName.getText();
            genreString = this.genre.getText();
            songLengthString = this.hours.getText() + ":" + this.minutes.getText() + ":" + this.seconds.getText();
            yearString = this.yearReleased.getValue().toString();
            recordLabelString = this.recordLabel.getText();
            albumString = this.album.getText();
            dbManager.editSong(songToEdit, songTitleString, artistString, genreString, songLengthString, yearString, recordLabelString, albumString);
            stage.close();
        }
    }

    private boolean hasNulls()
    {
        boolean hasNulls = false;
        //Check for empty values on columns with NOT NULL constraint
        if (this.songTitle.getText().isEmpty())
        {
            this.titleError.setText("Please enter a title");
            validTitle = false;
            hasNulls = true;
        }
        else
        {
            this.titleError.setText("");
        }
        if (this.artistName.getText().isEmpty())
        {
            this.artistError.setText("Please enter an artist");
            validArtist = false;
            hasNulls = true;
        }
        else
        {
            this.artistError.setText("");
        }
        if (this.recordLabel.getText().isEmpty())
        {
            this.recordLabelError.setText("Please enter a record label");
            validRecordLabel = false;
            hasNulls = true;
        }
        else
        {
            this.recordLabelError.setText("");
        }
        if (this.album.getText().isEmpty())
        {
            this.albumError.setText("Please enter an album");
            validAlbum = false;
            hasNulls = true;
        }
        else
        {
            this.albumError.setText("");
        }
        if (this.genre.getText().isEmpty())
        {
            this.genreError.setText("Please enter an album");
            validGenre = false;
            hasNulls = true;
        }
        else
        {
            this.genreError.setText("");
        }
        return hasNulls;
    }

    private boolean isValidInput()
    {
        boolean valid = true;
        //Check for empty values on columns with NOT NULL constraint
        if (!hasNulls())
        {
            //Check for proper data types
            //songTitle
            if (this.songTitle.getText().length() > 25)
            {
                valid = false;
                validTitle = false;
                this.titleError.setText("Title must be 25 or fewer characters");
            }
            else
            {
                this.titleError.setText("");
            }
            //artistID
            if (this.artistName.getText().length() > 25)
            {
                valid = false;
                validArtist = false;
                this.artistError.setText("Artist name must be 25 or fewer characters");
            }
            else
            {
                this.artistError.setText("");
            }
            //genreName
            if (this.genre.getText().length() > 15)
            {
                valid = false;
                validGenre = false;
                this.genreError.setText("Genre name must be 25 or fewer characters");
            }
            else
            {
                this.genreError.setText("");
            }
            //songLength
            try
            {
                int hoursInt, minutesInt, secondsInt;
                hoursInt = Integer.parseInt(this.hours.getText());
                minutesInt = Integer.parseInt(this.minutes.getText());
                secondsInt = Integer.parseInt(this.seconds.getText());
                if (hoursInt < 0 || minutesInt < 0 || minutesInt > 59 || secondsInt < 0 || secondsInt > 59)
                {
                    valid = false;
                    validLength = false;
                    this.lengthError.setText("Invalid length");
                }
                else
                {
                    this.lengthError.setText("");
                }
            }
            //not a valid integer
            catch (Exception ex)
            {
                valid = false;
                validLength = false;
                this.lengthError.setText("Length values must be integers");
            }

            //recordLabel
            if (this.recordLabel.getText().length() > 25)
            {
                valid = false;
                validRecordLabel = false;
                this.recordLabelError.setText("Record label name must be 25 or fewer characters");
            }
            else
            {
                this.recordLabelError.setText("");
            }
            //album
            if (this.album.getText().length() > 25)
            {
                valid = false;
                this.album.setText("Album name must be 25 or fewer characters");
            }
            else
            {
                this.albumError.setText("");
            }
        }
        //Found nulls
        else
        {
            valid = false;
        }
        return valid;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.dbManager = new DBManager("jdbc:mariadb://localhost:3306/songsproject", "root", "root");
        //Add all years from start to current year to the yearReleased comboBox
        for (int year = Integer.valueOf(Year.now().toString()); year >= 1900 ; year--)
            this.yearReleased.getItems().add(year);
    }
}
