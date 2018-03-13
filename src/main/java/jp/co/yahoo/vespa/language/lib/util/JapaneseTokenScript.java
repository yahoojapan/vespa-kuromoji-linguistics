/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.util;

import com.yahoo.language.process.TokenScript;

import java.lang.Character.UnicodeBlock;

/**
 * TokenScript converter for Japanese character.
 */
public class JapaneseTokenScript {

  /**
   * Check and return corresponding TokenScript of given UTF-8 codePoint.
   *
   * @param codePoint codePoint calculated by {@link Character#codePointAt(char[], int)}
   * @return {@link TokenScript} of given codePoint
   */
  public static TokenScript valueOf(int codePoint) {
    UnicodeBlock block = UnicodeBlock.of(codePoint);

    if (block == UnicodeBlock.HIRAGANA) {
      return TokenScript.HIRAGANA;
    } else if (block == UnicodeBlock.KATAKANA) {
      return TokenScript.KATAKANA;
    } else if (block == UnicodeBlock.BASIC_LATIN) {
      return TokenScript.ASCII;
    }

    // XXX: what script should we return with Kanji ?
    return TokenScript.UNKNOWN;
  }
}
