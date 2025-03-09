package org.lebastudios.theroundtable.plugintabledrawing.data;

import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomController;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomObjController;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.TableObjectController;

public class RoomObjData
{
    public static final RoomObjData SQUARE_TABLE =
            new RoomObjData(LangFileLoader.getTranslation("phrase.squaretable"), RoomObjectType.SQUARE, 0, 0, 0);
    public static final RoomObjData ROUND_TABLE =
            new RoomObjData(LangFileLoader.getTranslation("phrase.roundtable"), RoomObjectType.ROUND, 0, 0, 0);
    public static final RoomObjData BAR_STOOL =
            new RoomObjData(LangFileLoader.getTranslation("phrase.barstool"), RoomObjectType.BAR_STOOL, 0, 0, 0);
    public static final RoomObjData BAR_TABLE = 
            new RoomObjData("Bar Table", RoomObjectType.BAR_TABLE, 0, 0, 0);
    public static final RoomObjData ESTABLISHMENT_WALL =
            new RoomObjData("Establishment Wall", RoomObjectType.ESTABLISHMENT_WALL, 0, 0, 0);
    
    public int id = -1;
    public String tableName;
    public RoomObjectType roomObjectType;
    public double x;
    public double y;
    public double rotation;
    public OrderData orderData;

    public RoomObjData(String tableName, RoomObjectType roomObjectType, double x, double y, double rotation)
    {
        this.tableName = tableName;
        this.roomObjectType = roomObjectType;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public RoomObjController intoController(RoomController parentController)
    {
        return switch (this.roomObjectType)
        {
            case ROUND, SQUARE, BAR_STOOL-> new TableObjectController(this, parentController);
            default -> new RoomObjController(this, parentController);
        };
    }
}
