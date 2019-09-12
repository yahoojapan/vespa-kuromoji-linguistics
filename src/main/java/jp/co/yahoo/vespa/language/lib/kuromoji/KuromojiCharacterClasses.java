package jp.co.yahoo.vespa.language.lib.kuromoji;

import com.yahoo.language.process.CharacterClasses;

import java.text.Normalizer;

public class KuromojiCharacterClasses extends CharacterClasses {
  @Override
  public boolean isLetter(int c) {
    // see https://github.com/vespa-engine/vespa/blob/master/linguistics/src/main/java/com/yahoo/language/process/CharacterClasses.java
    if (java.lang.Character.isLetter(c)) return true;
    if (Character.isDigit(c) &&  ! isLatin(c)) return true;

    if (c == '\u3008' || c == '\u3009' || c == '\u300a' || c == '\u300b' ||
        c == '\u300c' || c == '\u300d' || c == '\u300e' ||
        c == '\u300f' || c == '\u3010' || c == '\u3011') {
      return true;
    }
    int type = java.lang.Character.getType(c);
    if (type == java.lang.Character.NON_SPACING_MARK ||
           type == java.lang.Character.COMBINING_SPACING_MARK ||
           type == java.lang.Character.ENCLOSING_MARK) {
      return true;
    } else if (type == Character.OTHER_SYMBOL) {
      // OTHER_SYMBOL contains Gengo(Era), letter enclosed within a circle, etc
      String norm = Normalizer.normalize(String.valueOf((char)c), Normalizer.Form.NFKC);
      if (c == norm.charAt(0)) return false;
      return isLetterOrDigit(norm.charAt(0));
    }
    return false;
  }
}
