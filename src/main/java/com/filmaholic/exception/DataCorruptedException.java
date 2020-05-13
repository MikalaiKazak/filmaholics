package com.filmaholic.exception;

public class DataCorruptedException extends RuntimeException {
  public DataCorruptedException(Throwable thr) {
    super(thr);
  }
}
