package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugincashregister.cash.CashRegister;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableCamelotEvents;
import org.lebastudios.theroundtable.plugintabledrawing.data.*;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.objects.RoomObjController;
import org.lebastudios.theroundtable.ui.IconButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RoomsPaneController extends PaneController<RoomsPaneController>
{
    private static RoomsPaneController instance;

    @FXML public TabPane roomsTabPane;
    @FXML public IconButton swapModeButton;
    @FXML public ScrollPane instanciateObjPane;
    
    private final List<RoomPaneController> roomPaneControllers = new ArrayList<>();
    private final Consumer<RoomData> onRoomDeleted = this::deleteRoom;
    private final Consumer<RoomData> onRoomCreated = this::loadRoom;
    
    public RoomPaneController activeRoom;
    
    public static RoomsPaneController getInstance()
    {
        if (instance == null) instance = new RoomsPaneController();
        return instance;
    }

    private RoomsPaneController()
    {
        CashRegister.onOrderItemModified.addListener(_ ->
        {
            PluginTableCamelotEvents.getInstance().invokeOnOrderModEvent();
        });
    }
    
    @FXML
    @Override
    protected void initialize()
    {
        loadRooms();

        PluginTableCamelotEvents.getInstance().onRoomDeleted.addWeakListener(onRoomDeleted);
        PluginTableCamelotEvents.getInstance().onRoomCreated.addWeakListener(onRoomCreated);
        
        roomsTabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
        {
            if (newValue == null) return;
            activeRoom = roomPaneControllers.get(roomsTabPane.getTabs().indexOf(newValue));
        });

        roomsTabPane.getSelectionModel().select(0);
        activeRoom = roomPaneControllers.getFirst();
    }

    private void loadRooms()
    {
        var roomSavingDirectory = Rooms.getRoomsFile();
        roomSavingDirectory.mkdirs();

        var roomFiles = roomSavingDirectory.listFiles();

        if (roomFiles == null)
        {
            System.err.println("Error loading rooms created");
            return;
        }

        if (roomFiles.length == 0)
        {
            newRoom(null);
        }
        else
        {
            for (File roomFile : roomFiles)
            {
                loadRoom(Rooms.loadRoom(roomFile.getName()));
            }
        }
    }

    private void loadRoom(RoomData roomData)
    {
        Tab newTab = new Tab(roomData.roomName);
        var newRoomController = new RoomPaneController(roomData, roomsTabPane);

        newTab.setContent(newRoomController.getRoot());
        roomPaneControllers.add(newRoomController);

        Platform.runLater(() ->
        {
            roomsTabPane.getTabs().add(newTab);
            roomsTabPane.getSelectionModel().select(newTab);
        });
        
        roomData.save();
    }

    public void deleteRoom(RoomData roomData)
    {
        roomData.delete();
        
        roomPaneControllers.removeIf(roomController -> roomController.getRoomData().equals(roomData));
        Platform.runLater(() -> roomsTabPane.getTabs().removeIf(tab -> tab.getText().equals(roomData.roomName)));
    }

    @FXML
    public void newRoom(ActionEvent actionEvent)
    {
        new RoomCreationStageController(roomData ->
        {
            PluginTableCamelotEvents.getInstance().onRoomCreated.invoke(roomData);
        }).setOwner(this.getStage() == null
                ? MainStageController.getInstance().getStage()
                : this.getStage()
        ).instantiate(true);
    }

    @FXML
    public void instantiateNewSquareTable(ActionEvent actionEvent)
    {
        activeRoom.instantiateObject(RoomObjData.SQUARE_TABLE);
    }

    @FXML
    public void instantiateNewRoundTable(ActionEvent actionEvent)
    {
        activeRoom.instantiateObject(RoomObjData.ROUND_TABLE);
    }

    @FXML
    public void instantiateNewBarStool(ActionEvent actionEvent)
    {
        activeRoom.instantiateObject(RoomObjData.BAR_STOOL);
    }

    @FXML
    public void instantiateNewBarTable(ActionEvent actionEvent)
    {
        activeRoom.instantiateObject(RoomObjData.BAR_TABLE);
    }

    @FXML
    public void instantiateNewEstablishmentWall(ActionEvent actionEvent)
    {
        activeRoom.instantiateObject(RoomObjData.ESTABLISHMENT_WALL);
    }
    
    @FXML
    public void deleteRoom(ActionEvent actionEvent)
    {
        new ConfirmationTextDialogController(
                LangFileLoader.getTranslation("textblock.deleteroomconfdialog"),
                result ->
                {
                    if (!result) return;

                    PluginTableCamelotEvents.getInstance().onRoomDeleted.invoke(activeRoom.getRoomData());
                }
        ).setOwner(this.getStage()).instantiate();
    }

    @FXML
    public void swapMode(ActionEvent actionEvent)
    {
        RoomObjController.editMode = !RoomObjController.editMode;

        if (RoomObjController.editMode)
        {
            swapModeButton.setIconName("exit.png");
            instanciateObjPane.setVisible(true);
        }
        else
        {
            swapModeButton.setIconName("edit.png");
            instanciateObjPane.setVisible(false);
        }
    }
}
