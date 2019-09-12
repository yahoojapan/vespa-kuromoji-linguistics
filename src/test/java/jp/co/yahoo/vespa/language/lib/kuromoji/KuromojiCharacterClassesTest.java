package jp.co.yahoo.vespa.language.lib.kuromoji;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KuromojiCharacterClassesTest {
  @Test
  public void testSpecialChars() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // is letter
    assertTrue(characterClasses.isLetter('㍻'));
    assertTrue(characterClasses.isLetter('㍉'));
    assertTrue(characterClasses.isLetter('㊑'));
    // isLetterOrDigit is also true
    assertTrue(characterClasses.isLetterOrDigit('㍻'));
    assertTrue(characterClasses.isLetterOrDigit('㍉'));
    // is noise
    assertFalse(characterClasses.isLetter('〒'));
    assertFalse(characterClasses.isLetter('©'));;
  }

  @Test
  public void testQuotes() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // quote is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('"'));
    assertFalse(characterClasses.isLetter('\u201C'));
    assertFalse(characterClasses.isLetter('\u201D'));
    assertFalse(characterClasses.isLetter('\u201E'));
    assertFalse(characterClasses.isLetter('\u201F'));
    assertFalse(characterClasses.isLetter('\u2039'));
    assertFalse(characterClasses.isLetter('\u203A'));
    assertFalse(characterClasses.isLetter('\u00AB'));
    assertFalse(characterClasses.isLetter('\u00BB'));
    assertFalse(characterClasses.isLetter('\u301D'));
    assertFalse(characterClasses.isLetter('\u301E'));
    assertFalse(characterClasses.isLetter('\u301F'));
    assertFalse(characterClasses.isLetter('\uFF02'));
  }

  @Test
  public void testSigns() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // +- is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('-'));
    assertFalse(characterClasses.isLetter('\uFF0D'));
    assertFalse(characterClasses.isLetter('+'));
    assertFalse(characterClasses.isLetter('\uFF0B'));
  }

  @Test
  public void testPunctuations() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // .,:; is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('.'));
    assertFalse(characterClasses.isLetter('\uFF0E'));
    assertFalse(characterClasses.isLetter(','));
    assertFalse(characterClasses.isLetter('\uFF0C'));
    assertFalse(characterClasses.isLetter(':'));
    assertFalse(characterClasses.isLetter('\uFF1A'));
    assertFalse(characterClasses.isLetter(';'));
    assertFalse(characterClasses.isLetter('\uFF1E'));
  }

  @Test
  public void testBrace() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // () is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('('));
    assertFalse(characterClasses.isLetter('\uFF08'));
    assertFalse(characterClasses.isLetter(')'));
    assertFalse(characterClasses.isLetter('\uFF09'));
  }

  @Test
  public void testBracket() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // [] is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('['));
    assertFalse(characterClasses.isLetter('\uFF3D'));
    assertFalse(characterClasses.isLetter(']'));
    assertFalse(characterClasses.isLetter('\uFF1B'));
  }

  @Test
  public void testGraterSmaller() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // <> is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('<'));
    assertFalse(characterClasses.isLetter('\uFF1E'));
    assertFalse(characterClasses.isLetter('>'));
    assertFalse(characterClasses.isLetter('\uFF1C'));
  }

  @Test
  public void testSimbols() {
    KuromojiCharacterClasses characterClasses = new KuromojiCharacterClasses();
    // !_^*$ is not letter
    // please see https://github.com/vespa-engine/vespa/blob/master/container-search/src/main/java/com/yahoo/prelude/query/parser/Tokenizer.java
    assertFalse(characterClasses.isLetter('!'));
    assertFalse(characterClasses.isLetter('\uFF01'));
    assertFalse(characterClasses.isLetter('_'));
    assertFalse(characterClasses.isLetter('\uFF3F'));
    assertFalse(characterClasses.isLetter('^'));
    assertFalse(characterClasses.isLetter('\uFF3E'));
    assertFalse(characterClasses.isLetter('*'));
    assertFalse(characterClasses.isLetter('\uFF0A'));
    assertFalse(characterClasses.isLetter('$'));
    assertFalse(characterClasses.isLetter('\uFF04'));
  }

}