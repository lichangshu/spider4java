package net.javacoding.jspider.mockobjects.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * $Id: Counter.java,v 1.1 2002/12/06 19:13:32 vanrogu Exp $
 */
public class Counter {

	protected AtomicInteger count;

	public Counter() {
		count = new AtomicInteger();
	}

	public void increment() {
		count.getAndIncrement();
	}

	public int getValue() {
		return count.get();
	}

}
