package net.javacoding.jspider.extension.rule;

import java.net.URL;
import java.util.regex.Pattern;

import net.javacoding.jspider.api.model.Decision;
import net.javacoding.jspider.api.model.Site;
import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.logging.Log;
import net.javacoding.jspider.core.logging.LogFactory;
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

	public static final String COUNT = "count";
	public static final String PATTERN = "pattern.";

	protected Pattern[] patterns;

	public AcceptPattenUrlOnlyRule(PropertySet config) {
		super();
		int count = config.getInteger(COUNT, 0);
		patterns = new Pattern[count];
		for (int i = 1; i <= count; i++) {
			patterns[i - 1] = Pattern.compile(config.getString(PATTERN + i, "^$"));
		}
		log.info("load pattern size " + patterns.length);
	}

	@Override
	public Decision apply(SpiderContext context, Site site, URL url) {
		for (Pattern skipUrl : patterns) {
			if (skipUrl.matcher(url.getPath()).matches()) {
				log.info("AcceptPattenUrlOnlyRule accept : " + url);
				return new DecisionInternal(Decision.RULE_ACCEPT, "accept pattern rule - so resource is accepted");
			}
		}
		if (context.getBaseURL().equals(url)) {
			log.info("AcceptPattenUrlOnlyRule accept base path : " + url);
			return new DecisionInternal(Decision.RULE_ACCEPT, "url accepted");
		} else {
			log.debug("AcceptPattenUrlOnlyRule Ignore path : " + url);
			return new DecisionInternal(Decision.RULE_IGNORE, "url ignored because it not accept pattern rule");
		}
	}
}
