package net.javacoding.jspider.extension.rule;

import java.net.URL;
import java.util.regex.Pattern;

import net.javacoding.jspider.api.model.Decision;
import net.javacoding.jspider.api.model.Site;
import net.javacoding.jspider.core.SpiderContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.javacoding.jspider.core.model.DecisionInternal;
import net.javacoding.jspider.core.rule.impl.BaseRuleImpl;
import net.javacoding.jspider.core.util.config.PropertySet;

/**
 * Rule for Regular Expressions ! <br/>
 * if match any regular,it is pass (Will be spider or parse) !!<br />
 *
 * Example::<br/>
 *
 * @author changshu.li
 */
public class AcceptPattenUrlOnlyRule extends BaseRuleImpl {

	private static final Log log = LogFactory.getLog(AcceptPattenUrlOnlyRule.class);

	public static final String PATTERN = "pattern";
	public static final String QUERY_ENABLE = "query.enable";

	protected Pattern pattern;
	protected boolean queryEnable = false;

	public AcceptPattenUrlOnlyRule(PropertySet config) {
		super();
		pattern = Pattern.compile(config.getString(PATTERN, "^$"));
		queryEnable = config.getBoolean(QUERY_ENABLE, false);
		log.info("load pattern size " + pattern.toString());
	}

	@Override
	public Decision apply(SpiderContext context, Site site, URL url) {
		String path = url.getPath();
		if (queryEnable) {
			if (url.getQuery() != null) {
				path += "?" + url.getQuery();
			}
		}
		if (pattern.matcher(path).matches()) {
			log.debug("AcceptPattenUrlOnlyRule accept : " + url);
			return new DecisionInternal(Decision.RULE_ACCEPT, "accept pattern rule - so resource is accepted");
		}
		if (context.getBaseURL().equals(url)) {
			log.debug("AcceptPattenUrlOnlyRule accept base path : " + url);
			return new DecisionInternal(Decision.RULE_ACCEPT, "url accepted");
		} else {
			log.debug("AcceptPattenUrlOnlyRule Ignore path : " + url);
			return new DecisionInternal(Decision.RULE_IGNORE, "url ignored because it not accept pattern rule");
		}
	}
}
