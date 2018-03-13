/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.kuromoji;

import com.yahoo.component.Version;
import com.yahoo.language.lib.kuromoji.KuromojiConfig;
import com.yahoo.vespa.configdefinition.SpecialtokensConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Settings of Kuromoji tokenizer.
 *
 * @see com.atilika.kuromoji.TokenizerBase
 * @see com.atilika.kuromoji.ipadic.Tokenizer
 */
public class KuromojiContext {

  private static final Logger logger = Logger.getLogger(KuromojiContext.class.getName());

  private com.atilika.kuromoji.TokenizerBase.Mode mode;

  private int kanjiLengthThreshold;
  private int kanjiPenalty;

  private int otherLengthThreshold;
  private int otherPenalry;
  private boolean nakaguroSplit;

  private boolean allLanguage;
  private boolean ignoreCase;

  private Map<String, String> specialTokens = new HashMap<>();

  private String userDictContent = "";

  /**
   * Settings of Kuromoji tokenizer.
   */
  public KuromojiContext(KuromojiConfig kuromojiConfig, SpecialtokensConfig specialtokensConfig) {
    try {
      this.mode = com.atilika.kuromoji.TokenizerBase.Mode.valueOf(kuromojiConfig.mode().toUpperCase(Locale.US));
    } catch (IllegalArgumentException | NullPointerException e) {
      logger.log(Level.WARNING, "unknown kuromoji mode, use default:", e);
      this.mode = com.atilika.kuromoji.TokenizerBase.Mode.SEARCH;
    }

    this.kanjiLengthThreshold = kuromojiConfig.kanji().length_threshold();
    this.kanjiPenalty = kuromojiConfig.kanji().penalty();

    this.otherLengthThreshold = kuromojiConfig.other().length_threshold();
    this.otherPenalry = kuromojiConfig.other().penalty();

    this.nakaguroSplit = kuromojiConfig.nakaguro_split();

    this.allLanguage = kuromojiConfig.all_language();
    this.ignoreCase = kuromojiConfig.ignore_case();

    setupSpecialTokens(kuromojiConfig.tokenlist_name(), specialtokensConfig);
    setupUserDict(kuromojiConfig.user_dict());
  }

  private void setupSpecialTokens(String tokenListName, SpecialtokensConfig config) {
    specialTokens.clear();

    for (SpecialtokensConfig.Tokenlist tokenList : config.tokenlist()) {
      if (!tokenList.name().equals(tokenListName)) {
        continue;
      }

      for (SpecialtokensConfig.Tokenlist.Tokens tokens : tokenList.tokens()) {
        specialTokens.put(tokens.token(), tokens.replace());
      }
    }
  }

  private void setupUserDict(String userDict) {
    StringBuilder builder = new StringBuilder();

    // load user dict if specified
    if (userDict.length() > 0) {
      try (Stream<String> stream = Files.lines(getResource(userDict), StandardCharsets.UTF_8)) {
        stream.forEach(line -> {
          builder.append(line);
          builder.append("\n");
        });
      } catch (IOException e) {
        logger.log(Level.SEVERE, "failed to load user dictionary", e);
      }
    }

    // load special tokens as user dict
    for (String token : specialTokens.keySet()) {
      if (isIgnoreCase()) {
        token = token.toLowerCase(Locale.US);
      }

      // XXX: do we need to provide correct reading?
      builder.append(String.join(",", token, token, token, "SpecialToken"));
      builder.append("\n");
    }

    userDictContent = builder.toString();
  }

  private Path getResource(String name) throws IOException {
    if (new File(name).exists()) {
      return Paths.get(name);
    } else {
      URL resource = this.getClass().getClassLoader().getResource(name);
      if (resource == null) {
        throw new IOException("unexisted resource: " + name);
      }
      return Paths.get(resource.getPath());
    }
  }

  /**
   * Create new Kuromoji tokenizer instance with given context.
   *
   * @return new Kuromoji tokenizer
   * @throws IOException failed to load user dictionary
   */
  public com.atilika.kuromoji.ipadic.Tokenizer createTokenizer() throws IOException {
    try (InputStream userDictStream = new ByteArrayInputStream(userDictContent.getBytes(StandardCharsets.UTF_8))) {
      // @formatter:off
      return new com.atilika.kuromoji.ipadic.Tokenizer.Builder()
                    .mode(mode)
                    .kanjiPenalty(kanjiLengthThreshold, kanjiPenalty)
                    .otherPenalty(otherLengthThreshold, otherPenalry)
                    .isSplitOnNakaguro(nakaguroSplit)
                    .userDictionary(userDictStream)
                    .build();
      // @formatter:on
    }
  }

  /**
   * Whether target language is all or only Japanese.
   *
   * @return true if target language is all
   */
  public boolean isAllLanguage() {
    return allLanguage;
  }

  /**
   * Whether tokenizer should ignore a difference between upper case and lower case.
   *
   * @return true if ignore case
   */
  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  /**
   * Whether given token is special token.
   *
   * @param token surface of token
   * @return true if given token is special token
   */
  public boolean isSpecialToken(String token) {
    return specialTokens.containsKey(token);
  }

  /**
   * Return the version of Kuromoji package.
   *
   * @return version of Kuromoji's version
   */
  public Version getKuromojiVersion() {
    // TODO: get version from pom or jar
    return new Version(0, 9, 0);
  }

}
