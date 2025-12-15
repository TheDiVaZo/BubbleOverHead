package me.thedivazo.bubbleoverhead.common;

public interface Bubble {
    Message message();
    void show(Viewer viewer);
    void update();
    void hide(Viewer viewer);
    void hideAll();
}
