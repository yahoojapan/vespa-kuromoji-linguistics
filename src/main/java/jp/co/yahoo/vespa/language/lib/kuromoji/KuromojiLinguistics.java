/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.kuromoji;

import com.google.inject.Inject;
import com.yahoo.component.Version;
import com.yahoo.language.Linguistics;
import com.yahoo.language.detect.Detector;
import com.yahoo.language.lib.kuromoji.KuromojiConfig;
import com.yahoo.language.process.CharacterClasses;
import com.yahoo.language.process.GramSplitter;
import com.yahoo.language.process.Normalizer;
import com.yahoo.language.process.Segmenter;
import com.yahoo.language.process.SegmenterImpl;
import com.yahoo.language.process.Stemmer;
import com.yahoo.language.process.StemmerImpl;
import com.yahoo.language.process.Tokenizer;
import com.yahoo.language.process.Transformer;
import com.yahoo.language.simple.SimpleLinguistics;
import com.yahoo.vespa.configdefinition.SpecialtokensConfig;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Linguistics with Kuromoji tokenizer.
 *
 * <p>This Linguistics intents to manage Japanese texts.
 *
 * <p>Available configurations are as below.
 * <table>
 * <tr><th>parameter</th><th>default</th><th>description</th></tr>
 * <tr><td>mode</td><td>search</td><td>mode of Kuromoji (normal|search|extended)</td></tr>
 * <tr><td>kanji.length_threshold</td><td>2</td><td>threshold of the length of kanji tokens which is penalized while running the Viterbi search (expert feature).</td></tr>
 * <tr><td>kanji.penalty</td><td>3000</td><td>additional cost for kanji tokens which is longer than the pre-defined length threshold (expert feature).</td></tr>
 * <tr><td>other.length_threshold</td><td>7</td><td>threshold of the length of non-kanji tokens which is penalized while running the Viterbi search (expert feature).</td></tr>
 * <tr><td>other.penalty</td><td>1700</td><td>additional cost for non-kanji tokens which is longer than the pre-defined length threshold (expert feature).</td></tr>
 * <tr><td>nakaguro_split</td><td>false</td><td>whether splits unknown words on the middle dot character (U+30FB KATAKANA MIDDLE DOT)</td></tr>
 * <tr><td>user_dict</td><td>-</td><td>path of user dictionary</td></tr>
 * <tr><td>tokenlist_name</td><td>default</td><td>target specialtokens name</td></tr>
 * <tr><td>all_language</td><td>false</td><td>apply kuromoji tokenizer to all language</td></tr>
 * <tr><td>ignore_case</td><td>true</td><td>ignore upper/lower difference</td></tr>
 * </table>
 */
public class KuromojiLinguistics implements Linguistics {

  private static final Logger logger = Logger.getLogger(KuromojiLinguistics.class.getName());

  private final SimpleLinguistics simpleLinguistics = new SimpleLinguistics();
  private final CharacterClasses characterClasses = new KuromojiCharacterClasses();
  private final GramSplitter gramSplitter = new GramSplitter(characterClasses);

  private KuromojiContext context;
  private Tokenizer tokenizer;

  @Inject
  public KuromojiLinguistics(KuromojiConfig kuromojiConfig, SpecialtokensConfig specialtokensConfig)
      throws IOException {
    this.context = new KuromojiContext(kuromojiConfig, specialtokensConfig);
    this.tokenizer = new KuromojiTokenizer(context, simpleLinguistics.getNormalizer(),
                                           simpleLinguistics.getTransformer(), simpleLinguistics.getTokenizer());
  }

  /**
   * Create KuromojiLinguistics.
   */
  public static Optional<Linguistics> create(KuromojiConfig kuromojiConfig, SpecialtokensConfig specialtokensConfig) {
    try {
      return Optional.of(new KuromojiLinguistics(kuromojiConfig, specialtokensConfig));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "failed to create KuromojiLinguistics, use default instead", e);
      return Optional.empty();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Stemmer getStemmer() {
    return new StemmerImpl(getTokenizer());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Tokenizer getTokenizer() {
    return tokenizer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Normalizer getNormalizer() {
    return simpleLinguistics.getNormalizer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Transformer getTransformer() {
    return simpleLinguistics.getTransformer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Segmenter getSegmenter() {
    return new SegmenterImpl(getTokenizer());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Detector getDetector() {
    return simpleLinguistics.getDetector();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GramSplitter getGramSplitter() {
    return gramSplitter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CharacterClasses getCharacterClasses() {
    return characterClasses;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Linguistics other) {
    return (other instanceof KuromojiLinguistics);
  }
}
