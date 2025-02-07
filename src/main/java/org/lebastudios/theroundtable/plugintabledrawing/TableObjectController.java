package org.lebastudios.theroundtable.plugintabledrawing;

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
import org.lebastudios.theroundtable.events.PluginEvents;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugincashregister.cash.Order;
import org.lebastudios.theroundtable.plugintabledrawing.data.RoomObjData;
import org.lebastudios.theroundtable.ui.IconView;

public class TableObjectController extends RoomObjController
{
    private Order order;
    @Getter @FXML private Label tableNameLabel;
    private ImageView orderDecorationIcon;

    private final IEventMethod onPluginShowListener = () ->
    {
        var image = order.getOrderItems().isEmpty()
                ? null
                : ImageLoader.getIcon("receipt.png");

        orderDecorationIcon.setImage(image);
    };
    
    public TableObjectController(RoomObjData roomObjectType, RoomController parentController)
    {
        super(roomObjectType, parentController);

        order = new Order();
        order.setOrderName(roomObjectType.tableName);
    }

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize();

        orderDecorationIcon = new ImageView();
        new GraphicDecoration(orderDecorationIcon).applyDecoration(getRoot());
        
        orderDecorationIcon.setFitHeight(33);
        orderDecorationIcon.setFitWidth(33);
        
        tableNameLabel.setText(roomObjectData.tableName);
        PluginTableDrawingEvents.onPluginShow.addWeakListener(onPluginShowListener);
    }

    @Override
    protected void onClick()
    {
        try
        {
            PluginEvents.invokePluginEvent("plugin-cash-register:showOrder", order);
        }
        catch (Exception exception)
        {
            System.err.println("Unable to invoke plugin event: " + exception);
        }
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
        menuItem_0.setOnAction(_ -> icon.setRotate(icon.getRotate() - 90));

        // Rotate Right Button
        var menuItem_1 = new MenuItem(LangFileLoader.getTranslation("phrase.rotateright"));
        graphic = new IconView("rotate-right.png");
        graphic.setIconSize(24);
        menuItem_1.setGraphic(graphic);
        menuItem_1.setOnAction(_ -> icon.setRotate(icon.getRotate() + 90));

        // Rename Button
        var firstMenuItem = new MenuItem(LangFileLoader.getTranslation("word.rename"));
        graphic = new IconView("rename.png");
        graphic.setIconSize(24);
        firstMenuItem.setGraphic(graphic);
        firstMenuItem.setOnAction(_ ->
                {
                    new RequestTextDialogController(tableNameLabel::setText,
                            LangFileLoader.getTranslation("title.newtablereqtext"),
                            "Rename", null).instantiate();
                    order.setOrderName(tableNameLabel.getText());
                }
        );

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
        return new RoomObjData(tableNameLabel.getText(), roomObjectData.roomObjectType, root.getLayoutX(),
                root.getLayoutY(), icon.getRotate());
    }
}
