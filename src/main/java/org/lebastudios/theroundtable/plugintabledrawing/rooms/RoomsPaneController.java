package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugincashregister.cash.CashRegister;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.objects.RoomObjController;
import org.lebastudios.theroundtable.ui.IconButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoomsPaneController extends PaneController<RoomsPaneController>
{
    private static RoomsPaneController instance;
    private static Node tablesUI;
    private final List<RoomPaneController> roomPaneControllers = new ArrayList<>();
    public RoomPaneController activeRoom;
    @FXML public TabPane roomsTabPane;
    @FXML public IconButton swapModeButton;
    @FXML public ScrollPane instanciateObjPane;

    public static RoomsPaneController getInstance()
    {
        if (instance == null) instance = new RoomsPaneController();
        return instance;
    }

    private RoomsPaneController()
    {
        CashRegister.onOrderItemModified.addListener(_ ->
        {
            CashRegister cashRegister = CashRegister.getInstance();

            if (cashRegister.getActualOrder() == cashRegister.getCashRegisterOrder()) return;

            activeRoom.saveRoom();
        });
    }

    @FXML
    @Override
    protected void initialize()
    {
        loadRooms();

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

    @FXML
    public void newRoom(ActionEvent actionEvent)
    {
        new RoomCreationStageController(this::loadRoom)
                .setOwner(this.getStage() == null
                        ? MainStageController.getInstance().getStage()
                        : this.getStage()
                ).instantiate(true);
    }

    private void loadRoom(RoomData roomData)
    {
        Tab newTab = new Tab(roomData.roomName);
        var newRoomController = new RoomPaneController(roomData, roomsTabPane);

        newTab.setContent(newRoomController.getRoot());
        roomPaneControllers.add(newRoomController);
        roomsTabPane.getTabs().add(newTab);

        roomsTabPane.getSelectionModel().select(newTab);
        roomData.save();
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

    public void unloadRoom(RoomData roomData)
    {
        roomPaneControllers.removeIf(roomController -> roomController.getRoomData().equals(roomData));
        roomsTabPane.getTabs().removeIf(tab -> tab.getText().equals(roomData.roomName));
    }

    @FXML
    public void deleteRoom(ActionEvent actionEvent)
    {
        new ConfirmationTextDialogController(
                LangFileLoader.getTranslation("textblock.deleteroomconfdialog"),
                result ->
                {
                    if (!result) return;

                    var roomName = activeRoom.getRoomData();

                    if (Rooms.deleteRoom(roomName)) unloadRoom(roomName);
                }
        ).instantiate();
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
