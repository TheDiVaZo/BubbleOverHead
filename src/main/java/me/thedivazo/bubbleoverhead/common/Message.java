package me.thedivazo.bubbleoverhead.common;

public interface Message {
    String text();

    interface Editable extends Message {
        void text(String text);
    }
}
