package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import lombok.NonNull;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableDrawing;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.net.URL;
import java.util.function.Consumer;

public class RoomCreationStageController extends StageController<RoomCreationStageController>
{
    private final Consumer<RoomData> roomDataConsumer;

    @FXML private TextField roomName;
    @FXML private TextField roomHeight;
    @FXML private TextField roomWidth;

    public RoomCreationStageController(@NonNull Consumer<RoomData> roomDataConsumer)
    {
        this.roomDataConsumer = roomDataConsumer;
    }

    @Override
    public String getTitle()
    {
        return "Room creation";
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public Class<?> getBundleClass()
    {
        return PluginTableDrawing.class;
    }

    @Override
    public URL getFXML()
    {
        return RoomCreationStageController.class.getResource("roomCreationStage.fxml");
    }

    @FXML
    private void accept()
    {
        if (!validateData()) return;

        RoomData roomData = new RoomData(roomName.getText());

        roomData.widthInTiles = Double.parseDouble(roomWidth.getText());
        roomData.heightInTiles = Double.parseDouble(roomHeight.getText());

        roomDataConsumer.accept(roomData);
        close();
    }

    private boolean validateData()
    {
        roomName.setText(roomName.getText().trim());
        
        if (roomName.getText().isBlank())
        {
            UIEffects.shakeNode(roomName);
            return false;
        }

        if (Rooms.existsRoom(roomName.getText())) 
        {
            UIEffects.shakeNode(roomName);
            return false;
        }
        
        try
        {
            if (Double.parseDouble(roomWidth.getText()) < 1)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            UIEffects.shakeNode(roomWidth);
            return false;
        }

        try
        {
            if (Double.parseDouble(roomHeight.getText()) < 1)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            UIEffects.shakeNode(roomHeight);
            return false;
        }

        return true;
    }
}
