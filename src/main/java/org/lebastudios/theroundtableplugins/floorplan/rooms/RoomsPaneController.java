package org.lebastudios.theroundtableplugins.floorplan.rooms;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.dialogs.FormDialogController;
import org.lebastudios.theroundtable.locale.Translator;
import org.lebastudios.theroundtableplugins.cr.cash.CashRegister;
import org.lebastudios.theroundtableplugins.floorplan.FloorPlanningCamelotEvents;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomData;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomObjData;
import org.lebastudios.theroundtableplugins.floorplan.forms.RoomFormPaneController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.RoomObjectController;
import org.lebastudios.theroundtable.components.IconButton;

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
            FloorPlanningCamelotEvents.getInstance().invokeOnOrderModEvent();
        });
    }

    @FXML
    @Override
    protected void initialize()
    {
        loadRooms();

        FloorPlanningCamelotEvents.getInstance().onRoomDeleted.addWeakListener(onRoomDeleted);
        FloorPlanningCamelotEvents.getInstance().onRoomCreated.addWeakListener(onRoomCreated);

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
        new FormDialogController<>(new RoomFormPaneController(), new RoomData())
                .setOwner(this.getStage() == null
                        ? MainStageController.getInstance().getStage()
                        : this.getStage()
                ).instantiate();
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
                Translator.getInstance().t("floor-plan:textblock.deleteroomconfdialog"),
                result ->
                {
                    if (!result) return;

                    FloorPlanningCamelotEvents.getInstance().onRoomDeleted.invoke(activeRoom.getRoomData());
                }
        ).setOwner(this.getStage()).instantiate();
    }

    @FXML
    public void swapMode(ActionEvent actionEvent)
    {
        RoomObjectController.editMode = !RoomObjectController.editMode;

        if (RoomObjectController.editMode)
        {
            swapModeButton.setIconName("core:exit.png");
            instanciateObjPane.setVisible(true);
        }
        else
        {
            swapModeButton.setIconName("core:edit.png");
            instanciateObjPane.setVisible(false);
        }
    }
}
