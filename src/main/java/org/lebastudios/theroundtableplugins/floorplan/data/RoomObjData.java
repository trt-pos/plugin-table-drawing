package org.lebastudios.theroundtableplugins.floorplan.data;

import org.lebastudios.theroundtable.locale.Translator;
import org.lebastudios.theroundtableplugins.floorplan.rooms.RoomPaneController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.RoomObjectController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.OrderStationController;

public class RoomObjData
{
    public static final RoomObjData SQUARE_TABLE =
            new RoomObjData(Translator.getInstance().t("floor-plan:phrase.squaretable"), RoomObjectType.SQUARE, 0, 0, 0);
    public static final RoomObjData ROUND_TABLE =
            new RoomObjData(Translator.getInstance().t("floor-plan:phrase.roundtable"), RoomObjectType.ROUND, 0, 0, 0);
    public static final RoomObjData BAR_STOOL =
            new RoomObjData(Translator.getInstance().t("floor-plan:phrase.barstool"), RoomObjectType.BAR_STOOL, 0, 0, 0);
    public static final RoomObjData BAR_TABLE = 
            new RoomObjData("Bar Table", RoomObjectType.BAR_TABLE, 0, 0, 0);
    public static final RoomObjData ESTABLISHMENT_WALL =
            new RoomObjData("Establishment Wall", RoomObjectType.ESTABLISHMENT_WALL, 0, 0, 0);
    
    public int id;
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

    public RoomObjectController intoController(RoomPaneController parentController)
    {
        return switch (this.roomObjectType)
        {
            case ROUND, SQUARE, BAR_STOOL-> new OrderStationController(this, parentController);
            default -> new RoomObjectController(this, parentController);
        };
    }
}
