package de.codecentric.opentracing.instana.demo.notebackend;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanReporter;

public class InstanaSpanReporter implements SpanReporter {

  @Override
  public void report(Span span) {

  }
}
