package org.mletkin.running.gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.mletkin.running.model.Activity;
import org.mletkin.running.model.Data;
import org.mletkin.running.model.Lap;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Main window of the application.
 */
public class Running extends Application {
    private Data data;
    private DayDetails dayDetails = new DayDetails();
    private MonthSummary monthSummary;
    private Stage primary;

    /**
     * Launches the application.
     *
     * @param args
     *                 first argument is the data directory
     */
    public static void launch(String... args) {
        Application.launch(args);
    }

    private Optional<Path> firstParameter() {
        return Optional.ofNullable(getParameters()) //
                .map(Parameters::getRaw) //
                .map(List::getFirst) //
                .map(Paths::get);
    }

    @Override
    public void start(Stage stage) {
        data = firstParameter().map(Data::new).orElseGet(Data::new);
        monthSummary = new MonthSummary(data);

        stage.setTitle("Running");

        var root = new BorderPane();
        root.setTop(menu());
        root.setCenter(new MonthCalendar(this::mkDayBox, this::mkWeekBox, monthSummary));

        var sideBox = new SplitPane();
        sideBox.setOrientation(Orientation.VERTICAL);
        sideBox.getItems().addAll(dayDetails, monthSummary);
        sideBox.setDividerPositions(0.5);

        root.setRight(sideBox);

        Scene scene = new Scene(root, 900, 500);

        stage.setScene(scene);
        stage.show();
        primary = stage;
    }

    private Node mkDayBox(LocalDate day, boolean active) {
        List<Activity> runs = data.runs() //
                .filter(run -> run.start().toLocalDate().equals(day)) //
                .collect(Collectors.toList());

        var dist = runs.stream() //
                .filter(run -> run.start().toLocalDate().equals(day)) //
                .mapToDouble(run -> run.dist()) //
                .sum() / 1000;

        var time = Duration.ofSeconds(data.runs() //
                .filter(run -> run.start().toLocalDate().equals(day)) //
                .flatMap(Activity::laps) //
                .mapToLong(Lap::seconds) //
                .sum());

        var text = dist > 0.001 //
                ? String.format("%d\n%.3f km\n%s", day.getDayOfMonth(), dist, format(time))
                : String.format("%d\n --", day.getDayOfMonth());

        var rectangle = new Rectangle(80, 50);
        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(active ? Color.BLACK : Color.GRAY);

        var textBox = new Text(text);
        textBox.setFill(active ? Color.BLACK : Color.GRAY);

        var box = new StackPane();
        box.getChildren().addAll(rectangle, textBox);
        box.setOnMouseClicked(e -> {
            dayDetails.setRun(runs.stream().findFirst().orElse(null));
        });
        return box;
    }

    private String format(Duration d) {
        return String.format("%02d:%02d:%02d", d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart());
    }

    private Node mkWeekBox(LocalDate day) {
        Predicate<LocalDate> btw = d -> !d.isBefore(day) && !d.isAfter(day.plusDays(6));
        var dist = data.runs() //
                .filter(run -> btw.test(run.start().toLocalDate())) //
                .mapToDouble(run -> run.dist()) //
                .sum() / 1000;

        var time = Duration.ofSeconds(data.runs() //
                .filter(run -> btw.test(run.start().toLocalDate())) //
                .flatMap(Activity::laps) //
                .mapToLong(Lap::seconds) //
                .sum());

        int weekOfYear = day.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        var text = dist > 0.001 //
                ? String.format("%d\n%.3f km\n%s", weekOfYear, dist, format(time))
                : String.format("%d\n--", weekOfYear);

        var rectangle = new Rectangle(80, 50);
        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(Color.BLACK);

        var textBox = new Text(text);
        textBox.setFill(Color.BLACK);

        var box = new StackPane();
        box.getChildren().addAll(rectangle, textBox);
        return box;
    }

    private MenuBar menu() {
        MenuItem[] items = { //
                item("Read Data", a -> readData()) //
        };

        var menuBar = new MenuBar();
        var menu = new Menu("File");

        menuBar.getMenus().addAll(menu);
        menu.getItems().addAll(items);

        return menuBar;
    }

    private MenuItem item(String text, EventHandler<ActionEvent> action) {
        var it = new MenuItem(text);
        it.setOnAction(action);
        return it;
    }

    /**
     * Select directory and read the data.
     */
    private void readData() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primary);
        if (selectedDirectory != null) {
            data = new Data(selectedDirectory.toPath());
            monthSummary = new MonthSummary(data);
        }
    }

}
