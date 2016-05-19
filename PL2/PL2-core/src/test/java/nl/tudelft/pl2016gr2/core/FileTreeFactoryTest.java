package nl.tudelft.pl2016gr2.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileTreeFactoryTest {

  private static final String filename = "test_tree.nwk";

  private File file;

  /**
   * Since the GfaReader uses files, we need to copy the resource to a file.
   *
   * @throws IOException When creation of tempfile fails.
   */
  @Before
  public void setup() throws IOException {
    file = File.createTempFile("GfaReaderTest", filename);

    FileUtils.copyInputStreamToFile(
        FileTreeFactoryTest.class.getClassLoader().getResourceAsStream(filename),
        file);

  }

  @After
  public void tearDown() {
    file.delete();
  }

  @Test
  public void testGet() throws FileNotFoundException {
    FileTreeFactory factory = spy(new FileTreeFactory(file));
    TreeParser treeParser = spy(
        new TreeParser(new BufferedReader(new InputStreamReader(new FileInputStream(file)))));
    Tree tree = new Tree();

    doReturn(treeParser).when(factory).getTreeParser(any());
    when(treeParser.tokenize(any())).thenReturn(tree);

    Tree actual = factory.getTree();
    Tree expected = tree;
    assertEquals(expected, actual);
  }

}