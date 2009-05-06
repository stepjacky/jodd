// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.db test suite");
		addTestSuite(DbQueryTest.class);
		addTestSuite(DbMiscTest.class);
		addTestSuite(DbTransactionTest.class);
	}

	public static Test suite() {
		return new jodd.db.AllTests();
	}

}