package org.lebastudios.theroundtableplugins.floorplan.rooms;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import lombok.Getter;
import org.lebastudios.theroundtable.apparience.ImageManager;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtableplugins.floorplan.PluginTableCamelotEvents;
import org.lebastudios.theroundtableplugins.floorplan.data.OrderModData;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomData;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomObjData;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.RoomObjectController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.OrderStationController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RoomPaneController extends PaneController<RoomPaneController>
{
    public static final double TILE_SIZE = 24;
    
    private final double roomWidth;
    private final double roomHeight;
    
    private final List<RoomObjectController> roomObjects = new ArrayList<>();
    @Getter private RoomData roomData;
    private final TabPane parent;
    
    @Getter private Pane tablesPane;

    private final Consumer<RoomData> onRoomChanged = roomData -> 
    {
        if (!roomData.equals(this.roomData)) return;
        
        this.roomData = roomData;
        Platform.runLater(() -> loadFromData(roomData));
    };
    
    private final Consumer<OrderModData> onOrderMod = orderModData ->
    {
        if (!orderModData.roomName.equals(this.roomData.roomName)) return;
        
        for (var roomObject : roomObjects)
        {
            if (roomObject.getRoomObjectData().id == orderModData.tableId) 
            {
                OrderStationController orderStationController = (OrderStationController) roomObject; 
                orderStationController.setOrder(orderModData.newOrder.intoOrder(
                        orderStationController.getTableNameLabel().getText()));
                orderStationController.updateOrderDecoration();
                updateRoomData();
                roomData.save();
                
                break;
            }
        }
    };     
    
    public RoomPaneController(RoomData roomData, TabPane parent)
    {
        PluginTableCamelotEvents.getInstance().onRoomChanged.addWeakListener(onRoomChanged);
        PluginTableCamelotEvents.getInstance().onOrderMod.addWeakListener(onOrderMod);
        
        roomData.updateRoomObjIds();
        
        this.roomData = roomData;
        this.parent = parent;
        
        roomWidth = roomData.widthInTiles * TILE_SIZE;
        roomHeight = roomData.heightInTiles * TILE_SIZE;
    }

    @FXML @Override protected void initialize()
    {
        tablesPane = (Pane) getRoot(); 
        
        loadFromData(roomData);

        Image image = ImageManager.getInstance().get("floor-plan:establishment-floor-tile.png", ImageManager.ImageType.TEXTURE);
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
    
    public void instantiateObject(RoomObjData roomObjData)
    {
        roomObjData.id = roomData.getAndIncrementNextObjId();
        var table = roomObjData.intoController(this);
        
        tablesPane.getChildren().add(table.getRoot());
        table.getController().setPosition(0, 0);
        roomObjects.add(table.getController());
        
        onRoomDataUpdated();
    }

    public void deleteRoomObject(RoomObjectController roomObjectController)
    {
        roomObjects.remove(roomObjectController);
        tablesPane.getChildren().remove(roomObjectController.getRoot());
        
        onRoomDataUpdated();
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
    
    public void onRoomDataUpdated()
    {
        updateRoomData();

        PluginTableCamelotEvents.getInstance().onRoomChanged.invoke(roomData);
    }

    private void updateRoomData()
    {
        roomData.roomObjects.clear();

        for (var roomObject : roomObjects)
        {
            if (roomObject instanceof RoomObjectController roomObjectController)
            {
                roomData.roomObjects.add(roomObjectController.getInstanceObjData());
            }
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
}
