package org.lebastudios.theroundtable.plugintabledrawing;

import org.lebastudios.theroundtable.events.CamelotEvent;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;

public class PluginTableCamelotEvents
{
    private static PluginTableCamelotEvents instance;
    
    public static PluginTableCamelotEvents getInstance()
    {
        if (instance == null) instance = new PluginTableCamelotEvents();
        
        return instance;
    }
    
    private PluginTableCamelotEvents() {}
    
    public final CamelotEvent<RoomData> roomHasChangedEvent = new CamelotEvent<>(
            "plugin-table-drawing:room-has-changed", 
            new RoomData()
    );
}
