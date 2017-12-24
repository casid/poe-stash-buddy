package org.jusecase.poe.plugins;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InputPluginTrainer implements InputPlugin {

    private List<Point> clicksWithControlPressed = new ArrayList<>();

    @Override
    public void clickWithControlPressed(int x, int y) {
        clicksWithControlPressed.add(new Point(x, y));
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
}