package main.view.component;

import javafx.scene.control.TreeCell;
import main.model.Ascenseur;

public class RessourceTreeCell<T> extends TreeCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        setGraphic(null);

        if (!empty && item != null) {
            if (item instanceof Ascenseur) {
                setStyle(((Ascenseur) item).getState().style);
            } else {
                setStyle("-fx-text-fill: white");
            }
            setText(item.toString());
            setGraphic(getTreeItem().getGraphic());
        }


    }
}
