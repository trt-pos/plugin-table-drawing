package org.lebastudios.theroundtable.plugintabledrawing.rooms.objects;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjectType;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomPaneController;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.ui.IconButton;
import org.lebastudios.theroundtable.ui.IconView;

import java.net.URL;

public class RoomObjController extends PaneController<RoomObjController>
{
    public static boolean editMode = false;
    @Getter protected final RoomObjData roomObjectData;
    protected final RoomPaneController roomPaneController;
    @FXML public Node root;
    @FXML public IconButton icon;

    public RoomObjController(RoomObjData roomObjData, RoomPaneController roomPaneController)
    {
        if (this.getClass().equals(RoomObjController.class)) 
        {
            if (roomObjData.roomObjectType == RoomObjectType.SQUARE || roomObjData.roomObjectType == RoomObjectType.ROUND ||
                    roomObjData.roomObjectType == RoomObjectType.BAR_STOOL)
            {
                throw new IllegalArgumentException("Invalid Room Object Type");
            }
        }
        
        if (this.getClass().equals(TableObjectController.class))
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
        var menuItem_0 = new MenuItem(LangFileLoader.getTranslation("word.delete"));
        var graphic = new IconView("delete.png");
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
    public final URL getFXML()
    {
        var fxmlObject = switch (this.roomObjectData.roomObjectType)
        {
            case SQUARE -> "squareTableObject.fxml";
            case ROUND -> "roundTableObject.fxml";
            case BAR_STOOL -> "barStoolObject.fxml";
            case ESTABLISHMENT_WALL -> "establishmentWallObject.fxml";
            case BAR_TABLE -> "barTableObject.fxml";
        };

        return RoomObjController.class.getResource(fxmlObject);
    }
}
