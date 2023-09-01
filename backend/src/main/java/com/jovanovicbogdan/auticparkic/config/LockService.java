package com.jovanovicbogdan.auticparkic.config;

import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class LockService {

  private static final String LOCK_KEY = "lock:key";
  private final LockRegistry lockRegistry;

  public LockService(final JdbcLockRegistry lockRegistry) {
    this.lockRegistry = lockRegistry;
  }

  public String lock() {
    Lock lock = null;

    try {
      lock = lockRegistry.obtain(LOCK_KEY);
    } catch (Exception e) {
      System.out.printf("Unable to obtain lock: %s%n", LOCK_KEY);
    }

    String returnVal = null;
    try {
      if (lock.tryLock()) {
        returnVal = "JDBC Lock Successful.";
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return returnVal;
  }

  public void failLock() {
    var executor = Executors.newFixedThreadPool(2);
    Runnable lockThreadOne = () -> {
      UUID uuid = UUID.randomUUID();
      StringBuilder sb = new StringBuilder();
      var lock = lockRegistry.obtain(LOCK_KEY);
      try {
        System.out.println("Attempting to lock with thread: " + uuid);
        if (lock.tryLock(1, TimeUnit.SECONDS)) {
          System.out.println("Locked with thread: " + uuid);
          Thread.sleep(5000);
        } else {
          System.out.println("failed to lock with thread: " + uuid);
        }
      } catch (Exception e0) {
        System.out.println("exception thrown with thread: " + uuid);
      } finally {
        lock.unlock();
        System.out.println("unlocked with thread: " + uuid);
      }
    };

    Runnable lockThreadTwo = () -> {
      UUID uuid = UUID.randomUUID();
      StringBuilder sb = new StringBuilder();
      var lock = lockRegistry.obtain(LOCK_KEY);
      try {
        System.out.println("Attempting to lock with thread: " + uuid);
        if (lock.tryLock(1, TimeUnit.SECONDS)) {
          System.out.println("Locked with thread: " + uuid);
          Thread.sleep(5000);
        } else {
          System.out.println("failed to lock with thread: " + uuid);
        }
      } catch (Exception e0) {
        System.out.println("exception thrown with thread: " + uuid);
      } finally {
        lock.unlock();
        System.out.println("unlocked with thread: " + uuid);
      }
    };
    executor.submit(lockThreadOne);
    executor.submit(lockThreadTwo);
    executor.shutdown();
  }
}
