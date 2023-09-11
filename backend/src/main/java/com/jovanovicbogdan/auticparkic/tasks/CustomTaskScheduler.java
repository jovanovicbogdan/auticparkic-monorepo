package com.jovanovicbogdan.auticparkic.tasks;

import java.time.Duration;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class CustomTaskScheduler extends ThreadPoolTaskScheduler {

  private static final Logger log = LoggerFactory.getLogger(CustomTaskScheduler.class);
  private final Map<String, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

  public void scheduleAtFixedRate(final Runnable task, final Duration period, final String taskId) {
    log.info("Scheduling task with id: {} and period: {}", taskId, period);
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

  public boolean isTaskRunning(String taskId) {
    ScheduledFuture<?> future = scheduledTasks.get(taskId);
    return future != null && !future.isDone();
  }

  public boolean cancelScheduledTask(final String taskId) {
    boolean result = false;
    log.info("Cancelling task with id: {}", taskId);
    final ScheduledFuture<?> future = scheduledTasks.get(taskId);

    if (null != future) {
      result = future.cancel(true);
    }

    return result;
  }

}
