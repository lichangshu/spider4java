package net.javacoding.jspider.mod.plugin.findurl;

import net.javacoding.jspider.api.event.JSpiderEvent;
import net.javacoding.jspider.api.event.resource.ResourceFetchedEvent;
import net.javacoding.jspider.core.logging.Log;
import net.javacoding.jspider.core.logging.LogFactory;
import net.javacoding.jspider.core.util.config.*;
import net.javacoding.jspider.spi.Plugin;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.javacoding.jspider.api.model.HTTPHeader;
import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.event.impl.URLFoundEvent;
import net.javacoding.jspider.core.util.URLUtil;

/**
 * $Id: PutPattenUrlPlugin.java,v 1.13 2003/04/09 17:08:14 vanrogu Exp $
 *
 * $Id: PutPattenUrlPlugin.java,v 1.13 2003/04/09 17:08:14 vanrogu Exp $
 */
public class PutPattenUrlPlugin implements Plugin {

	private static final Log log = LogFactory.getLog(PutPattenUrlPlugin.class);
	public static final String MODULE_NAME = "Find URL JSpider plugin";
	public static final String MODULE_VERSION = "v1.0";
	public static final String MODULE_DESCRIPTION = "A JSpider plugin that and url to spider";
	public static final String MODULE_VENDOR = "http://www.javacoding.net";

	public static final String COUNT = "count";
	public static final String PATTERN = "pattern.";
	public static final String REPLACE = "replace.";
	public static final String PARSER = "parse";
	public static final String QUERY_ENABLE = "query.enable";

	protected Pattern[] patterns;
	protected Pattern parsers;
	protected String[] replaces;
	protected boolean queryEnable = false;

	public PutPattenUrlPlugin(PropertySet config) {
		int count = config.getInteger(COUNT, 0);
		patterns = new Pattern[count];
		replaces = new String[count];
		parsers = Pattern.compile(config.getString(PARSER, "^$"));
		for (int i = 1; i <= count; i++) {
			patterns[i - 1] = Pattern.compile(config.getString(PATTERN + i, "^$"));
			replaces[i - 1] = config.getString(REPLACE + i, "");
		}
		queryEnable = config.getBoolean(QUERY_ENABLE, false);
		log.info("load pattern size " + patterns.length);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public String getVersion() {
		return MODULE_VERSION;
	}

	@Override
	public String getDescription() {
		return MODULE_DESCRIPTION;
	}

	@Override
	public String getVendor() {
		return MODULE_VENDOR;
	}

	@Override
	public void notify(JSpiderEvent event) {
		if (event instanceof ResourceFetchedEvent) {
			ResourceFetchedEvent rfe = (ResourceFetchedEvent) event;
			SpiderContext context = rfe.getSpiderContext();
			URL origin = rfe.getResource().getURL();
			String path = origin.getPath();
			if(queryEnable){
				if(origin.getQuery() != null){
					path += "?" + origin.getQuery();
				}
			}
			if (!parsers.matcher(path).matches()) {
				return;
			}
			InputStream in = rfe.getResource().getInputStream();
			HTTPHeader[] heads = rfe.getResource().getHeaders();
			String charset = Charset.defaultCharset().toString();
			for (HTTPHeader hd : heads) {
				//Content-Type:text/html;charset=GBK
				if ("Content-Type".equals(hd.getName())) {
					int i = hd.getValue().indexOf("charset");
					if (i >= 0) {
						int ii = hd.getValue().indexOf(";", i);
						if (ii < 0) {
							ii = hd.getValue().length();
						}
						String cset = hd.getValue().substring(i, ii);
						String[] sp = cset.split("=");
						if (sp.length == 2) {
							charset = sp[1].trim();
							break;
						}
					}
				}
			}
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(in, charset));
				while (true) {
					String line = rd.readLine();
					if (line == null) {
						break;
					}
					for (int i = 0; i < patterns.length; i++) {
						if (line.length() == 0) {
							continue;
						}
						String[] strs = patternreplace(patterns[i], replaces[i], line);
						for (String fm : strs) {
							URL find = URLUtil.normalize(new URL(origin, fm));
							rfe.getSpiderContext()
									.getAgent()
									.registerEvent(
											origin,
											new URLFoundEvent(context, origin, find));
							log.info("Plug increasing page " + fm);
						}
					}
				}
			} catch (IOException ex) {
				log.error("read url: " + origin, ex);
			}
		}
	}

	public static String[] patternreplace(Pattern pattern, String replace, String str) {
		Matcher mt = pattern.matcher(str);
		List<String> list = new ArrayList();
		while (mt.find()) {
			int size = mt.groupCount() + 1;
			Object[] pts = new Object[size];
			for (int i = 0; i < size; i++) {
				pts[i] = mt.group(i);
			}
			String fm = MessageFormat.format(replace, pts);
			list.add(fm);
		}
		return list.toArray(new String[list.size()]);
	}

}
