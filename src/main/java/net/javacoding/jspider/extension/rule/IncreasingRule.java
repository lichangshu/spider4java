/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.extension.rule;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
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
import net.javacoding.jspider.core.util.config.PropertySet;

/**
 *
 * @author changshu.li
 */
public class IncreasingRule extends BaseRuleImpl {

	private static final Log log = LogFactory.getLog(AcceptPattenUrlOnlyRule.class);

	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String PATTERN = "pattern";
	public static final String REPLACE = "replace";

	protected int from;
	protected int to;
	protected Pattern pattern;
	protected String replace;

	/**
	 *
	 * Content form and to (from <= X <=to) ! MessageFormat.format to format
	 * Data!
	 *
	 * @param config
	 */
	public IncreasingRule(PropertySet config) {
		super();
		this.from = config.getInteger(FROM, 0);
		this.to = config.getInteger(TO, 0);
		this.pattern = Pattern.compile(config.getString(PATTERN, null));
		this.replace = config.getString(REPLACE, null);
		log.info("load pattern " + from + " to " + to);
	}

	@Override
	public Decision apply(SpiderContext context, Site currentSite, URL url) {
		String path = url.getPath();
		Matcher mt = pattern.matcher(path);
		if (mt.matches()) {
			int size = mt.groupCount() + 1;
			Object[] pts = new Object[size + 1];
			for (int i = 0; i < size; i++) {
				pts[i] = mt.group(i);
			}
			for (int i = from; i <= to; i++) {
				pts[size] = i;
				String fm = MessageFormat.format(replace, pts);
				try {
					context.getAgent().registerEvent(url, new URLFoundEvent(context, url, new URL(fm)));
					log.info("Increasing page " + fm);
				} catch (MalformedURLException ex) {
					log.error(ex);
				}
			}
		}
		return new DecisionInternal(DecisionInternal.RULE_ACCEPT);
	}

}
