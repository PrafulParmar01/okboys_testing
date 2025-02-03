package com.psp.google.direction;

import com.psp.google.direction.model.Direction;


public interface DirectionCallback {
    void onDirectionSuccess(Direction direction);
    void onDirectionFailure(Throwable t);
}
