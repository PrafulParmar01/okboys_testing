package com.ok.boys.delivery.base.api.network;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RetryWithDelay implements Function<Flowable<Throwable>, Publisher<?>> {

    private final int maxRetries;
    private final long retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
    }

    @Override
    public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
        return throwableFlowable.flatMap((Function<Throwable, Publisher<?>>) throwable -> {
            if (++retryCount < maxRetries) {
                return Flowable.timer(retryDelayMillis,
                        TimeUnit.MILLISECONDS);
            }
            return Flowable.error(throwable);
        });
    }
}