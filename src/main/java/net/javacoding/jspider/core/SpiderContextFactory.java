package net.javacoding.jspider.core;

import net.javacoding.jspider.api.event.EventSink;
import net.javacoding.jspider.core.dispatch.EventDispatcher;
import net.javacoding.jspider.core.dispatch.impl.EventDispatcherImpl;
import net.javacoding.jspider.core.impl.*;
import net.javacoding.jspider.core.storage.Storage;
import net.javacoding.jspider.core.storage.StorageFactory;
import net.javacoding.jspider.core.throttle.ThrottleFactory;
import net.javacoding.jspider.core.util.config.*;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * $Id: SpiderContextFactory.java,v 1.12 2003/04/03 15:57:14 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public class SpiderContextFactory {

	private static Map<URL, SpiderContext> map = new ConcurrentHashMap();

	public static SpiderContext getSpiderContext(URL baseURL) {
		if (map.get(baseURL) == null) {
			synchronized (SpiderContextFactory.class) {
				if (map.get(baseURL) == null) {
					map.put(baseURL, createContext(baseURL));
				}
			}
		}
		return map.get(baseURL);
	}

	private static SpiderContext createContext(URL baseURL) {

		EventSink[] sinks = new PluginFactory().createPlugins();
		PropertySet props = ConfigurationFactory.getConfiguration().getPluginsConfiguration();
		PropertySet filterProps = new MappedPropertySet(ConfigConstants.CONFIG_FILTER, props);
		EventDispatcher dispatcher = new EventDispatcherImpl("Global Event Dispatcher", sinks, filterProps);
		dispatcher.initialize();

		Storage storage = StorageFactory.createStorage();
		ThrottleFactory throttleFactory = new ThrottleFactory();

		SpiderContext context = new SpiderContextImpl(baseURL, dispatcher, throttleFactory, storage);

		return context;
	}
}
