package com.grandilo.financelearn.callbacks;

public interface DoneCallback<T> {
    void done(T result, Exception e);
}
