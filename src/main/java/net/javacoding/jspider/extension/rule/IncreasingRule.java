/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.extension.rule;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.javacoding.jspider.api.model.Decision;
import net.javacoding.jspider.api.model.Site;
import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.event.impl.URLFoundEvent;
import net.javacoding.jspider.core.logging.Log;
import net.javacoding.jspider.core.logging.LogFactory;
import net.javacoding.jspider.core.model.DecisionInternal;
import net.javacoding.jspider.core.rule.impl.BaseRuleImpl;
import net.javacoding.jspider.core.util.URLUtil;
import net.javacoding.jspider.core.util.config.PropertySet;

/**
 *
 * @author changshu.li
 */
public class IncreasingRule extends BaseRuleImpl {

	private static final Log log = LogFactory.getLog(AcceptPattenUrlOnlyRule.class);
	private static final Set<String> set = new ConcurrentSkipListSet();

	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String PATTERN = "pattern";
	public static final String REPLACE = "replace";
	public static final String QUERY_ENABLE = "query.enable";

	protected int from;
	protected int to;
	protected Pattern pattern;
	protected String replace;
	protected boolean queryEnable = false;

	/**
	 * 包含 form 和 to
	 *
	 * @param config
	 */
	public IncreasingRule(PropertySet config) {
		super();
		this.from = config.getInteger(FROM, 0);
		this.to = config.getInteger(TO, 0);
		this.pattern = Pattern.compile(config.getString(PATTERN, null));
		this.replace = config.getString(REPLACE, null);
		this.queryEnable = config.getBoolean(QUERY_ENABLE, false);
		log.info("load pattern " + from + " to " + to);
	}

	@Override
	public Decision apply(SpiderContext context, Site currentSite, URL url) {
		if (!set.contains(url.toString())) {
			String path = url.getPath();
			if (this.queryEnable) {
				if (url.getQuery() != null) {
					path += "?" + url.getQuery();
				}
			}
			Matcher mt = pattern.matcher(path);
			if (mt.matches()) {
				int size = mt.groupCount() + 1;
				Object[] pts = new Object[size + 1];
				for (int i = 0; i < size; i++) {
					pts[i] = mt.group(i);
				}
				for (int i = from; i <= to; i++) {
					log.info(String.format("Increasing page for [%s], from [%d] to [%d]!", url, from, to));
					pts[size] = i;
					String fm = MessageFormat.format(replace, pts);
					try {
						URL find = URLUtil.normalize(new URL(url, fm));
						set.add(find.toString());
						context.getAgent().registerEvent(url, new URLFoundEvent(context, url, find));
						log.debug("Increasing page " + find);
					} catch (MalformedURLException ex) {
						log.error(ex);
					}
				}
			}
		}
		return new DecisionInternal(DecisionInternal.RULE_ACCEPT);
	}

}
