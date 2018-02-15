package org.jusecase.poe.plugins;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InputPluginTrainer implements InputPlugin {

    private List<Point> clicks = new ArrayList<>();
    private List<Point> clicksWithControlPressed = new ArrayList<>();
    private List<String> events = new ArrayList<>();

    @Override
    public void wait(int millis) {
    }

    @Override
    public void waitDefaultTime() {
    }

    @Override
    public void waitDefaultTime(int factor) {
    }

    @Override
    public void click(int x, int y) {
        clicks.add(new Point(x, y));
        events.add("click(" + x + ", " + y + ")");
    }

    @Override
    public void rightClick(int x, int y) {
        events.add("right click(" + x + ", " + y + ")");
    }

    @Override
    public void clickWithControlPressed(int x, int y) {
        clicksWithControlPressed.add(new Point(x, y));
    }

    @Override
    public void pressShift() {
        events.add("shift pressed");
    }

    @Override
    public void releaseShift() {
        events.add("shift released");
    }

    public void thenNoClickWithControlPressed() {
        assertThat(clicksWithControlPressed).isEmpty();
    }

    public void thenClickedWithControlPressedCountIs(int count) {
        assertThat(clicksWithControlPressed).hasSize(count);
    }

    public void thenClickedWithControlPressedAt(int x, int y) {
        thenClickedWithControlPressedCountIs(1);
        thenClickedWithControlPressedAt(0, x, y);
    }

    public void thenClickedWithControlPressedAt(int clickIndex, int x, int y) {
        Point click = clicksWithControlPressed.get(clickIndex);
        assertThat(click.x).isEqualTo(x);
        assertThat(click.y).isEqualTo(y);
    }

    public void thenClicked(int clickIndex, Point point) {
        assertThat(clicks.get(clickIndex)).isEqualTo(point);
    }

    public void thenEventsAre(String ... expected) {
        assertThat(events).containsExactly(expected);
    }
}