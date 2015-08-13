/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.cache;

import java.io.Serializable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author changshu.li
 */
public class CacheFactory {

	private static final Log logger = LogFactory.getLog(CacheFactory.class);

	/**
	 * 一个小时过期
	 *
	 * @return
	 */
	public static CommonCache getCommonCache() {
		return CommonCacheImp.ccache;
	}

	public static class CommonCacheImp implements CommonCache {

		private static CommonCacheImp ccache = new CommonCacheImp();

		private Cache mcache;

		public CommonCacheImp() {
			try {
				DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
				CacheConfiguration conf = new CacheConfiguration("disk_cache_task", 5);
				conf.setMaxEntriesLocalHeap(20);// in memory
				conf.setMaxEntriesLocalDisk(Integer.MAX_VALUE);
				conf.setEternal(true);// never expired
				conf.setDiskPersistent(false);
				Configuration cf = new Configuration();
				cf.addDiskStore(diskStoreConfiguration);
				cf.addCache(conf);
				CacheManager manager = CacheManager.create(cf);
				this.mcache = manager.getCache("disk_cache_task");
			} catch (CacheException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public void cache(String key, Serializable o) {
			Element ele = new Element(key, o);
			mcache.put(ele);
		}

		@Override
		public void cache(String key, String o) {
			this.cache(key, (Serializable) o);
		}

		@Override
		public String getCacheString(String key) {
			return (String) this.getCacheSerializable(key);
		}

		@Override
		public Serializable getCacheSerializable(String key) {
			try {
				Element ele = this.mcache.get(key);
				if (ele == null) {
					return null;
				}
				return (Serializable) ele.getObjectValue();
			} catch (Exception ex) {
				CacheFactory.logger.error(ex);
				return null;
			}
		}

		@Override
		public boolean isCached(String key) {
			return null != this.getCacheSerializable(key);
		}

		@Override
		public void removeCache(String key) {
			this.mcache.remove(key);
		}

		@Override
		public void removeAll() {
			this.mcache.removeAll();
		}
	}
}
