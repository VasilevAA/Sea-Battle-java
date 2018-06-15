package gui.elements;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

public class ShipItem extends ImageView {
    private int direction;
    private int size;

    public int getDirection() {
        return direction;
    }

    public int getSize() {
        return size;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public ShipItem(String name, int direction, int size) {
        super(name);
        this.direction = direction;
        this.size = size;


        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            db.setDragView(getImage(), 14, 14);

            ClipboardContent cont = new ClipboardContent();
            cont.putString(getId());
            db.setContent(cont);
            event.consume();
        });
    }

    public void rotate() {
        setRotate(90);
        SnapshotParameters par = new SnapshotParameters();
        par.setFill(Color.TRANSPARENT);
        Image nIm = snapshot(par, null);
        setRotate(0);
        setImage(nIm);
        setDirection(getDirection() == 1 ? 2 : 1);

    }
}
