package org.lebastudios.theroundtableplugins.floorplan.rooms.objects;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.locale.Translator;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomObjData;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomObjectType;
import org.lebastudios.theroundtableplugins.floorplan.rooms.RoomPaneController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.components.IconButton;
import org.lebastudios.theroundtable.components.IconView;

public class RoomObjectController extends PaneController<RoomObjectController>
{
    public static boolean editMode = false;
    @Getter protected final RoomObjData roomObjectData;
    protected final RoomPaneController roomPaneController;
    @FXML public IconButton icon;

    public RoomObjectController(RoomObjData roomObjData, RoomPaneController roomPaneController)
    {
        if (this.getClass().equals(RoomObjectController.class)) 
        {
            if (roomObjData.roomObjectType == RoomObjectType.SQUARE || roomObjData.roomObjectType == RoomObjectType.ROUND ||
                    roomObjData.roomObjectType == RoomObjectType.BAR_STOOL)
            {
                throw new IllegalArgumentException("Invalid Room Object Type");
            }
        }
        
        if (this.getClass().equals(OrderStationController.class))
        {
            if (roomObjData.roomObjectType == RoomObjectType.ESTABLISHMENT_WALL || roomObjData.roomObjectType == RoomObjectType.BAR_TABLE)
            {
                throw new IllegalArgumentException("Invalid Room Object Type");
            }
        }
        
        this.roomObjectData = roomObjData;
        this.roomPaneController = roomPaneController;
    }

    @FXML @Override protected void initialize()
    {
        addEventHandlers();

        root.setLayoutX(roomObjectData.x);
        root.setLayoutY(roomObjectData.y);

        icon.setIconSize((int) switch (roomObjectData.roomObjectType)
        {
            case SQUARE, ROUND -> RoomPaneController.TILE_SIZE * 2;
            case BAR_STOOL, ESTABLISHMENT_WALL, BAR_TABLE -> RoomPaneController.TILE_SIZE;
        });
        
        icon.setRotate(roomObjectData.rotation);
    }

    @FXML
    protected void onClick() {}

    @FXML
    protected void onContextMenuEditting(double x, double y)
    {
        var contextMenu = new ContextMenu();

        // Delete Button
        var menuItem_0 = new MenuItem(Translator.getInstance().t("floor-plan:word.delete"));
        var graphic = new IconView("floor-plan:delete.png");
        graphic.setIconSize(24);
        menuItem_0.setGraphic(graphic);
        menuItem_0.setOnAction(_ -> RoomsPaneController.getInstance().activeRoom.deleteRoomObject(this));

        contextMenu.getItems().addAll(menuItem_0);

        contextMenu.show(root, x, y);
    }

    @FXML
    protected void onContextMenu() {}

    private final double[] offsetX = new double[1];

    private final double[] offsetY = new double[1];
    private void addEventHandlers()
    {
        root.setOnMousePressed(event ->
        {
            if (editMode)
            {
                offsetX[0] = root.getLayoutX() - event.getSceneX() / roomPaneController.getTablesPane().getScaleX();
                offsetY[0] = root.getLayoutY() - event.getSceneY() / roomPaneController.getTablesPane().getScaleY();
                
                root.toFront();
            }
            else
            {
                onClick();
            }
        });

        root.setOnContextMenuRequested(event ->
        {
            if (editMode)
            {
                onContextMenuEditting(event.getScreenX(), event.getScreenY());
            }
            else
            {
                onContextMenu();
            }
        });

        root.setOnMouseDragged(event ->
        {
            if (!editMode) return;

            setPosition(event.getSceneX() / roomPaneController.getTablesPane().getScaleX() + offsetX[0],
                    event.getSceneY() / roomPaneController.getTablesPane().getScaleY() + offsetY[0]);
        });
        
        root.setOnMouseReleased(_ ->
        {
            if (!editMode) return;

            RoomsPaneController.getInstance().activeRoom.onRoomDataUpdated();
        });
    }

    public void setPosition(double x, double y)
    {
        final double xOffset = -getRoot().getLayoutBounds().getWidth() / 2f;
        final double yOffset = -icon.getLayoutBounds().getHeight() / 2f;
        
        x = Math.min(Math.max(x, xOffset), roomPaneController.getTablesPane().getWidth() + xOffset);
        y = Math.min(Math.max(y, yOffset), roomPaneController.getTablesPane().getHeight() + yOffset);
        
        x = Math.round(x / RoomPaneController.TILE_SIZE) * RoomPaneController.TILE_SIZE;
        y = Math.round(y / RoomPaneController.TILE_SIZE) * RoomPaneController.TILE_SIZE;
        
        root.setLayoutX(x);
        root.setLayoutY(y);
    }

    public RoomObjData getInstanceObjData()
    {
        roomObjectData.x = root.getLayoutX();
        roomObjectData.y = root.getLayoutY();
        roomObjectData.rotation = icon.getRotate();
        
        return roomObjectData;
    }

    @Override
    public void loadFXML()
    {
        this.root = new RoomObject$View(this);
        
        String iconName = switch (this.roomObjectData.roomObjectType)
        {
            case ESTABLISHMENT_WALL -> "edit-map-establishment-wall.png";
            case BAR_TABLE -> "edit-map-bar-table.png";
            default -> throw new IllegalStateException("Unexpected value: " + this.roomObjectData.roomObjectType);
        };

        this.icon.setIconName(iconName);
        this.initialize();
    }
}
