package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableDrawing;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;

import java.io.File;
import java.io.FileReader;

public class Rooms
{
    public static File getRoomsFile()
    {
        return new File(PluginTableDrawing.getInstance().getPluginFolder(), "rooms");
    }
    
    public static boolean existsRoom(String roomName)
    {
        return new File(getRoomsFile(), roomName).exists();
    }
    
    @SneakyThrows
    public static RoomData loadRoom(String roomName)
    {
        var roomFile = new File(getRoomsFile(), roomName);

        if (!roomFile.exists()) return new RoomData(roomName);

        return new Gson().fromJson(new FileReader(roomFile), RoomData.class);
    }

    public static boolean deleteRoom(RoomData roomData)
    {
        var roomFile = roomData.getFile();

        if (roomFile.exists()) return roomFile.delete();
    }
}
