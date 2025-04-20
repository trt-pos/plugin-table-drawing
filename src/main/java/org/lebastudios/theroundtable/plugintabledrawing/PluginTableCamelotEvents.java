package org.lebastudios.theroundtable.plugintabledrawing;

import org.lebastudios.theroundtable.events.CamelotEvent;
import org.lebastudios.theroundtable.plugincashregister.cash.CashRegister;
import org.lebastudios.theroundtable.plugintabledrawing.data.OrderData;
import org.lebastudios.theroundtable.plugintabledrawing.data.OrderModData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomData;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomPaneController;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.objects.TableObjectController;

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
        int tableId = TableObjectController.lastCLickedTable.getRoomObjectData().id;
        OrderData order = OrderData.fromOrder(TableObjectController.lastCLickedTable.getOrder());

        OrderModData modData = new OrderModData(
                roomName,
                tableId,
                order
        );

        PluginTableCamelotEvents.getInstance().onOrderMod.invoke(modData);
    }
}
