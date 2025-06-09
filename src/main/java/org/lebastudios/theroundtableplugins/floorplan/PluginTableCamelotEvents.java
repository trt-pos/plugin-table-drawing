package org.lebastudios.theroundtableplugins.floorplan;

import org.lebastudios.theroundtable.events.CamelotEvent;
import org.lebastudios.theroundtableplugins.cr.cash.CashRegister;
import org.lebastudios.theroundtableplugins.floorplan.data.OrderData;
import org.lebastudios.theroundtableplugins.floorplan.data.OrderModData;
import org.lebastudios.theroundtableplugins.floorplan.data.RoomData;
import org.lebastudios.theroundtableplugins.floorplan.rooms.RoomsPaneController;
import org.lebastudios.theroundtableplugins.floorplan.rooms.objects.OrderStationController;

public class PluginTableCamelotEvents
{
    private static PluginTableCamelotEvents instance;
    
    public static PluginTableCamelotEvents getInstance()
    {
        if (instance == null) instance = new PluginTableCamelotEvents();
        
        return instance;
    }
    
    private PluginTableCamelotEvents() {}
    
    public final CamelotEvent<RoomData> onRoomChanged = new CamelotEvent<>(
            "plugin-table-drawing:room-changed", 
            new RoomData()
    );

    public final CamelotEvent<RoomData> onRoomCreated = new CamelotEvent<>(
            "plugin-table-drawing:room-created",
            new RoomData()
    );
    
    public final CamelotEvent<RoomData> onRoomDeleted = new CamelotEvent<>(
            "plugin-table-drawing:room-deleted",
            new RoomData()
    );
    
    public final CamelotEvent<OrderModData> onOrderMod = new CamelotEvent<>(
            "plugin-table-drawing:order-mod",
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

        PluginTableCamelotEvents.getInstance().onOrderMod.invoke(modData);
    }
}
