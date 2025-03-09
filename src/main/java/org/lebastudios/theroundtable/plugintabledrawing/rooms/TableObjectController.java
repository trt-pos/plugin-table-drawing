package org.lebastudios.theroundtable.plugintabledrawing.rooms;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.dialogs.RequestTextDialogController;
import org.lebastudios.theroundtable.events.IEventMethod;
import org.lebastudios.theroundtable.events.IEventMethod1;
import org.lebastudios.theroundtable.events.PluginEvents;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugincashregister.cash.CashRegister;
import org.lebastudios.theroundtable.plugincashregister.cash.Order;
import org.lebastudios.theroundtable.plugincashregister.cash.OrderItem;
import org.lebastudios.theroundtable.plugintabledrawing.data.OrderData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;
import org.lebastudios.theroundtable.ui.IconView;

public class TableObjectController extends RoomObjController
{
    private final Order order;
    @Getter @FXML private Label tableNameLabel;
    private ImageView orderDecorationIcon;

    public TableObjectController(RoomObjData roomObjectData, RoomController parentController)
    {
        super(roomObjectData, parentController);

        if (roomObjectData.orderData == null)
        {
            roomObjectData.orderData = new OrderData();
        }

        order = roomObjectData.orderData.intoOrder(roomObjectData);

        order.getOrderItems().addListener((ListChangeListener<OrderItem>) _ ->
        {
            RoomsPaneController.getInstance().activeRoom.saveRoom();
            updateOrderDecoration();
        });
    }

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize();

        orderDecorationIcon = new ImageView();
        new GraphicDecoration(orderDecorationIcon).applyDecoration(getRoot());

        updateOrderDecoration();

        orderDecorationIcon.setFitHeight(33);
        orderDecorationIcon.setFitWidth(33);

        tableNameLabel.setText(roomObjectData.tableName);
    }

    private void updateOrderDecoration()
    {
        var image = order.getOrderItems().isEmpty()
                ? null
                : ImageLoader.getIcon("receipt.png");

        orderDecorationIcon.setImage(image);
    }

    @Override
    protected void onClick()
    {
        PluginEvents.invokePluginEvent("plugin-cash-register:showOrder", order);
    }

    @Override
    protected void onContextMenuEditting(double x, double y)
    {
        var contextMenu = new ContextMenu();

        // Rotate Left Button
        var menuItem_0 = new MenuItem(LangFileLoader.getTranslation("phrase.rotateleft"));
        var graphic = new IconView("rotate-left.png");
        graphic.setIconSize(24);
        menuItem_0.setGraphic(graphic);
        menuItem_0.setOnAction(_ ->
        {
            icon.setRotate(icon.getRotate() - 90);
            RoomsPaneController.getInstance().activeRoom.saveRoom();
        });

        // Rotate Right Button
        var menuItem_1 = new MenuItem(LangFileLoader.getTranslation("phrase.rotateright"));
        graphic = new IconView("rotate-right.png");
        graphic.setIconSize(24);
        menuItem_1.setGraphic(graphic);
        menuItem_1.setOnAction(_ ->
        {
            icon.setRotate(icon.getRotate() + 90);
            RoomsPaneController.getInstance().activeRoom.saveRoom();
        });

        // Rename Button
        var firstMenuItem = new MenuItem(LangFileLoader.getTranslation("word.rename"));
        graphic = new IconView("rename.png");
        graphic.setIconSize(24);
        firstMenuItem.setGraphic(graphic);
        firstMenuItem.setOnAction(_ ->
        {
            new RequestTextDialogController(text ->
            {
                String newText = text.trim();

                if (newText.length() > 15)
                {
                    newText = newText.substring(0, 15);
                }

                newText = newText.trim();
                
                tableNameLabel.setText(newText);
            }, LangFileLoader.getTranslation("title.newtablereqtext"), "Rename", null).instantiate(true);

            order.setOrderName(tableNameLabel.getText());
            RoomsPaneController.getInstance().activeRoom.saveRoom();
        });

        // Delete Button
        var secondMenuItem = new MenuItem(LangFileLoader.getTranslation("word.delete"));
        graphic = new IconView("delete.png");
        graphic.setIconSize(24);
        secondMenuItem.setGraphic(graphic);
        secondMenuItem.setOnAction(_ -> RoomsPaneController.getInstance().activeRoom.deleteRoomObject(this));

        contextMenu.getItems().addAll(menuItem_0, menuItem_1, firstMenuItem, secondMenuItem);

        contextMenu.show(root, x, y);
    }

    @Override
    public RoomObjData getInstanceObjData()
    {
        RoomObjData roomObjData = super.getInstanceObjData();

        roomObjData.tableName = tableNameLabel.getText();
        roomObjData.orderData = OrderData.fromOrder(order);
        return roomObjData;
    }
}
