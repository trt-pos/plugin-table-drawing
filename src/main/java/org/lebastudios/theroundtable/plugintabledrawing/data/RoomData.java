package org.lebastudios.theroundtable.plugintabledrawing.data;

import lombok.NoArgsConstructor;
import org.lebastudios.theroundtable.camelot.FromBytes;
import org.lebastudios.theroundtable.camelot.FromJsonBytesToObject;
import org.lebastudios.theroundtable.camelot.FromObjectToJsonBytes;
import org.lebastudios.theroundtable.camelot.IntoBytes;
import org.lebastudios.theroundtable.files.JsonFile;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.Rooms;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class RoomData extends JsonFile<RoomData> implements IntoBytes, FromBytes<RoomData>
{
    public String roomName;
    public double widthInTiles = 24;
    public double heightInTiles = 24;
    public int nextObjId = 1;
    public List<RoomObjData> roomObjects = new ArrayList<>();

    public RoomData(String roomName)
    {
        this.roomName = roomName;
    }

    public void updateRoomObjIds()
    {
        for (RoomObjData roomObject : roomObjects)
        {
            if (roomObject.id == 0)
            {
                roomObject.id = this.getAndIncrementNextObjId();
            }
        }
    }
    
    public synchronized int getAndIncrementNextObjId()
    {
        return nextObjId++;
    }
    
    @Override
    public File getFile()
    {
        return new File(Rooms.getRoomsFile(), roomName);
    }

    @Override
    public RoomData fromBytes(byte[] bytes) throws ParseException
    {
        return new FromJsonBytesToObject<>(this.getClass()).fromBytes(bytes);
    }

    @Override
    public byte[] intoBytes()
    {
        return new FromObjectToJsonBytes(this).intoBytes();
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof RoomData roomData)) return false;

        return roomName.equals(roomData.roomName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(roomName);
    }
}
