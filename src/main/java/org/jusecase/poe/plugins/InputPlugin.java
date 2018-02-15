package org.jusecase.poe.plugins;

public interface InputPlugin {
    void wait(int millis);
    void waitDefaultTime();
    void waitDefaultTime(int factor);
    void click(int x, int y);
    void rightClick(int x, int y);
    void clickWithControlPressed(int x, int y);
    void pressShift();
    void releaseShift();
}
