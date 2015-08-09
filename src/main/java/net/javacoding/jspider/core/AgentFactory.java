/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.javacoding.jspider.core.impl.AgentImpl;

/**
 *
 * @author changshu.li
 */
public class AgentFactory {

	private static Map<SpiderContext, Agent> map = new ConcurrentHashMap();

	public static Agent getAgent(SpiderContext context) {
		if (map.get(context) == null) {
			synchronized (AgentFactory.class) {
				if (map.get(context) == null) {
					map.put(context, new AgentImpl(context));
				}
			}
		}
		return map.get(context);
	}
}
