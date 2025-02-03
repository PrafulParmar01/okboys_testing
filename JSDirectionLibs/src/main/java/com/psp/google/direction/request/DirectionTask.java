package com.psp.google.direction.request;

import com.psp.google.direction.model.Direction;
import retrofit2.Call;


public class DirectionTask {
    private Call<Direction> directionCall;

    public DirectionTask(Call<Direction> directionCall) {
        this.directionCall = directionCall;
    }

    public void cancel() {
        if (directionCall != null && !directionCall.isCanceled()) {
            directionCall.cancel();
        }
    }


    public boolean isFinished() {
        return directionCall != null && directionCall.isExecuted();
    }

    public Call<Direction> toRetrofitCall() {
        return directionCall;
    }
}
