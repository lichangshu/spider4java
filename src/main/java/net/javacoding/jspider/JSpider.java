package net.javacoding.jspider;

import net.javacoding.jspider.core.*;
import net.javacoding.jspider.core.impl.CLI;
import net.javacoding.jspider.core.util.config.ConfigurationFactory;

import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main startup class.
 *
 * $Id: JSpider.java,v 1.27 2003/04/10 16:19:03 vanrogu Exp $
 *
 * @author G�nther Van Roey
 * @todo support commandline input for proxy password
 * @todo implement Swing-based monitor UI ( threading, progress, ...)
 */
public class JSpider {

	private static final Log logger = LogFactory.getLog(JSpider.class);
	protected Spider spider;
	protected SpiderContext context;

	public JSpider(URL baseURL) throws Exception {
		SpiderNest nest = new SpiderNest();
		context = SpiderContextFactory.getSpiderContext(baseURL);
		spider = nest.breedSpider(context);
	}

	public void start() throws Exception {
		spider.crawl(context);
	}

	public SpiderContext getContext() {
		return context;
	}

	public static void main(String[] args) throws Exception {

		Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
		if (handler == null) {
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

				@Override
				public void uncaughtException(Thread t, Throwable e) {
					logger.error(String.format("Thread runtime exception %s, %d !", t.getName(), t.getId()), e);
				}
			});
		}

		CLI.printSignature();

		if (args.length != 1 && args.length != 2) {
			System.out.println("Usage: JSpider baseURL [config]");
			return;
		}

		if (args.length > 1) {
			ConfigurationFactory.getConfiguration(args[1]);
		} else {
			ConfigurationFactory.getConfiguration();
		}

		URL baseURL = new URL(args[0]);

		JSpider jspider = new JSpider(baseURL);
		jspider.start();
	}

}
