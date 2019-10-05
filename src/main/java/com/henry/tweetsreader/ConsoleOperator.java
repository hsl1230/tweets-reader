package com.henry.tweetsreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Objects;

public class ConsoleOperator {
  private final BufferedReader reader;
  private final PrintWriter writer;

  public ConsoleOperator(Reader reader, PrintWriter writer) {
    Objects.requireNonNull(reader, "reader can not be null");
    Objects.requireNonNull(writer, "writer can not be null");

    this.reader = new BufferedReader(reader);
    this.writer = writer;
  }

  public String readLine() throws IOException {
    return reader.readLine();
  }

  public void print(String str) throws IOException {
    writer.print(str);
    writer.flush();
  }
}
