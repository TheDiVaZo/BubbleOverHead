package me.thedivazo.bubbleoverhead.common;

public interface Message {
    String plainText();

    interface Editable extends Message {
        void plainText(String text);
    }
}
