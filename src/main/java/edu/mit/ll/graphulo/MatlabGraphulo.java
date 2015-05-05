package edu.mit.ll.graphulo;

import edu.mit.ll.graphulo.mult.LongMultiply;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.iterators.user.BigDecimalCombiner;
import org.apache.accumulo.core.iterators.user.SummingCombiner;
import org.apache.log4j.xml.DOMConfigurator;

import java.util.Collection;

/**
 * Matlab interface to Graphulo.
 */
public class MatlabGraphulo extends Graphulo {

  static {
    // load log4j once, when this class is loaded
    DOMConfigurator.configure(MatlabGraphulo.class.getClassLoader().getResource("log4j.xml"));
  }

  public MatlabGraphulo(String instanceName, String zookeepers, String username, String password)
      throws AccumuloSecurityException, AccumuloException {
    super(new ZooKeeperInstance(instanceName, zookeepers).getConnector(username, new PasswordToken(password)), new PasswordToken(password));
  }

  public void TableMult(String ATtable, String Btable, String Ctable) {
    TableMult(ATtable, Btable, Ctable, 250000, true);
  }

  public void TableMult(String ATtable, String Btable, String Ctable, String rowFilter, String colFilterAT, String colFilterB) {
    TableMult(ATtable, Btable, Ctable, rowFilter, colFilterAT, colFilterB, 250000, true);
  }

  public void TableMult(String ATtable, String Btable, String Ctable, int numEntriesCheckpoint, boolean trace) {
    TableMult(ATtable, Btable, Ctable, null, null, null, 250000, true);
  }

  public void TableMult(String ATtable, String Btable, String Ctable,
                        String rowFilter, String colFilterAT, String colFilterB,
                        int numEntriesCheckpoint, boolean trace) {
    Collection<Range> rowFilterRanges =
      rowFilter != null && !rowFilter.isEmpty() ? GraphuloUtil.d4mRowToRanges(rowFilter) : null;


    TableMult(ATtable, Btable, Ctable,
        //BigDecimalMultiply.class, BigDecimalCombiner.BigDecimalSummingCombiner.class,
        LongMultiply.class, new IteratorSetting(1, "sum", BigDecimalCombiner.BigDecimalSummingCombiner.class),
        rowFilterRanges, colFilterAT, colFilterB, numEntriesCheckpoint, trace);
  }



}
