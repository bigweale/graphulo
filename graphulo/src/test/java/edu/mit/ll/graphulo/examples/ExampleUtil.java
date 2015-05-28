package edu.mit.ll.graphulo.examples;

import edu.mit.ll.graphulo.d4m.D4MTripleFileWriter;
import org.apache.accumulo.core.client.Connector;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Utility functions used by examples.
 */
public class ExampleUtil {

  /** Reads files from src/test/resource/data and inserts into Accumulo using D4M Schema table+transpose+degree. */
  public static void ingestSCALE(int SCALE, String baseName, Connector conn) throws FileNotFoundException {
    D4MTripleFileWriter tripleFileWriter = new D4MTripleFileWriter(conn);
    URL url = Thread.currentThread().getContextClassLoader().getResource("data/"+SCALE+"r.txt");
    Assert.assertNotNull(url);
    File rowFile = new File(url.getPath());
    url = Thread.currentThread().getContextClassLoader().getResource("data/"+SCALE+"c.txt");
    Assert.assertNotNull(url);
    File colFile = new File(url.getPath());

    // deleteExistingTables
    long cnt = tripleFileWriter.writeTripleFile(rowFile, colFile, null, ",", baseName, true, false);
    System.out.println("Wrote "+cnt+" triples to D4M tables with base name "+baseName);
  }

}
