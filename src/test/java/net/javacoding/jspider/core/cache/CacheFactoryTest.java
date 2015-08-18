/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.cache;

import junit.framework.TestCase;

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
			result.cache("test-" + i, "https://www.baidu.com/s?wd=fileinputstream&rsv_spt=1&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&oq=ehcache%20%E5%AE%98%E7%BD%91&rsv_t=3334XG8IJ2barFSEW35%2BwRcXFAGXzk9VIvyRmXbOSekPCuRBGkRcN1ep7TEMYyCCDptY&inputT=3470&rsv_pq=f8831cba00016c05&rsv_sug3=56&rsv_sug1=30&bs=ehcache%20%E5%AE%98%E7%BD%91");
			assertNotNull(result.getCacheSerializable("test-" + i));
		}

		for (int i = 0; i < size; i++) {
			assertNotNull(result.getCacheSerializable("test-" + i));
		}
	}
}
