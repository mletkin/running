package org.mletkin.running.gui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Renders the month calendar.
 */
public class MonthCalendar extends BorderPane {
    private static final String[] DAYS = //
            { "monday", "tuesday", "wednesday", "thuesday", "friday", "saturday", "sunday" };
    private YearMonth current;
    private LocalDate first;
    private LocalDate last;
    private BiFunction<LocalDate, Boolean, Node> drawDay;
    private Function<LocalDate, Node> drawWeek;

    /**
     * Creates the calendar.
     *
     * @param drawDay
     *                     renders the content of a day cell
     * @param drawWeek
     *                     renders the content of a week cell
     */
    public MonthCalendar(BiFunction<LocalDate, Boolean, Node> drawDay, Function<LocalDate, Node> drawWeek) {
        this.drawDay = drawDay;
        this.drawWeek = drawWeek;
        draw(YearMonth.now());
    }

    private void draw(YearMonth month) {
        current = month;
        getChildren().clear();
        header();
        body();
        footer();
    }

    private void header() {
        Text tHeader = new Text(current.getMonth().name() + ", " + current.getYear());
        setTop(tHeader);
        setAlignment(tHeader, Pos.CENTER);
        setMargin(tHeader, new Insets(1));
    }

    private void body() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        // Draw days of the week
        for (int day = 0; day <= 6; day++) {
            var tDayName = new Text(DAYS[day]);
            pane.add(tDayName, day, 0);
        }

        int count = 0;
        var firstDay = startOfCalendar();
        var lastDay = endOfCalendar();
        for (LocalDate day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1), count++) {
            var active = day.getMonth() == current.getMonth();
            var dayNode = drawDay.apply(day, active);
            pane.add(dayNode, count % 7, (int) (count / 7) + 1);
            if (count % 7 == 0) {
                var weekNode = drawWeek.apply(day);
                pane.add(weekNode, 8, (int) (count / 7) + 1);
            }
        }

        setCenter(pane);
        // setMargin(pane, new Insets(30));
    }

    private LocalDate startOfCalendar() {
        var start = current.atDay(1);
        return start.minusDays(start.getDayOfWeek().ordinal());
    }

    private LocalDate endOfCalendar() {
        var end = current.atDay(current.lengthOfMonth());
        return end.plusDays(6 - end.getDayOfWeek().ordinal());
    }

    private void footer() {
        Button btPrev = new Button("Prev");
        Button btNext = new Button("Next");

        btPrev.setOnAction(e -> draw(current.minusMonths(1)));
        btNext.setOnAction(e -> draw(current.plusMonths(1)));

        HBox hbFooter = new HBox(10);
        hbFooter.getChildren().addAll(btPrev, btNext);
        hbFooter.setAlignment(Pos.CENTER);

        setBottom(hbFooter);
        setMargin(hbFooter, new Insets(15));
    }

}
