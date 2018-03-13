/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.util;

import jp.co.yahoo.vespa.language.lib.exception.NormalizationException;

/**
 * Normalize utilities for Japanese text.
 */
public class JapaneseNormalizer {

  public static final int MAX_SUB_LENGTH = 255;

  public static class Result {
    public final String norm;
    // normIndex => origIndex
    public final int[] origIndexes;

    public Result(String norm) {
      this.norm = norm;
      this.origIndexes = new int[norm.length() + 1];
    }

    /**
     * Reset indexes.
     */
    public void reset() {
      for (int i = 0; i < norm.length() + 1; ++i) {
        origIndexes[i] = i;
      }
    }
  }

  /**
   * NFKC normalization with orig<=>norm validation.
   *
   * <p>This method normalizes input text and also
   * checks index mapping between original and normalized text.
   *
   * <p>With check, this method considers Japanese half-mark sound,
   * i.e., HANKAKU KATAKANA.
   *
   * @param input target text
   * @return normalization result as {@link Result} object
   * @throws NormalizationException thrown when normalization failed, mostly by some validation issues.
   */
  public static Result normalizeByNFKC(String input) throws NormalizationException {
    String norm = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFKC);
    Result result = new Result(norm);

    // check gaps between raw and norm input by site-by-side check
    int ri = 0, ni = 0;
    while (ri < input.length() && ni < norm.length()) {
      int rc = Character.codePointAt(input, ri);
      int nc = Character.codePointAt(norm, ni);

      if (rc == nc) {
        result.origIndexes[ni] = ri;
        ++ri;
        ++ni;
        continue;
      }

      int rl = 0;
      String subNorm = "";
      do {
        // increment length
        ++rl;

        // exceed input
        if (ri + rl > input.length() || rl > MAX_SUB_LENGTH) {
          throw new NormalizationException(
              String.format("exceed original length during validation: \"%s\" => \"%s\"", input, norm));
        }

        // normalize sub string
        subNorm = java.text.Normalizer.normalize(input.substring(ri, ri + rl), java.text.Normalizer.Form.NFKC);

        // exceed norm
        if (ni + subNorm.length() > norm.length()) {
          throw new NormalizationException(
              String.format("exceed normalied length during validation: \"%s\" => \"%s\"", input, norm));
        }
      } while (!subNorm.equals(norm.substring(ni, ni + subNorm.length())));

      if (subNorm.equals(norm.substring(ni, ni + subNorm.length()))) {
        // save norm=>orig correspondence
        for (int i = 0; i < subNorm.length(); ++i) {
          result.origIndexes[ni + i] = ri;
        }
        ri += rl;
        ni += subNorm.length();
      }
    }

    if (ri != input.length() || ni != norm.length()) {
      throw new NormalizationException(
          String.format("characters remain or exceed after termination [%d:%d]: \"%s\" => \"%s\"", input.length() - ri,
                        norm.length() - ni, input, norm));
    }

    // guard
    result.origIndexes[ni] = ri;

    return result;
  }
}
