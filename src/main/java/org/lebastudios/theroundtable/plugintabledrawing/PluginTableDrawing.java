package org.lebastudios.theroundtable.plugintabledrawing;

import javafx.scene.control.Button;
import org.lebastudios.theroundtable.MainStageController;
import org.lebastudios.theroundtable.plugins.IPlugin;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.ui.IconButton;

import java.util.ArrayList;
import java.util.List;

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
