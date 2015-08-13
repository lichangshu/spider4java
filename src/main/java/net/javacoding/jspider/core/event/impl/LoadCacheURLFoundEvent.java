package net.javacoding.jspider.core.event.impl;

import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.event.CoreEventVisitor;
import net.javacoding.jspider.api.model.Folder;

import java.net.URL;

/**
 *
 * $Id: URLFoundEvent.java,v 1.5 2003/04/09 17:08:04 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public class LoadCacheURLFoundEvent extends BaseCoreEventImpl {

	public LoadCacheURLFoundEvent(SpiderContext context) {
		super(context);
	}

	public void accept(URL url, CoreEventVisitor visitor) {
		visitor.visit(url, this);
	}

}
