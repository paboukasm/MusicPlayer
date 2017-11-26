package com.example.heartmatch.functions;

/**
 * TODO
 */
public interface Subscriber<T> {
    void onComplete(T t);
    void onError(Exception e);
}
