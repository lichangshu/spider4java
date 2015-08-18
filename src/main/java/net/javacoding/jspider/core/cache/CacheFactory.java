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

	public static CommonCache getCommonCache() {
		return CommonCacheImp.ccache;
	}

	public static class CommonCacheImp implements CommonCache {

		private static CommonCacheImp ccache = new CommonCacheImp();

		private Cache mcache;

		private CommonCacheImp() {
			try {
				CacheConfiguration def = new CacheConfiguration();
				def.setName("default");
				CacheConfiguration cache = new CacheConfiguration();
				cache.setName("disk_cache_task");
				cache.setMaxElementsInMemory(5);// in memory
				cache.setEternal(true);// never expired
				cache.setDiskPersistent(false);
				cache.setOverflowToDisk(true);
				cache.setTimeToIdleSeconds(0);
				cache.setTimeToLiveSeconds(0);
				Configuration config = new Configuration();
				DiskStoreConfiguration disk = new DiskStoreConfiguration();
				disk.setPath("user.dir");
				logger.info("Cache file path :" + disk.getPath());
				config.addDiskStore(disk);
				config.addCache(cache);
				config.setDefaultCacheConfiguration(def);
				CacheManager manager = new CacheManager(config);
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
