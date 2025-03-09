package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import lombok.Getter;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableDrawing;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RoomController extends PaneController<RoomController>
{
    public static final double TILE_SIZE = 24;
    
    private final double roomWidth;
    private final double roomHeight;
    
    private final List<RoomObjController> roomObjects = new ArrayList<>();
    @Getter @FXML private Pane tablesPane;
    @Getter private RoomData roomData;
    private final TabPane parent;

    public RoomController(RoomData roomData, TabPane parent)
    {
        roomData.updateRoomObjIds();
        
        this.roomData = roomData;
        this.parent = parent;
        
        roomWidth = roomData.widthInTiles * TILE_SIZE;
        roomHeight = roomData.heightInTiles * TILE_SIZE;
    }
    
    public void instantiateObject(RoomObjData roomObjData)
    {
        roomObjData.id = roomData.getAndIncrementNextObjId();
        System.out.println(roomObjData.id);
        var table = roomObjData.intoController(this);
        
        tablesPane.getChildren().add(table.getRoot());
        table.getController().setPosition(0, 0);
        roomObjects.add(table.getController());
        
        saveRoom();
    }

    public void saveRoom()
    {
        roomData.roomObjects.clear();

        for (var roomObject : roomObjects)
        {
            if (roomObject instanceof RoomObjController roomObjController)
            {
                roomData.roomObjects.add(roomObjController.getInstanceObjData());
            }
        }
        roomData.save();
    }

    public void deleteRoomObject(RoomObjController roomObjController)
    {
        roomObjects.remove(roomObjController);
        tablesPane.getChildren().remove(roomObjController.getRoot());
        
        saveRoom();
    }

    @FXML @Override protected void initialize()
    {
        loadFromData(roomData);
        
        Image image = ImageLoader.getTexture("establishment-floor-tile.png");
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT, // Repetir en X
                BackgroundRepeat.REPEAT, // Repetir en Y
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        Background background = new Background(backgroundImage);
        tablesPane.setBackground(background);
        
        parent.widthProperty().addListener((_, _, newVal) -> adjustScale(newVal.doubleValue(), parent.getHeight()));
        parent.heightProperty().addListener((_, _, newVal) -> adjustScale(parent.getWidth(), newVal.doubleValue()));
        
        tablesPane.setPrefSize(roomWidth, roomHeight);
        tablesPane.setMaxSize(roomWidth, roomHeight);
        tablesPane.setMinSize(roomWidth, roomHeight);
        
        adjustScale(parent.getWidth(), parent.getHeight());
    }

    public void loadRoom()
    {
        roomData = Rooms.loadRoom(this.roomData.roomName);
        loadFromData(roomData);
        roomData.save();
    }

    private void loadFromData(RoomData roomData)
    {
        tablesPane.getChildren().clear();
        roomObjects.clear();
        
        for (var roomObjectData : roomData.roomObjects)
        {
            var newTable = roomObjectData.intoController(this);

            tablesPane.getChildren().add(newTable.getRoot());
            roomObjects.add(newTable.getController());
        }
    }

    private void adjustScale(double width, double height)
    {
        // Magic number 100 solves the problem of the tables being cut off at the bottom
        double scale = Math.min(width / roomWidth, (height - 100) / roomHeight);

        tablesPane.setScaleX(scale);
        tablesPane.setScaleY(scale);
        
        tablesPane.setTranslateX((parent.getWidth() - roomWidth) / 2);
        tablesPane.setTranslateY((parent.getHeight() - roomHeight) / 2);
    }

    @Override
    public Class<?> getBundleClass()
    {
        return PluginTableDrawing.class;
    }

    @Override
    public URL getFXML()
    {
        return RoomController.class.getResource("roomController.fxml");
    }
}
