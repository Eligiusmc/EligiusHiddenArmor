package com.makrozai.eligiushiddenarmor.domain.port;

public interface PlatformPort {
    void runTask(Runnable task);
    void runTaskAsync(Runnable task);
    void runTaskLater(Runnable task, long delay);
    void runTaskTimer(Runnable task, long delay, long period);
    boolean isFolia();
}
