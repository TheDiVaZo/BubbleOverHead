package me.thedivazo.bubbleoverhead.common;

public interface BubbleFactory<T> {
    Bubble create(T body);
}
