package org.lebastudios.theroundtable.plugintabledrawing;

import javafx.scene.control.Button;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.files.JsonFile;
import org.lebastudios.theroundtable.fxml2java.CompileFxml;
import org.lebastudios.theroundtable.plugins.IPlugin;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.components.IconButton;

import java.util.ArrayList;
import java.util.List;

@CompileFxml(
        directories = {
                "org/lebastudios/theroundtable/plugintabledrawing/forms",
                "org/lebastudios/theroundtable/plugintabledrawing/rooms",
                "org/lebastudios/theroundtable/plugintabledrawing/rooms/objects",
        }
)
public class PluginTableDrawing implements IPlugin
{
    private static PluginTableDrawing instance;
    
    public static PluginTableDrawing getInstance()
    {
        if (instance == null) throw new IllegalStateException("This plugin has to be instantiated");

        return instance;
    }

    @Override
    public void initialize()
    {
        instance = this;
        
        PluginTableCamelotEvents.getInstance().onRoomChanged.addListener(JsonFile::save);
    }

    @Override
    public List<Button> getRightButtons()
    {
        var buttonsList = new ArrayList<Button>();

        buttonsList.add(loadTablesButton());

        return buttonsList;
    }

    private Button loadTablesButton()
    {
        var newButton = new IconButton("table.png");

        newButton.setOnAction(_ -> MainStageController.getInstance().setCentralNode(
                RoomsPaneController.getInstance()

        ));

        return newButton;
    }
}
