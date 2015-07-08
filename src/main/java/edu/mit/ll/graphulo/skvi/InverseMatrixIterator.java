package edu.mit.ll.graphulo.skvi;

import com.google.common.base.Preconditions;
import edu.mit.ll.graphulo.util.MatrixUtil;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.mock.IteratorAdapter;
import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Matrix inverse. Values interpreted as doubles, encoded as Strings.
 * Designed to be used at full major compaction so that it sees all the entries.
 */
public class InverseMatrixIterator implements SortedKeyValueIterator<Key,Value> {
  private static final Logger log = LogManager.getLogger(MapIterator.class);

  public static final String MATRIX_SIZE = "matrixSize";

  public static IteratorSetting iteratorSetting(int priority, int matrixSize) {
    IteratorSetting itset = new IteratorSetting(priority, InverseMatrixIterator.class);
    itset.addOption(MATRIX_SIZE, Integer.toString(matrixSize));
    return itset;
  }

  private SortedKeyValueIterator<Key,Value> source, mapIterator;
  private int matrixSize;

  @Override
  public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options, IteratorEnvironment env) throws IOException {
    this.source = source;
    Preconditions.checkArgument(options.containsKey(MATRIX_SIZE));
    matrixSize = Integer.parseInt(options.get(MATRIX_SIZE));
  }

  @Override
  public SortedKeyValueIterator<Key, Value> deepCopy(IteratorEnvironment env) {
    return new InverseMatrixIterator();
  }

  @Override
  public void seek(Range range, Collection<ByteSequence> columnFamilies, boolean inclusive) throws IOException {
    if (!range.isInfiniteStartKey() || !range.isInfiniteStopKey())
      log.warn("Range is not infinite: "+range);
    source.seek(range, columnFamilies, inclusive);

    IteratorAdapter ia = new IteratorAdapter(source);
    SortedMap<Key, Value> map = MatrixUtil.matrixToMap(new TreeMap<Key, Value>(),
        MatrixUtil.doInverse(MatrixUtil.buildMatrix(ia, matrixSize)));

    mapIterator = new MapIterator(map);
    mapIterator.init(null, null, null);
    mapIterator.seek(range, columnFamilies, inclusive);
  }

  @Override
  public Key getTopKey() {
    return mapIterator.getTopKey();
  }

  @Override
  public Value getTopValue() {
    return mapIterator.getTopValue();
  }

  @Override
  public boolean hasTop() {
    return mapIterator.hasTop();
  }

  @Override
  public void next() throws IOException {
    mapIterator.next();
  }




}
