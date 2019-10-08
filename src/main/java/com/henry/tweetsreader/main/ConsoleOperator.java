package com.henry.tweetsreader.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Objects;

/**
 * The wrapper of System.in, System.out.
 * Doing this to make class using System.in, System.out testable.
 */
public class ConsoleOperator {
  private final BufferedReader reader;
  private final PrintWriter writer;

  /**
   * constructor of console operator.
   * @param reader Reader
   * @param writer PrintWriter
   */
  public ConsoleOperator(Reader reader, PrintWriter writer) {
    Objects.requireNonNull(reader, "reader can not be null");
    Objects.requireNonNull(writer, "writer can not be null");

    this.reader = new BufferedReader(reader);
    this.writer = writer;
  }

  public String readLine() throws IOException {
    return reader.readLine();
  }

  public void printf(String str, Object ... objects) {
    writer.printf(str, objects);
    writer.flush();
  }
}
