package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableDrawing;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;
import org.lebastudios.theroundtable.ui.IconButton;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RoomsPaneController extends PaneController<RoomsPaneController>
{
    private static RoomsPaneController instance;
    private static Node tablesUI;
    private final List<RoomController> roomControllers = new ArrayList<>();
    public RoomController activeRoom;
    @FXML private TabPane roomsTabPane;
    @FXML private IconButton swapModeButton;
    @FXML private ScrollPane instanciateObjPane;
    
    public static RoomsPaneController getInstance()
    {
        if (instance == null) instance = new RoomsPaneController();
        return instance;
    }
    
    private RoomsPaneController() {}
    
    @Override
    public URL getFXML()
    {
        return RoomsPaneController.class.getResource("roomsPaneController.fxml");
    }

    @Override
    public Class<?> getBundleClass()
    {
        return PluginTableDrawing.class;
    }

    @FXML @Override protected void initialize()
    {
        loadRooms();
        
        roomsTabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
        {
            if (newValue == null) return;
            activeRoom = roomControllers.get(roomsTabPane.getTabs().indexOf(newValue));
        });

        roomsTabPane.getSelectionModel().select(0);
        activeRoom = roomControllers.getFirst();
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
            newRoom();
        }
        else
        {
            for (File roomFile : roomFiles)
            {
                loadRoom(Rooms.loadRoom(roomFile.getName()));
            }
        }
    }

    @FXML
    private void newRoom()
    {
        new RoomCreationStageController(this::loadRoom).instantiate(true);
    }

    private void loadRoom(RoomData roomData)
    {
        Tab newTab = new Tab(roomData.roomName);
        var newRoomController = new RoomController(roomData, roomsTabPane);

        newTab.setContent(newRoomController.getRoot());
        roomControllers.add(newRoomController);
        roomsTabPane.getTabs().add(newTab);
        
        roomsTabPane.getSelectionModel().select(newTab);
        roomData.save();
    }

    @FXML
    private void instantiateNewSquareTable()
    {
        activeRoom.instantiateObject(RoomObjData.SQUARE_TABLE);
    }

    @FXML
    private void instantiateNewRoundTable()
    {
        activeRoom.instantiateObject(RoomObjData.ROUND_TABLE);
    }

    @FXML
    private void instantiateNewBarStool()
    {
        activeRoom.instantiateObject(RoomObjData.BAR_STOOL);
    }

    @FXML
    private void instantiateNewBarTable()
    {
        activeRoom.instantiateObject(RoomObjData.BAR_TABLE);
    }

    @FXML
    private void instantiateNewEstablishmentWall()
    {
        activeRoom.instantiateObject(RoomObjData.ESTABLISHMENT_WALL);
    }

    public void unloadRoom(RoomData roomData)
    {
        roomControllers.removeIf(roomController -> roomController.getRoomData().equals(roomData));
        roomsTabPane.getTabs().removeIf(tab -> tab.getText().equals(roomData.roomName));
    }

    @FXML
    private void saveRoom(ActionEvent actionEvent)
    {
        activeRoom.saveRoom();
    }

    @FXML
    private void deleteRoom(ActionEvent actionEvent)
    {
        new ConfirmationTextDialogController(
                LangFileLoader.getTranslation("textblock.deleteroomconfdialog"),
                result ->
                {
                    if (!result) return;

                    var roomName = activeRoom.getRoomData();
                    
                    unloadRoom(roomName);
                    Rooms.deleteRoom(roomName);
                }
        ).instantiate();
    }

    @FXML
    private void reloadRoom(ActionEvent actionEvent)
    {
        activeRoom.loadRoom();
    }

    @FXML
    private void swapMode(ActionEvent actionEvent)
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
