package net.zomis.minesweeper.analyze;

public class NoInterrupt implements InterruptCheck {
    @Override
    public boolean isInterrupted() {
            return false;
        }
}

