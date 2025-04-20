package org.lebastudios.theroundtable.plugintabledrawing.forms;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.controllers.FormPaneController;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableCamelotEvents;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.Rooms;

public class RoomFormPaneController extends FormPaneController<RoomData>
{
    @FXML public TextField roomName;
    @FXML public TextField roomHeight;
    @FXML public TextField roomWidth;
    
    @Override
    protected void updateUI(RoomData object) 
    {
        roomName.setText(object.roomName);
        roomHeight.setText(String.valueOf(object.heightInTiles));
        roomWidth.setText(String.valueOf(object.widthInTiles));
    }

    @Override
    public boolean validate()
    {
        if (roomName.getText() == null) 
        {
            UIEffects.shakeNode(roomName);
            return false;
        }
        
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

    @Override
    public RoomData buildObject(RoomData roomData)
    {
        roomData.roomName = roomName.getText();
        roomData.widthInTiles = Double.parseDouble(roomWidth.getText());
        roomData.heightInTiles = Double.parseDouble(roomHeight.getText());
        
        return roomData;
    }

    @Override
    public boolean onSaveAction(RoomData object)
    {
        PluginTableCamelotEvents.getInstance().onRoomCreated.invoke(object);
        return true;
    }
}
