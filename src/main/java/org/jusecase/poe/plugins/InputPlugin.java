package org.jusecase.poe.plugins;

public interface InputPlugin {
    void wait(int millis);
    void click(int x, int y);
    void clickWithControlPressed(int x, int y);
}
