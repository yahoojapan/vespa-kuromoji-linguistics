/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.kuromoji;

import com.yahoo.language.Language;
import com.yahoo.language.LinguisticsCase;
import com.yahoo.language.process.Normalizer;
import com.yahoo.language.process.StemMode;
import com.yahoo.language.process.Token;
import com.yahoo.language.process.Tokenizer;
import com.yahoo.language.process.Transformer;

import jp.co.yahoo.vespa.language.lib.exception.NormalizationException;
import jp.co.yahoo.vespa.language.lib.util.JapaneseNormalizer;
import jp.co.yahoo.vespa.language.lib.util.JapaneseTokenScript;
import jp.co.yahoo.vespa.language.lib.util.JapaneseTokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Vespa wrapper of Kuromoji tokenizer.
 */
public class KuromojiTokenizer implements Tokenizer {

  private KuromojiContext context;

  private Normalizer normalizer;
  private Transformer transformer;

  private Tokenizer fallback;
  private com.atilika.kuromoji.ipadic.Tokenizer tokenizer;

  /**
   * Create KuromojiTokenizer.
   */
  public KuromojiTokenizer(KuromojiContext context, Normalizer normalizer, Transformer transformer, Tokenizer fallback)
      throws IOException {
    this.context = context;
    this.normalizer = normalizer;
    this.transformer = transformer;
    this.fallback = fallback;

    tokenizer = context.createTokenizer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<Token> tokenize(String input, Language language, StemMode stemMode, boolean removeAccents) {
    if (input.isEmpty()) {
      return Collections.emptyList();
    }

    if (!context.isAllLanguage() && !language.equals(Language.JAPANESE)) {
      return fallback.tokenize(input, language, stemMode, removeAccents);
    }

    // normalize input before tokenizing
    JapaneseNormalizer.Result result = null;
    try {
      // XXX: is it OK to transform input before tokenization?
      result = JapaneseNormalizer.normalizeByNFKC(context.isIgnoreCase() ? LinguisticsCase.toLowerCase(input) : input);
    } catch (NormalizationException e) {
      // fallback to original input
      // XXX: is it better to output error log?
      result = new JapaneseNormalizer.Result(input);
      result.reset();
    }

    List<Token> tokens = new ArrayList<>();
    int normOffset = 0;
    for (com.atilika.kuromoji.ipadic.Token t : tokenizer.tokenize(result.norm)) {
      if (t.getSurface().isEmpty()) {
        continue;
      }

      String orig = getOrig(t, input, normOffset, result);
      String tokenString = processToken(t, language, stemMode, removeAccents);

      if (tokenString.isEmpty()) {
        continue;
      }

      // @formatter:off
      tokens.add(new KuromojiToken.Builder(orig).tokenString(tokenString)
                     // XXX: we only consider head character
                     .type(JapaneseTokenType.valueOf(Character.codePointAt(tokenString, 0)))
                     .script(JapaneseTokenScript.valueOf(Character.codePointAt(tokenString, 0)))
                     .specialToken(context.isSpecialToken(t.getSurface()))
                     .offset(result.origIndexes[normOffset]).build());
      // @formatter:on

      normOffset += t.getSurface().length();
    }

    return tokens;
  }

  private String getOrig(com.atilika.kuromoji.ipadic.Token t, String input, int normOffset,
                         JapaneseNormalizer.Result result) {
    int endNormOffset = normOffset + t.getSurface().length();
    while (result.origIndexes[normOffset] == result.origIndexes[endNormOffset]) {
      // for cases like CJK Compatibility
      ++endNormOffset;
    }

    return input.substring(result.origIndexes[normOffset], result.origIndexes[endNormOffset]);
  }

  private String processToken(com.atilika.kuromoji.ipadic.Token t, Language language, StemMode stemMode,
                              boolean removeAccents) {
    String input = t.getBaseForm();
    if (stemMode == StemMode.NONE || "*".equals(input)) {
      // XXX: sometime, base form will be "*" (maybe UNKNOWN)
      input = t.getSurface();
    }
    input = normalizer.normalize(input);
    // we already transformed before tokenization if necessary
    //        input = LinguisticsCase.toLowerCase(input);
    if (removeAccents) {
      input = transformer.accentDrop(input, language);
    }
    return input;
  }

}
