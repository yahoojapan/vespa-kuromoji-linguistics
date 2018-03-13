/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.exception;

/**
 * A exception thrown when normalization failed.
 */
public class NormalizationException extends Exception {

  private static final long serialVersionUID = -5298493111077410346L;

  public NormalizationException() {
    super();
  }

  public NormalizationException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public NormalizationException(String message, Throwable cause) {
    super(message, cause);
  }

  public NormalizationException(String message) {
    super(message);
  }

  public NormalizationException(Throwable cause) {
    super(cause);
  }

}
