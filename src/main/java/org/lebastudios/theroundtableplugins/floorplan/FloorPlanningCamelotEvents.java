package org.lebastudios.theroundtableplugins.floorplan;

import org.lebastudios.theroundtable.camelot.CamelotEvent;
import org.lebastudios.theroundtableplugins.cr.cash.CashRegister;
import org.lebastudios.theroundtableplugins.floorplan.data.OrderData;
import org.lebastudios.theroundtableplugins.floorplan.data.OrderModData;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomData;
import org.lebastudios.theroundtableplugins.floorplan.rooms.RoomsPaneController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.OrderStationController;

public class FloorPlanningCamelotEvents
{
    private static FloorPlanningCamelotEvents instance;
    
    public static FloorPlanningCamelotEvents getInstance()
    {
        if (instance == null) instance = new FloorPlanningCamelotEvents();
        
        return instance;
    }
    
    private FloorPlanningCamelotEvents() {}
    
    public final CamelotEvent<RoomData> onRoomChanged = new CamelotEvent<>(
            PluginFloorPlanning.class,
            "room-changed", 
            new RoomData()
    );

    public final CamelotEvent<RoomData> onRoomCreated = new CamelotEvent<>(
            PluginFloorPlanning.class,
            "room-created",
            new RoomData()
    );
    
    public final CamelotEvent<RoomData> onRoomDeleted = new CamelotEvent<>(
            PluginFloorPlanning.class,
            "room-deleted",
            new RoomData()
    );
    
    public final CamelotEvent<OrderModData> onOrderMod = new CamelotEvent<>(
            PluginFloorPlanning.class,
            "order-mod",
            new OrderModData()
    );
    
    public void invokeOnOrderModEvent()
    {
        CashRegister cashRegister = CashRegister.getInstance();

        if (cashRegister.getActualOrder() == cashRegister.getCashRegisterOrder()) return;

        String roomName = RoomsPaneController.getInstance().activeRoom.getRoomData().roomName;
        int tableId = OrderStationController.lastCLickedTable.getRoomObjectData().id;
        OrderData order = OrderData.fromOrder(OrderStationController.lastCLickedTable.getOrder());

        OrderModData modData = new OrderModData(
                roomName,
                tableId,
                order
        );

        FloorPlanningCamelotEvents.getInstance().onOrderMod.invoke(modData);
    }
}
