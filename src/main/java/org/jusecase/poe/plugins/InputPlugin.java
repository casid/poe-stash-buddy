package org.jusecase.poe.plugins;

public interface InputPlugin {
    int DELAY = 30;
    int DELAY_AFTER_CLICK = 10;

    void wait(int millis);
    void click(int x, int y);
    void clickWithControlPressed(int x, int y);
}
