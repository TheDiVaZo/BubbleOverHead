package me.thedivazo.bubbleoverhead.common;

import me.thedivazo.bubbleoverhead.common.math.Vector;

public interface Bubble {
    Message message();
    void setMessage(Message message);
    void show(Viewer viewer);
    void update();
    void hide(Viewer viewer);
    void hideAll();
    Vector location();
    void setLocation(Vector vector);
}
