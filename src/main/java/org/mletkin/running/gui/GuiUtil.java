package org.mletkin.running.gui;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.mletkin.running.gui.filter.Preds;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public final class GuiUtil {

    private GuiUtil() {
        // prevent instantiation
    }

    public static <T> TableColumn<T, Preds> mkPredsColumn(String colName, BiConsumer<T, Preds> setter) {
        var column = new TableColumn<T, Preds>(colName);
        column.setCellValueFactory(new PropertyValueFactory<T, Preds>(colName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new PredsConverter()));
        column.setOnEditCommit(mkEditHandler(setter));
        return column;
    }

    private static class PredsConverter extends StringConverter<Preds> {

        @Override
        public String toString(Preds object) {
            return object.toString();
        }

        @Override
        public Preds fromString(String string) {

            return Preds.valueOf(string);
        }

    }

    /**
     * Creates an editable column containing a string.
     *
     * @param <T>
     *                    Type of the table entries
     * @param colName
     *                    Name of the column
     * @param setter
     *                    setter of the table entries field
     * @return the column object
     */
    public static <T> TableColumn<T, String> mkColumn(String colName, BiConsumer<T, String> setter) {
        var column = new TableColumn<T, String>(colName);
        column.setCellValueFactory(new PropertyValueFactory<T, String>(colName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(mkEditHandler(setter));
        return column;
    }

    private static <T, X> EventHandler<CellEditEvent<T, X>> mkEditHandler(BiConsumer<T, X> setter) {
        return evt -> {
            T entry = (T) evt.getTableView().getItems().get(evt.getTablePosition().getRow());
            setter.accept(entry, evt.getNewValue());
        };
    }

    public static double max(double start, double... rest) {
        return chain(Math::max, start, rest);
    }

    public static double min(double start, double... rest) {
        return chain(Math::min, start, rest);
    }

    public static double chain(BiFunction<Double, Double, Double> fkt, double start, double... rest) {
        var result = start;
        for (var x : rest) {
            result = fkt.apply(result, x);
        }
        return result;
    }

}
