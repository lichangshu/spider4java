/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.cache;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Not thread safe !!
 *
 * @author changshu.li
 * @param <T>
 */
public class CacheStack<T extends Serializable> {

	// form zero
	private final AtomicInteger index = new AtomicInteger(-1);
	private final String prefix;
	private final CommonCache cache;

	public CacheStack() {
		this(CacheFactory.getCommonCache());
	}

	public CacheStack(CommonCache cache) {
		this.prefix = UUID.randomUUID().toString();
		this.cache = cache;
	}

	public String push(T s) {
		String key = prefix + index.incrementAndGet();
		cache.cache(key, s);
		return key;
	}

	public T peek() {
		String key = prefix + index.get();
		return (T) cache.getCacheSerializable(key);
	}

	public T pop() {
		String key = prefix + index.getAndDecrement();
		return (T) cache.getCacheSerializable(key);
	}

	public int size() {
		return index.get() + 1;
	}

	public boolean isEmpty() {
		return index.get() < 0;
	}

	public String prefix() {
		return prefix;
	}
}
