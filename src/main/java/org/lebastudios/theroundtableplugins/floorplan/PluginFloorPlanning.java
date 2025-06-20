package org.lebastudios.theroundtableplugins.floorplan;

import javafx.scene.control.Button;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.files.JsonFile;
import org.lebastudios.theroundtable.fxml2java.CompileFxml;
import org.lebastudios.theroundtable.plugins.IPlugin;
import org.lebastudios.theroundtableplugins.floorplan.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.components.IconButton;

import java.util.ArrayList;
import java.util.List;

@CompileFxml(
        directories = {
                "org/lebastudios/theroundtableplugins/floorplan/forms",
                "org/lebastudios/theroundtableplugins/floorplan/rooms",
                "org/lebastudios/theroundtableplugins/floorplan/rooms/objects",
        }
)
public class PluginFloorPlanning implements IPlugin
{
    private static PluginFloorPlanning instance;
    
    public static PluginFloorPlanning getInstance()
    {
        if (instance == null) throw new IllegalStateException("This plugin has to be instantiated");

        return instance;
    }

    @Override
    public void initialize()
    {
        instance = this;
        
        FloorPlanningCamelotEvents.getInstance().onRoomChanged.addListener(JsonFile::save);
    }

    @Override
    public List<Button> getRightButtons()
    {
        var buttonsList = new ArrayList<Button>();

        var newButton = new IconButton("floor-plan:table.png");

        newButton.setOnAction(_ -> MainStageController.getInstance().setCentralNode(
                RoomsPaneController.getInstance()

        ));
        
        buttonsList.add(newButton);

        return buttonsList;
    }
}
