package com.henry.tweetsreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AppUtils {
  private static final Logger LOG = LoggerFactory.getLogger(AppUtils.class);

  /**
   * sleep in milliseconds.
   * @param milliseconds milliseconds
   * @param purpose purpose
   */
  public static void sleep(long milliseconds, String purpose) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ex) {
      LOG.error("sleep interrupted when doing " + purpose, ex);
    }

  }
}
