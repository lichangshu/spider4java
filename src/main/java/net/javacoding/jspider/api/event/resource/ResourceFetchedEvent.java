package net.javacoding.jspider.api.event.resource;

import net.javacoding.jspider.api.event.EventVisitor;
import net.javacoding.jspider.api.model.FetchedResource;
import net.javacoding.jspider.api.model.Resource;
import net.javacoding.jspider.core.SpiderContext;

/**
 *
 * $Id: ResourceFetchedEvent.java,v 1.2 2002/12/23 17:13:35 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public class ResourceFetchedEvent extends ResourceRelatedEvent {

	private SpiderContext spiderContext;

	public ResourceFetchedEvent(SpiderContext spiderContext, Resource resource) {
		super(resource);
		this.spiderContext = spiderContext;
	}

	public FetchedResource getResource() {
		return (FetchedResource) resource;
	}

	public String getComment() {
		FetchedResource fetchedResource = (FetchedResource) resource;
		return "resource " + resource.getURL() + " fetched [" + fetchedResource.getMime() + "]";
	}

	public void accept(EventVisitor visitor) {
		visitor.visit(this);
	}

	public SpiderContext getSpiderContext() {
		return spiderContext;
	}
}
