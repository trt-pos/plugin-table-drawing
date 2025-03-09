package org.lebastudios.theroundtable.plugintabledrawing.data;

import org.lebastudios.theroundtable.config.data.FileRepresentator;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.Rooms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoomData implements FileRepresentator
{
    public String roomName;
    public double widthInTiles = 36;
    public double heightInTiles = 21;
    public List<RoomObjData> roomObjects = new ArrayList<>();

    public RoomData(String roomName)
    {
        this.roomName = roomName;
    }

    public void updateRoomObjIds()
    {
        for (int i = 0; i < roomObjects.size(); i++)
        {
            roomObjects.get(i).id = i;
        }
    }
    
    @Override
    public File getFile()
    {
        return new File(Rooms.getRoomsFile(), roomName);
    }

    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof RoomData roomData)) return false;

        return Double.compare(widthInTiles, roomData.widthInTiles) == 0 &&
                Double.compare(heightInTiles, roomData.heightInTiles) == 0 &&
                Objects.equals(roomName, roomData.roomName) &&
                Objects.equals(roomObjects, roomData.roomObjects);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hashCode(roomName);
        result = 31 * result + Double.hashCode(widthInTiles);
        result = 31 * result + Double.hashCode(heightInTiles);
        result = 31 * result + Objects.hashCode(roomObjects);
        return result;
    }
}
