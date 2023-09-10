package com.jovanovicbogdan.auticparkic.config;

import java.time.Duration;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class CustomTaskScheduler extends ThreadPoolTaskScheduler {

  private final Map<String, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

  public void scheduleAtFixedRate(final Runnable task, final Duration period, final String taskId) {
    final ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
    scheduledTasks.put(taskId, future);
  }

  public boolean isTaskScheduled(final String taskId) {
    ScheduledFuture<?> future = scheduledTasks.get(taskId);
    if (future == null) {
      return false;
    }
    return !(future.isCancelled() || future.isDone());
  }

  public boolean cancelScheduledTask(final String taskId) {
    boolean result = false;
    final ScheduledFuture<?> future = scheduledTasks.get(taskId);

    if (null != future) {
      result = future.cancel(true);
    }

    return result;
  }

}
