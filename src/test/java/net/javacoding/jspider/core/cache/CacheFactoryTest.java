/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.cache;

import junit.framework.TestCase;
import net.javacoding.jspider.core.task.impl.SchedulerImpl;

/**
 *
 * @author changshu.li
 */
public class CacheFactoryTest extends TestCase {

	public void testGetCommonCache() {
		System.out.println("getCommonCache");
		int size = 20000;
		CommonCache result = CacheFactory.getCommonCache();
//		assertNull(result.getCacheSerializable("test-" + 1));
		for (int i = 0; i < size; i++) {
			result.cache("test-" + i, "1");
			assertNotNull(result.getCacheSerializable("test-" + i));
		}

		for (int i = 0; i < size; i++) {
			assertNotNull(result.getCacheSerializable("test-" + i));
		}
	}

	public void _testSchedulerImpl(){
		CommonCache cc = CacheFactory.getCommonCache();
		cc.cache("test-", 1);
		for(int i=1; i<100; i++){
			System.err.print(i + ",   ");
			assertNotNull(cc.getCacheSerializable(SchedulerImpl.CACHE_PREFIX + i));
		}
	}
}
