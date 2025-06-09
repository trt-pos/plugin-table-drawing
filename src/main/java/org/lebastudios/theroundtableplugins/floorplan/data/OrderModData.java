package org.lebastudios.theroundtableplugins.floorplan.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.lebastudios.theroundtable.camelot.FromBytes;
import org.lebastudios.theroundtable.camelot.FromJsonBytesToObject;
import org.lebastudios.theroundtable.camelot.FromObjectToJsonBytes;
import org.lebastudios.theroundtable.camelot.IntoBytes;

import java.text.ParseException;

@AllArgsConstructor
@NoArgsConstructor
public class OrderModData implements IntoBytes, FromBytes<OrderModData>
{
    public String roomName;
    public int tableId;
    public OrderData newOrder;
    
    @Override
    public OrderModData fromBytes(byte[] bytes) throws ParseException
    {
        return new FromJsonBytesToObject<>(this.getClass()).fromBytes(bytes);
    }

    @Override
    public byte[] intoBytes()
    {
        return new FromObjectToJsonBytes(this).intoBytes();
    }
}
