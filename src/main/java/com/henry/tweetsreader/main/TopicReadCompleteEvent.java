package com.henry.tweetsreader.main;

import org.springframework.context.ApplicationEvent;

public class TopicReadCompleteEvent extends ApplicationEvent {
  /**
   * Create a new ApplicationEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public TopicReadCompleteEvent(Object source) {
    super(source);
  }

  public TopicReader getTopicReader() {
    return (TopicReader) getSource();
  }
}
