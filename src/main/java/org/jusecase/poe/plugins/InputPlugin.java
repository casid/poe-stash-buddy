package org.jusecase.poe.plugins;

public interface InputPlugin {
    int DELAY_AFTER_CLICK = 10;

    void wait(int millis);
    void waitDefaultTime();
    void waitDefaultTime(int factor);
    void click(int x, int y);
    void clickWithControlPressed(int x, int y);
}
