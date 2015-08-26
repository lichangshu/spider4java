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
	private static final CacheManager manager;
	private static CommonCache ccache;

	static {
		CacheConfiguration cache = new CacheConfiguration("disk_cache_task", 5);
		cache.setEternal(true);// never expired
		cache.setDiskPersistent(false);
		cache.setOverflowToDisk(true);
		Configuration config = new Configuration();
		DiskStoreConfiguration disk = new DiskStoreConfiguration();
		config.addDiskStore(disk);
		config.addCache(cache);
		config.setDefaultCacheConfiguration(new CacheConfiguration("default", 1));
		manager = new CacheManager(config);
	}

	public static CommonCache getCommonCache() {
		synchronized (CacheFactory.class) {
			if (ccache == null) {
				ccache = new CommonCacheImp(manager);
			}
		}
		return ccache;
	}

	public static void shutdown() {
		manager.shutdown();
	}

	public static class CommonCacheImp implements CommonCache {

		private Cache mcache;

		private CommonCacheImp(CacheManager manager) {
			try {
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
