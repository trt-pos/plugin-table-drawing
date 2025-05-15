package org.lebastudios.theroundtable.plugintabledrawing.rooms.objects;

import javafx.application.Platform;
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
import org.lebastudios.theroundtable.events.PluginEvents;
import org.lebastudios.theroundtable.locale.Translator;
import org.lebastudios.theroundtable.plugincashregister.cash.CashRegister;
import org.lebastudios.theroundtable.plugincashregister.cash.Order;
import org.lebastudios.theroundtable.plugincashregister.cash.OrderItem;
import org.lebastudios.theroundtable.plugintabledrawing.PluginTableCamelotEvents;
import org.lebastudios.theroundtable.plugintabledrawing.data.OrderData;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomPaneController;
import org.lebastudios.theroundtable.plugintabledrawing.rooms.RoomsPaneController;
import org.lebastudios.theroundtable.components.IconView;

public class OrderStationController extends RoomObjectController
{
    public static OrderStationController lastCLickedTable = null;

    @Getter private Order order;
    @Getter @FXML public Label tableNameLabel;
    private ImageView orderDecorationIcon;

    public OrderStationController(RoomObjData roomObjectData, RoomPaneController parentController)
    {
        super(roomObjectData, parentController);

        if (roomObjectData.orderData == null)
        {
            roomObjectData.orderData = new OrderData();
        }

        setOrder(roomObjectData.orderData.intoOrder(roomObjectData.tableName));
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

    public void setOrder(Order order)
    {
        this.order = order;

        this.order.getObservableOrderItems().addListener((ListChangeListener<OrderItem>) _ ->
        {
            PluginTableCamelotEvents.getInstance().invokeOnOrderModEvent();
        });

        CashRegister cashRegister = CashRegister.getInstance();

        // Updating the order in the UI by swapping the old one with the new generated one
        if (lastCLickedTable != this) return;
        if (cashRegister.getActualOrder().equals(cashRegister.getCashRegisterOrder())) return;

        Platform.runLater(() -> cashRegister.swapOrder(order));
    }

    public void updateOrderDecoration()
    {
        var image = order.getOrderItems().isEmpty()
                ? null
                : ImageLoader.getIcon("receipt.png");

        orderDecorationIcon.setImage(image);
    }

    @Override
    protected void onClick()
    {
        lastCLickedTable = this;
        PluginEvents.invokePluginEvent("plugin-cash-register:showOrder", order);
    }

    @Override
    protected void onContextMenuEditting(double x, double y)
    {
        var contextMenu = new ContextMenu();

        // Rotate Left Button
        var menuItem_0 = new MenuItem(Translator.getInstance().t("phrase.rotateleft"));
        var graphic = new IconView("rotate-left.png");
        graphic.setIconSize(24);
        menuItem_0.setGraphic(graphic);
        menuItem_0.setOnAction(_ ->
        {
            icon.setRotate(icon.getRotate() - 90);
            RoomsPaneController.getInstance().activeRoom.onRoomDataUpdated();
        });

        // Rotate Right Button
        var menuItem_1 = new MenuItem(Translator.getInstance().t("phrase.rotateright"));
        graphic = new IconView("rotate-right.png");
        graphic.setIconSize(24);
        menuItem_1.setGraphic(graphic);
        menuItem_1.setOnAction(_ ->
        {
            icon.setRotate(icon.getRotate() + 90);
            RoomsPaneController.getInstance().activeRoom.onRoomDataUpdated();
        });

        // Rename Button
        var firstMenuItem = new MenuItem(Translator.getInstance().t("word.rename"));
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
            }, Translator.getInstance().t("title.newtablereqtext"), "Rename", null)
                    .setOwner(this.getStage())
                    .instantiate(true);

            order.setOrderName(tableNameLabel.getText());
            RoomsPaneController.getInstance().activeRoom.onRoomDataUpdated();
        });

        // Delete Button
        var secondMenuItem = new MenuItem(Translator.getInstance().t("word.delete"));
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

    @Override
    public void loadFXML()
    {
        this.root = new org.lebastudios.theroundtable.plugintabledrawing.rooms.objects.OrderStation$View(this);
        
        String iconName = switch (this.roomObjectData.roomObjectType)
        {
            case BAR_STOOL -> "edit-map-bar-stool.png";
            case ROUND -> "edit-map-round-table.png";
            case SQUARE -> "edit-map-square-table.png";
            default -> throw new IllegalStateException("Unexpected value: " + this.roomObjectData.roomObjectType);
        };
        
        this.icon.setIconName(iconName);
        this.initialize();
    }
}
