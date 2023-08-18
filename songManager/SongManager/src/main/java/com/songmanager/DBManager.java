package com.songmanager;

import java.sql.*;

public class DBManager
{
    private Connection connection;

    public DBManager(String url, String user, String pwd)
    {
        try
        {
            connection = DriverManager.getConnection(url, user, pwd);
        }
        catch(Exception ex)
        {
            System.out.println("Connection failed");
        }
    }
    //Creates a DB connection
   /* public static Connection getConnection()
    {
        Connection connection = null;
        String url = "jdbc:mariadb://localhost:3306/songsproject";
        String user = "root";
        String pwd = "root";

        try
        {
            connection = DriverManager.getConnection(url, user, pwd);
        }
        catch (Exception ex)
        {
            System.out.println("Connection failed");
        }
        System.out.println("Connection successful");
        return connection;
    }*/


    private boolean find(String table, String toFind)
    {
        boolean found = false;
        String tableKeyWord = "";
        ResultSet resultSet = null;
        switch(table) {
            case "Genre":
                tableKeyWord = "GenreName";
                break;
            case "Album":
                tableKeyWord = "AlbumName";
                break;
            case "Artist":
                tableKeyWord = "ArtistName";
                break;
            case "RecordLabel":
                tableKeyWord = "RecordLabelName";
                break;
            case "Song":
                tableKeyWord = "SongTitle";
                break;
        }
        try {
            String query = " SELECT " + tableKeyWord + " FROM " + table + " WHERE "+ tableKeyWord + " LIKE ?";
            System.out.println("Reading data..");
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, toFind);
                resultSet = statement.executeQuery();
            }
            if(resultSet.next())
            {
                found = true;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return found;
    }
    private int find( String toFind)
    {
        int found = 0;
        String tableKeyWord = "ArtistName";
        String table = "Artist";
        ResultSet resultSet = null;
        try {
            String query = " SELECT ArtistID, " + tableKeyWord + " FROM " + table + " WHERE "+ tableKeyWord + " LIKE ?";
            System.out.println("Reading data..");
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, toFind);
                resultSet = statement.executeQuery();
            }
            if(resultSet.next())
            {
                found = resultSet.getInt(1);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return found;
    }

    public String getArtist(int id)
    {
        String artist = "";
        String tableKeyWord = "ArtistName";
        String table = "Artist";
        ResultSet resultSet = null;
        try {
            String query = " SELECT " + tableKeyWord + " FROM " + table + " WHERE "+ "ArtistID" + " LIKE ?";
            System.out.println("Reading data..");
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, id);
                resultSet = statement.executeQuery();
            }
            if(resultSet.next())
            {
                artist = resultSet.getString(1);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return artist;
    }

    public ResultSet getAlbum(String album)
    {
        String artist = "";
        String tableKeyWord = "AlbumName, ArtistID, Year";
        String table = "Album";
        ResultSet resultSet = null;
        try {
            String query = " SELECT " + tableKeyWord + " FROM " + table + " WHERE "+ "AlbumName" + " LIKE ?";
            System.out.println("Reading data..");
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, album);
                resultSet = statement.executeQuery();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return resultSet;
    }

//
//    //Creates a table in the DB
//    public void createTable() throws Exception {
//        try
//        {
//            PreparedStatement create = connection.prepareStatement("CREATE OR REPLACE TABLE songsproject.song(id int NOT NULL, SongTitle varchar(25), Artist char(25), Genre char(15), LengthOfSong varchar(4), PublishedYear varchar(4), Label varchar(20), Album varchar(20), PRIMARY KEY(SongTitle));");
//            create.executeUpdate();
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }
//        finally {System.out.println("createTable function completed...");};
//    }

    //Adds a song to the db
    public void addSong(String title, String artistName,  String genre, String songLength, String year, String recordLabel, String album)
    {
        /*Check artist first

        Make following SQL call
            SELECT id FROM artist WHERE Name = artist(The string parameter)

        if result is NULL/Empty, make this SQL Call
            INSERT INTO artist VALUES(artist, recordLabel) //I assume that we can get IDs to autoincrement and not have to actually pass a value in,
                                                           //also, probably want to check if record label exists, but not showing for simplicity

        else
            INSERT INTO song(title, id(which we retrieved from DB), genre, songLength, year, recordLabel, album)

        Will have to make similar checks for other foreign keys, but the idea should still work


         */
        if(!find("Genre", genre))
        {
            addGenre(genre);
        }
        if(!find("RecordLabel", recordLabel))
        {
            addRecordLabel(recordLabel);
        }
        if(!find("Artist", artistName))
        {
            addArtist(artistName, recordLabel);
        }
        if(!find("Album", album))
        {
            addAlbum(album, artistName, year);
        }
        int artistID = 0;
        artistID = find(artistName);
        try
        {
            int rowsInserted;
            System.out.println("Creating Song...");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO song(SongTitle, ArtistID, GenreName,  RecordLabelName, AlbumName, Length)
                    VALUES (?,?,?,?,?,?)""")) {
                statement.setString(1, title);
                statement.setInt(2, artistID);
                statement.setString(3, genre);
                statement.setString(4, recordLabel);
                //statement.setString(5, year);
                statement.setString(5, album);
                statement.setString(6, songLength);

                rowsInserted = statement.executeUpdate();
            }
            System.out.println("Rows inserted: " + rowsInserted);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }

    private void addAlbum(String album, String artistName, String year) {
        try
        {
            int rowsInserted;
            int artistID = find(artistName);
            System.out.println("Creating Album...");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO album(AlbumName, ArtistID, Year)
                    VALUES (?,?,?)""")) {
                statement.setString(1, album);
                statement.setInt(2, artistID);
                statement.setString(3, year);

                rowsInserted = statement.executeUpdate();
            }
            System.out.println("Rows inserted: " + rowsInserted);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void addArtist(String artistName, String recordLabel) {
        try
        {
            int rowsInserted;
            System.out.println("Creating Artist...");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO artist(ArtistID, ArtistName, RecordLabelName)
                    VALUES (?,?,?)""")) {
                statement.setNull(1, 1);
                statement.setString(2, artistName);
                statement.setString(3, recordLabel);

                rowsInserted = statement.executeUpdate();
            }
            System.out.println("Rows inserted: " + rowsInserted);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void addRecordLabel(String recordLabel) {
        try
        {
            int rowsInserted;
            System.out.println("Creating Label...");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO recordlabel(RecordLabelName)
                    VALUES (?)""")) {
                statement.setString(1, recordLabel);

                rowsInserted = statement.executeUpdate();
            }
            System.out.println("Rows inserted: " + rowsInserted);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void addGenre(String genre) {
        try
        {
            int rowsInserted;
            System.out.println("Creating Genre...");
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO genre(GenreName)
                    VALUES (?)""")) {
                statement.setString(1, genre);


                rowsInserted = statement.executeUpdate();
            }
            System.out.println("Rows inserted: " + rowsInserted);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    //TODO: edit song actually edits the album / artist if needed instead of adding a new artist and album
    public void editSong(String oldTitle, String title, String artist,  String genre, String songLength, String year, String recordLabel, String album)
    {
        if(!find("Genre", genre))
        {
            addGenre(genre);
        }
        if(!find("RecordLabel", recordLabel))
        {
            addRecordLabel(recordLabel);
        }
        if(!find("Artist", artist))
        {
            addArtist(artist, recordLabel);
        }
        if(!find("Album", album))
        {
            addAlbum(album, artist, year);
        }
        try
        {
            int rowsUpdated;
            int artistID = find(artist);
            System.out.println("Editing data...");
            try (PreparedStatement statement = connection.prepareStatement("""
                    UPDATE song
                    SET SongTitle = ?, ArtistID = ?, GenreName = ?,  RecordLabelName = ?, AlbumName = ?, Length = ?
                    WHERE SongTitle = ?""")) {
                statement.setString(1, title);
                statement.setInt(2, artistID);
                statement.setString(3, genre);
                statement.setString(4, recordLabel);
                statement.setString(5, album);
                statement.setString(6, songLength);
                statement.setString(7, oldTitle);

                rowsUpdated = statement.executeUpdate();
            }
            System.out.println("Rows updated: " + rowsUpdated);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public ResultSet getSongs()
    {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try(PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, Song.Length, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName AND\s
                    Song.ArtistID = Artist.ArtistID ;""")){
                resultSet = statement.executeQuery();
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getSong(String songName)
    {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try(PreparedStatement statement = connection.prepareStatement("""
                    SELECT SongTitle, ArtistID, GenreName, RecordLabelName, AlbumName, Length
                    FROM song
                    WHERE SongTitle LIKE ?""")){
                statement.setString(1, songName);
                resultSet = statement.executeQuery();
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return resultSet;
    }

    public void deleteSong(String songName)
    {
        try {
            System.out.println("Deleting data...");
            int rowsDeleted;
            try (PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM song
                    WHERE songTitle LIKE ?
                    """)) {
                statement.setString(1, songName);
                rowsDeleted = statement.executeUpdate();
                System.out.println("Rows deleted: " + rowsDeleted);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
    /*
    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName
FROM Song, Album, Artist
WHERE Album.AlbumName = Song.AlbumName AND
Song.ArtistID = Artist.ArtistID ;
     */
    //Gets a song view by the artist
    public ResultSet getSongByArtist(String artistName) {
        ResultSet resultSet = null;
        try {

            int artistID = find(artistName);
            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, 
                    Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName\s
                    AND Song.ArtistID = Artist.ArtistID\s
                    AND Song.ArtistID = ?
                    ORDER BY Song.SongTitle""")) {
                statement.setInt(1, artistID);
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getSongByAlbum(String album) {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, 
                    Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName\s
                    AND Song.ArtistID = Artist.ArtistID\s
                    AND Song.AlbumName LIKE ?
                    ORDER BY Song.SongTitle""")) {
                statement.setString(1, album);
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getSongByGenre(String genre) {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, 
                    Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName\s
                    AND Song.ArtistID = Artist.ArtistID\s
                    AND Song.GenreName LIKE ?
                    ORDER BY Song.SongTitle""")) {
                statement.setString(1, genre);
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }
    public ResultSet getSongByRecordLabel(String label) {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, 
                    Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName\s
                    AND Song.ArtistID = Artist.ArtistID\s
                    AND Song.RecordLabelName LIKE ?
                    ORDER BY Song.SongTitle""")) {
                statement.setString(1, label);
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getSongByLength(String minLength, String maxLength) {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, 
                    Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName\s
                    AND Song.ArtistID = Artist.ArtistID\s
                    AND Song.Length >= ? AND Song.Length <= ?
                    ORDER BY Song.Length""")) {
                statement.setString(1, minLength);
                statement.setString(2, maxLength);
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getSongByYear(String year1, String year2) {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT Song.SongTitle, Song.ArtistID, Song.GenreName, Song.AlbumName, Song.RecordLabelName, 
                    Song.AlbumName, Song.LENGTH, Album.Year , Artist.ArtistName\s
                    FROM Song, Album, Artist
                    WHERE Album.AlbumName = Song.AlbumName\s
                    AND Song.ArtistID = Artist.ArtistID\s
                    AND Album.Year >= ? AND Album.Year <= ?
                    ORDER BY Album.Year;""")) {
                statement.setString(1, year1);
                statement.setString(2, year2);
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getAllArtistNames() {
        ResultSet resultSet = null;
        try {
            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT ArtistName
                    FROM Artist
                    ORDER BY ArtistName
                    """)) {
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getAllAlbumNames() {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT AlbumName
                    FROM Album
                    ORDER BY AlbumName
                    """)) {
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getAllRecordLabelNames() {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT RecordLabelName
                    FROM RecordLabel
                    ORDER BY RecordLabelName
                    """)) {
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public ResultSet getAllGenreNames() {
        ResultSet resultSet = null;
        try {

            System.out.println("Reading data..");
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT GenreName
                    FROM Genre
                    ORDER BY GenreName
                    """)) {
                resultSet = statement.executeQuery();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return resultSet;
    }


}
