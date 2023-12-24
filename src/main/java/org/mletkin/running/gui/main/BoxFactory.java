package org.mletkin.running.gui.main;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.util.function.Consumer;
import java.util.function.Function;

import org.mletkin.running.model.Data;
import org.mletkin.running.util.Format;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

class BoxFactory {

    private Data data;
    private Consumer<Range> update;

    BoxFactory(Data data, Consumer<Range> update) {
        this.data = data;
        this.update = update;
    }

    private Node mkBox(Range range, Function<Summarizer, String> textFkt, boolean active) {
        var sum = new Summarizer();

        data.runs() //
                .filter(range::filter) //
                .forEach(sum::add);

        var text = textFkt.apply(sum);

        var rectangle = new Rectangle(80, 50);
        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(active ? Color.BLACK : Color.GRAY);

        var textBox = new Text(text);
        textBox.setFill(active ? Color.BLACK : Color.GRAY);

        var box = new StackPane();
        box.getChildren().addAll(rectangle, textBox);
        box.setOnMouseClicked(e -> update.accept(range));

        return box;
    }

    Node mkDayBox(LocalDate day, boolean active) {
        var range = Range.day(day);
        return mkBox(range, s -> dayText(day, s), active);
    }

    private String dayText(LocalDate day, Summarizer sum) {
        return sum.dist() > 0.001 //
                ? String.format("%d\n%.3f km\n%s", day.getDayOfMonth(), sum.dist(), Format.time(sum.time()))
                : String.format("%d\n --", day.getDayOfMonth());
    }

    Node mkWeekBox(LocalDate day) {
        var range = Range.week(day);
        return mkBox(range, s -> weekText(day, s), true);
    }

    private String weekText(LocalDate day, Summarizer sum) {
        int weekOfYear = day.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        var text = sum.dist() > 0.001 //
                ? String.format("%d\n%.3f km\n%s", weekOfYear, sum.dist(), Format.time(sum.time()))
                : String.format("%d\n--", weekOfYear);
        return text;
    }

    Node monthHeader(YearMonth ym) {
        var month = new Text(ym.getMonth().name());
        var year = new Text(ym.getYear() + "");
        month.setOnMouseClicked(e -> update.accept(Range.month(ym)));
        year.setOnMouseClicked(e -> update.accept(Range.year(ym.getYear())));
        return new HBox(month, new Text(", "), year);
    }
}