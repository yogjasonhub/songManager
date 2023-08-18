package com.songmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class BrowseSongsController implements Initializable
{
    private DBManager dbManager;
    //Lists of criteria to filter by
    private ArrayList<String> artistList;
    private ArrayList<String> genreList;
    private ArrayList<String> recordLabelList;
    private ArrayList<String> albumList;
    //For UI, don't even remember how I made this work, but it's like magic.
    private HashMap<String, VBox> criteriaVBoxMap;

    @FXML TableView<ModelTable> songTable;

    @FXML TableColumn<ModelTable, String> titleCol;
    @FXML TableColumn<ModelTable, String> artistCol;
    @FXML TableColumn<ModelTable, String> genreCol;
    @FXML TableColumn<ModelTable, String> recordLabelCol;
    @FXML TableColumn<ModelTable, String> albumCol;
    @FXML TableColumn<ModelTable, String> lengthCol;
    @FXML TableColumn<ModelTable, String> yearCol;

    @FXML ComboBox genreComboBox;
    @FXML ComboBox artistComboBox;
    @FXML ComboBox albumComboBox;
    @FXML ComboBox recordLabelComboBox;
    @FXML ComboBox criteriaComboBox;

    @FXML Button filterByArtistBtn;
    @FXML Button filterByGenreBtn;
    @FXML Button filterByYearBtn;
    @FXML Label errorMessage;

    @FXML VBox criteriaBox;

    @FXML VBox artistBox;
    @FXML VBox albumBox;
    @FXML VBox genreBox;
    @FXML VBox yearBox;
    @FXML VBox recordLabelBox;
    @FXML VBox lengthOfSongBox;

    @FXML ComboBox startYear;
    @FXML ComboBox endYear;

    @FXML Button selectCriteriaBtn;

    @FXML Label songLengthMinLabel;
    @FXML Label songLengthMaxLabel;

    @FXML TextField secondsMin;
    @FXML TextField minutesMin;
    @FXML TextField hoursMin;

    @FXML TextField secondsMax;
    @FXML TextField minutesMax;
    @FXML TextField hoursMax;

    @FXML Button filterByLengthBtn;

    @FXML HBox buttonsBox;

    ObservableList<ModelTable> objectList = FXCollections.observableArrayList();

    /**
     * Default constructor
     */
    public BrowseSongsController()
    {
        this.dbManager = new DBManager("jdbc:mariadb://localhost:3306/songsproject", "root", "root");
        getCriteriaData();

        //For UI
        this.criteriaVBoxMap = new HashMap<String, VBox>();
    }

    /**
     * Fills combo boxes with criteria data
     */
    public void populateComboBoxes()
    {
        clearComboBoxes();
        for (String artist : this.artistList)
            artistComboBox.getItems().add(artist);

        for (String album : this.albumList)
            albumComboBox.getItems().add(album);

        for (String genre : this.genreList)
            genreComboBox.getItems().add(genre);

        for (String recordLabel : this.recordLabelList)
            recordLabelComboBox.getItems().add(recordLabel);

        //Add all years from start to current year to the yearReleased comboBox
        for (int year = Integer.valueOf(Year.now().toString()); year >= 1900 ; year--)
        {
            this.startYear.getItems().add(year);
            this.endYear.getItems().add(year);
        }

        this.criteriaComboBox.getItems().add("Artist");
        this.criteriaComboBox.getItems().add("Album");
        this.criteriaComboBox.getItems().add("Genre");
        this.criteriaComboBox.getItems().add("Record Label");

        this.criteriaComboBox.getItems().add("Year");
        this.criteriaComboBox.getItems().add("Length of Song");
    }

    /**
     * Removes items from all criteria combo boxes
     */
    private void clearComboBoxes()
    {
        this.criteriaComboBox.getItems().clear();
        this.artistComboBox.getItems().clear();
        this.albumComboBox.getItems().clear();
        this.genreComboBox.getItems().clear();
        this.recordLabelComboBox.getItems().clear();
    }

    /**
     * Loads criteria data
     */
    private void getCriteriaData()
    {
        this.artistList = new ArrayList<String>();
        this.albumList = new ArrayList<String>();
        this.genreList = new ArrayList<String>();
        this.recordLabelList = new ArrayList<String>();

        //Get data from database
        ResultSet resultSet = dbManager.getAllArtistNames();
        //Get all artists
        try
        {
            while (resultSet.next())
            {
                String artist = resultSet.getString("ArtistName");
                this.artistList.add(artist);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        resultSet = dbManager.getAllAlbumNames();
        //Get all albums
        try
        {
            while (resultSet.next())
            {
                String album = resultSet.getString("AlbumName");
                this.albumList.add(album);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        resultSet = dbManager.getAllGenreNames();
        //Get all albums
        try
        {
            while (resultSet.next())
            {
                String genre = resultSet.getString("GenreName");
                this.genreList.add(genre);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        resultSet = dbManager.getAllRecordLabelNames();
        //Get all albums
        try
        {
            while (resultSet.next())
            {
                String recordLabel = resultSet.getString("RecordLabelName");
                this.recordLabelList.add(recordLabel);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Initializes JavaFX components
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //Remove all FXML criteria VBoxes and add to list
        int count = criteriaBox.getChildren().size();
        for (Node box : criteriaBox.getChildren())
        {
            System.out.println(((VBox)(box)).getId());
            criteriaVBoxMap.put( ((VBox)(box)).getId(), (VBox)(box));
        }
        for (int i =criteriaBox.getChildren().size() - 1; i>= 0; i--)
        {
            criteriaBox.getChildren().remove(i);
        }
        this.songTable.setPrefWidth(500);
        populateComboBoxes();

        this.titleCol.setCellValueFactory(new PropertyValueFactory("title"));
        this.artistCol.setCellValueFactory(new PropertyValueFactory("artist"));
        this.genreCol.setCellValueFactory(new PropertyValueFactory("genre"));
        this.recordLabelCol.setCellValueFactory(new PropertyValueFactory("recordLabel"));
        this.albumCol.setCellValueFactory(new PropertyValueFactory("album"));
        this.lengthCol.setCellValueFactory(new PropertyValueFactory("length"));
        this.yearCol.setCellValueFactory(new PropertyValueFactory("year"));
        LoadAllTableData();

        this.buttonsBox.setSpacing(10);
    }

    /**
     * Populates the songTable
     */
    private void setTable(ResultSet resultSet)
    {
        try
        {
            this.songTable.getItems().clear();
            while (resultSet.next())
            {
                objectList.add(new ModelTable(resultSet.getString("SongTitle"), resultSet.getString("ArtistName"),
                        resultSet.getString("GenreName"), resultSet.getString("RecordLabelName"),
                        resultSet.getString("AlbumName"), resultSet.getString("Length"),
                        resultSet.getString("Year")));
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        songTable.setItems(objectList);
    }

    /**
     * Attempts to select a criteria to filter by
     */
    @FXML protected void selectCriteriaBtnClick()
    {
        if (criteriaComboBox.getValue() != null)
        {
            if (criteriaComboBox.getValue().toString() == "Artist")
            {
                if (criteriaBox.getChildren().size() != 0)
                {
                    criteriaBox.getChildren().remove(0);
                }
                criteriaBox.getChildren().add(criteriaVBoxMap.get("artistBox"));
            }
            if (criteriaComboBox.getValue().toString() == "Album")
            {
                if (criteriaBox.getChildren().size() != 0)
                {
                    criteriaBox.getChildren().remove(0);
                }
                criteriaBox.getChildren().add(criteriaVBoxMap.get("albumBox"));
            }
            else if (criteriaComboBox.getValue().toString() == "Genre")
            {
                if (criteriaBox.getChildren().size() != 0)
                {
                    criteriaBox.getChildren().remove(0);
                }
                criteriaBox.getChildren().add(criteriaVBoxMap.get("genreBox"));
            }
            else if (criteriaComboBox.getValue().toString() == "Year")
            {
                if (criteriaBox.getChildren().size() != 0)
                {
                    criteriaBox.getChildren().remove(0);
                }
                criteriaBox.getChildren().add(criteriaVBoxMap.get("yearBox"));
            }
            else if (criteriaComboBox.getValue().toString() == "Record Label")
            {
                if (criteriaBox.getChildren().size() != 0)
                {
                    criteriaBox.getChildren().remove(0);
                }
                criteriaBox.getChildren().add(criteriaVBoxMap.get("recordLabelBox"));
            }
            else if (criteriaComboBox.getValue().toString() == "Length of Song")
            {
                if (criteriaBox.getChildren().size() != 0)
                {
                    criteriaBox.getChildren().remove(0);
                }
                criteriaBox.getChildren().add(criteriaVBoxMap.get("lengthOfSongBox"));
            }
            errorMessage.setText("");
        }
        else
        {
            errorMessage.setText("Select a filter criteria!");
        }
    }

    /**
     * Attempts to filter by artist
     */
    @FXML protected void filterByArtistBtnClick()
    {
       if (this.artistComboBox.getValue() != null)
        {
            String artist = artistComboBox.getValue().toString();
            ResultSet resultSet = dbManager.getSongByArtist(artist);
            setTable(resultSet);
            errorMessage.setText("");
        }
       else
       {
           errorMessage.setText("Select an artist!");
       }
    }

    /**
     * Attempts to filter by album
     */
    @FXML protected void filterByAlbumBtnClick()
    {
        if (this.albumComboBox.getValue() != null)
        {
            String album = albumComboBox.getValue().toString();
            ResultSet resultSet = dbManager.getSongByAlbum(album);
            setTable(resultSet);
            errorMessage.setText("");
        }
        else
        {
            errorMessage.setText("Select an album!");
        }
    }

    /**
     * Attempts to filter by genre
     */
    @FXML protected void filterByGenreBtnClick()
    {
        if (this.genreComboBox.getValue() != null)
        {
            String genre = genreComboBox.getValue().toString();
            ResultSet resultSet = dbManager.getSongByGenre(genre);
            setTable(resultSet);
            errorMessage.setText("");
        }
        else
        {
            errorMessage.setText("Select a genre!");
        }
    }

    /**
     * Attempts to filter by record label
     */
    @FXML protected void filterByRecordLabelBtnClick()
    {
        if (this.recordLabelComboBox.getValue() != null) {
            String recordLabel = recordLabelComboBox.getValue().toString();
            ResultSet resultSet = dbManager.getSongByRecordLabel(recordLabel);
            setTable(resultSet);
            errorMessage.setText("");
        }
        else
        {
            errorMessage.setText("Select an record label!");
        }
    }

    /**
     * Attempts to filter by song length
     */
    @FXML protected void filterByLengthBtnClick()
    {
        if (this.hoursMin.getText() != null && this.minutesMin.getText() != null && this.secondsMin.getText() != null &&
                this.hoursMax.getText() != null && this.minutesMax.getText() != null && this.secondsMax.getText() != null)
        {
            String minHours, minMinutes, minSeconds, maxHours, maxMinutes, maxSeconds, minLength, maxLength;

            minHours = hoursMin.getText();
            minMinutes = minutesMin.getText();
            minSeconds = secondsMin.getText();

            maxHours = hoursMax.getText();
            maxMinutes = minutesMax.getText();
            maxSeconds = secondsMax.getText();
            try
            {
                if (validTimes(minHours, minMinutes, minSeconds) && validTimes(maxHours, maxMinutes, maxSeconds))
                {
                    minLength = getTimeAsFormattedString(minHours, minMinutes, minSeconds);
                    maxLength = getTimeAsFormattedString(maxHours, maxMinutes, maxSeconds);
                    ResultSet resultSet = dbManager.getSongByLength(minLength, maxLength);
                    setTable(resultSet);
                    errorMessage.setText("");
                }
                else
                {
                    errorMessage.setText("Enter a valid time!");
                }
            }
            catch(Exception ex)
            {
                errorMessage.setText("Enter a valid time!");
            }
        }
    }

    /**
     * Attempts to filter by year
     */
    @FXML protected void filterByYearBtnClick()
    {
        //No nulls
        if (this.startYear.getValue() != null && this.endYear.getValue() != null)
        {
            //Start year <= end year
            if ((Integer.parseInt(this.startYear.getValue().toString()))
                    <= (Integer.parseInt(this.endYear.getValue().toString())))
            {
                //Get values as strings
                String startYear, endYear;
                startYear = this.startYear.getValue().toString();
                endYear = this.endYear.getValue().toString();

                ResultSet resultSet = dbManager.getSongByYear(startYear, endYear);
                setTable(resultSet);

                errorMessage.setText("");
            }
            else
            {
                errorMessage.setText("Start year must be before end year!");
            }
        }
        else
        {
            errorMessage.setText("Select a year!");
        }
    }

    /**
     * Attempts to add a song
     */
    @FXML protected void AddSongBtnClick(ActionEvent event)
    {
        try
        {
            openAddSongView(event);
            errorMessage.setText("");
            LoadAllTableData();
            getCriteriaData();
            populateComboBoxes();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Attempts to add remove a song
     */
    @FXML protected void RemoveSongBtnClick()
    {
        if (songTable.getSelectionModel().getSelectedItem() != null)
        {
            String songToRemove = songTable.getSelectionModel().getSelectedItem().getTitle();
            dbManager.deleteSong(songToRemove);
            this.errorMessage.setText("");
            LoadAllTableData();
            getCriteriaData();
            populateComboBoxes();
        }
        else
        {
            this.errorMessage.setText("Please pick a song to remove");
        }

    }

    /**
     * Attempts to edit a song
     */
    @FXML protected void EditSongBtnClick(ActionEvent event)
    {
        if (songTable.getSelectionModel().getSelectedItem() != null)
        {
            String songToEdit = songTable.getSelectionModel().getSelectedItem().getTitle();
            try
            {
                openEditSongView(event, songToEdit);
                this.errorMessage.setText("");
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            this.errorMessage.setText("Please pick a song to edit");
        }
    }

    /**
     * Opens popup to add a song
     */
    public void openAddSongView(ActionEvent event) throws IOException
    {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddSongsView.fxml"));

        Parent root = (Parent)loader.load();

        AddSongsController controller = loader.<AddSongsController>getController();

        Stage stage = new Stage();
        Scene scene = new Scene(root,700,700);

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();

        //Update table upon closing popup
        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                LoadAllTableData();
                getCriteriaData();
                populateComboBoxes();
            }
        });
    }

    /**
     * Opens popup to edit a song
     */
    public void openEditSongView(ActionEvent event, String songToEdit) throws IOException
    {
        //String songToEdit = editSongList.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditSongsView.fxml"));

        Parent root = (Parent)loader.load();

        EditSongsController controller = loader.<EditSongsController>getController();
        controller.setSongToEdit(songToEdit);

        Stage stage = new Stage();
        Scene scene = new Scene(root,700,700);

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();

        //Update table upon closing popup
        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                LoadAllTableData();
                getCriteriaData();
                populateComboBoxes();
            }
        });
    }

    /**
     * Updates the Table on the page to correctly display the songs available
     */
    private void LoadAllTableData()
    {
        ResultSet resultSet = dbManager.getSongs();
        try {
            songTable.getItems().clear();
            setTable(resultSet);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Updates the Table on the page to correctly display the songs available,
     * used to return to previous filtered view of table
     */
    private void LoadSpecificTableData(ResultSet resultSet) {
        try {
            songTable.getItems().clear();
            setTable(resultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Checks that times inputted are valid
     */
    public boolean validTimes(String hours, String minutes, String seconds) throws Exception
    {
        if (Integer.parseInt(hours) < 0 || Integer.parseInt(minutes) < 0 ||
                Integer.parseInt(minutes) > 59 || Integer.parseInt(seconds) < 0
                || Integer.parseInt(seconds) > 59)
        {
            return false;
        }
        return true;
    }

    /**
     * Converts hour, minute, second time strings to a single string that can be used for an SQL command
     */
    public String getTimeAsFormattedString(String hours, String minutes, String seconds)
    {
        return hours + ":" + minutes + ":" + seconds;
    }

}
