package jp.co.yahoo.vespa.language.lib.kuromoji;

import com.yahoo.language.lib.kuromoji.KuromojiConfig;
import com.yahoo.language.process.GramSplitter;
import com.yahoo.vespa.configdefinition.SpecialtokensConfig;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GramSplitterTest {
  private static final GramSplitter gramSplitter;

  static {
    GramSplitter splitter = null;
    try {
      splitter = new KuromojiLinguistics(new KuromojiConfig(new KuromojiConfig.Builder()),
                                         new SpecialtokensConfig(new SpecialtokensConfig.Builder())).getGramSplitter();
    } catch (IOException e) {
    } finally {
      gramSplitter = splitter;
    }
  }

  @Test
  public void testWithSymbols() {
    assertGramSplit("㍻最後の㍉㌔", 2, "[㍻最, 最後, 後の, の㍉, ㍉㌔]");
  }

  @Test
  public void testWithWhiteSpace() {
    assertGramSplit("㍻最後 の ㍉㌔", 2, "[㍻最, 最後, の, ㍉㌔]");
  }

  @Test
  public void testWithIgnoreSymbol() {
    assertGramSplit("㍻最後の©㍉㌔", 2, "[㍻最, 最後, 後の, ㍉㌔]");
  }

  private void assertGramSplit(String input, int gramSize, String expected) {
    assertEquals(gramSplitter.split(input, gramSize).toExtractedList().toString(), expected);
  }
}