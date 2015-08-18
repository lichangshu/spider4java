package net.javacoding.jspider.core.storage.jdbc;

import net.javacoding.jspider.core.event.impl.*;
import net.javacoding.jspider.core.model.*;
import net.javacoding.jspider.core.storage.spi.ResourceDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;
import net.javacoding.jspider.core.storage.exception.InvalidStateTransitionException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import net.javacoding.jspider.api.model.HTTPHeader;

/**
 * $Id: ResourceDAOImpl.java,v 1.11 2003/04/19 19:00:46 vanrogu Exp $
 */
class ResourceDAOImpl implements ResourceDAOSPI {

	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_SITE = "site";
	public static final String ATTRIBUTE_URL = "url";
	public static final String ATTRIBUTE_HEADER = "header";
	public static final String ATTRIBUTE_STATE = "state";
	public static final String ATTRIBUTE_MIME = "mimetype";
	public static final String ATTRIBUTE_TIME = "timems";
	public static final String ATTRIBUTE_SIZE = "size";
	public static final String ATTRIBUTE_FOLDER = "folder";
	public static final String ATTRIBUTE_HTTP_STATUS = "httpstatus";

	protected DBUtil dbUtil;
	protected StorageSPI storage;
	protected Log log;

	public ResourceDAOImpl(StorageSPI storage, DBUtil dbUtil) {
		this.storage = storage;
		this.dbUtil = dbUtil;
		this.log = LogFactory.getLog(ResourceDAOImpl.class);
	}

	public void registerURLReference(URL url, URL refererURL) {
		ResourceInternal resource = getResource(url);
		Statement st = null;
		ResultSet rs = null;
		if (refererURL != null) {
			ResourceInternal referer = getResource(refererURL);
			try {
				int from = referer.getId();
				int to = resource.getId();
				Connection connection = dbUtil.getConnection();

				st = connection.createStatement();
				rs = st.executeQuery("select count(*) from jspider_resource_reference where referer = " + from + " and referee = " + to);
				rs.next();
//				Statement st2 = connection.createStatement();
				if (rs.getInt(1) == 0) {
					st.executeUpdate("insert into jspider_resource_reference ( referer, referee, count ) values (" + from + "," + to + ", 1)");
				} else {
					st.executeUpdate("update jspider_resource_reference set count = count + 1 where referer = " + from + " and referee = " + to);
				}
			} catch (SQLException e) {
				log.error("SQLException", e);
			} finally {
				dbUtil.safeClose(rs, log);
				dbUtil.safeClose(st, log);
			}
		}
	}

	public ResourceInternal[] findAllResources() {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from jspider_resource");
			while (rs.next()) {
				al.add(createResourceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
	}

	public ResourceInternal[] getRefereringResources(ResourceInternal resource) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from jspider_resource, jspider_resource_reference where jspider_resource.id = jspider_resource_reference.referer and jspider_resource_reference.referee = " + resource.getId());
			while (rs.next()) {
				al.add(createResourceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
	}

	public ResourceReferenceInternal[] getOutgoingReferences(ResourceInternal resource) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select referer.url as referer, referee.url as referee, count from jspider_resource referer, jspider_resource referee, jspider_resource_reference where jspider_resource_reference.referer = " + resource.getId() + " and jspider_resource_reference.referee = referee.id and jspider_resource_reference.referer = referer.id");
			while (rs.next()) {
				al.add(createResourceReferenceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceReferenceInternal[]) al.toArray(new ResourceReferenceInternal[al.size()]);
	}

	public ResourceReferenceInternal[] getIncomingReferences(ResourceInternal resource) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select referer.url as referer, referee.url as referee, count from jspider_resource referer, jspider_resource referee, jspider_resource_reference where jspider_resource_reference.referee = " + resource.getId() + " and jspider_resource_reference.referee = referee.id and jspider_resource_reference.referer = referer.id");
			while (rs.next()) {
				al.add(createResourceReferenceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceReferenceInternal[]) al.toArray(new ResourceReferenceInternal[al.size()]);
	}

	public ResourceInternal[] getReferencedResources(ResourceInternal resource) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from jspider_resource, jspider_resource_reference where jspider_resource.id = jspider_resource_reference.referee and jspider_resource_reference.referer = " + resource.getId());
			while (rs.next()) {
				al.add(createResourceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
	}

	public ResourceInternal[] findByFolder(FolderInternal folder) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from jspider_resource where folder=" + folder.getId());
			while (rs.next()) {
				al.add(createResourceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
	}

	public ResourceInternal[] getBySite(SiteInternal site) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from jspider_resource where site=" + site.getId());
			while (rs.next()) {
				al.add(createResourceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
	}

	public ResourceInternal[] getRootResources(SiteInternal site) {
		ArrayList al = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from jspider_resource where site=" + site.getId() + " and folder=0");
			while (rs.next()) {
				al.add(createResourceFromRecord(rs));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
	}

	public synchronized void setSpidered(URL url, URLSpideredOkEvent event) {
		ResourceInternal resource = getResource(url);
		resource.setFetched(event.getHttpStatus(), event.getSize(), event.getTimeMs(), event.getMimeType(), null, event.getHeaders());
		save(resource);
		resource.setBytes(event.getBytes());
	}

	public synchronized void setIgnoredForParsing(URL url) throws InvalidStateTransitionException {
		ResourceInternal resource = getResource(url);
		resource.setParseIgnored();
		save(resource);
	}

	public synchronized void setIgnoredForFetching(URL url, URLFoundEvent event) throws InvalidStateTransitionException {
		ResourceInternal resource = getResource(url);
		resource.setFetchIgnored();
		save(resource);
	}

	public synchronized void setForbidden(URL url, URLFoundEvent event) throws InvalidStateTransitionException {
		ResourceInternal resource = getResource(url);
		resource.setForbidden();
		save(resource);
	}

	public synchronized void setError(URL url, ResourceParsedErrorEvent event) throws InvalidStateTransitionException {
		ResourceInternal resource = getResource(url);
		resource.setParseError();
		save(resource);
	}

	public synchronized void setParsed(URL url, ResourceParsedOkEvent event) throws InvalidStateTransitionException {
		ResourceInternal resource = getResource(url);
		resource.setParsed();
		save(resource);
	}

	public synchronized void setError(URL url, URLSpideredErrorEvent event) throws InvalidStateTransitionException {
		ResourceInternal resource = getResource(url);
		resource.setFetchError(event.getHttpStatus(), event.getHeaders());
		save(resource);
	}

	public ResourceInternal getResource(int id) {
		ResourceInternal resource = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = dbUtil.getConnection().createStatement();
			rs = st.executeQuery("select * from jspider_resource where id='" + id + "'");
			if (rs.next()) {
				resource = createResourceFromRecord(rs);
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(st, log);
		}
		return resource;
	}

	public ResourceInternal getResource(URL url) {
		ResourceInternal resource = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		if (url != null) {
			try {
				st = dbUtil.getConnection().prepareStatement("select * from jspider_resource where url=?");
				st.setString(1, url.toString());
				rs = st.executeQuery();
				if (rs.next()) {
					resource = createResourceFromRecord(rs);
				}
			} catch (SQLException e) {
				log.error("SQLException", e);
			} finally {
				dbUtil.safeClose(rs, log);
				dbUtil.safeClose(st, log);
			}
		}
		return resource;
	}

	public void create(int id, ResourceInternal resource) {
		Connection connection = dbUtil.getConnection();
		PreparedStatement st = null;

		String sql = "insert into jspider_resource (id, url, site, state, httpstatus, timems, folder) "
				+ " value (?,?,?,?,?,?,?)";
		FolderInternal folder = (FolderInternal) resource.getFolder();
		int folderId = (folder == null) ? 0 : folder.getId();
		try {
			st = connection.prepareStatement(sql);
			st.setInt(1, id);
			st.setString(2, resource.getURL().toString());
			st.setInt(3, resource.getSiteId());
			st.setInt(4, resource.getState());
			st.setInt(5, resource.getHttpStatusInternal());
			st.setInt(6, resource.getTimeMsInternal());
			st.setInt(7, folderId);
			st.executeUpdate();
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(st, log);
		}
	}

	public void save(ResourceInternal resource) {
		Connection connection = dbUtil.getConnection();
		PreparedStatement st = null;
		String sql = "update jspider_resource set "
				+ " state = ?, mimetype = ?, httpstatus = ?, size=?, header=?, timems=? "
				+ " where id= ? ";
		StringBuffer sb = new StringBuffer();
		if (resource.getHeaders() != null) {
			for (HTTPHeader hd : resource.getHeaders()) {
				String nm = hd.getName();
				if (nm == null) {
					nm = "";
				}
				sb.append(DBUtil.format(nm + ":" + hd.getValue() + "\n"));
			}
		}
		try {
			st = connection.prepareStatement(sql);
			st.setString(1, DBUtil.format(resource.getState()));
			st.setString(2, resource.getMimeInternal());
			st.setInt(3, resource.getHttpStatusInternal());
			st.setInt(4, resource.getSizeInternal());
			st.setString(5, sb.toString());
			st.setInt(6, resource.getTimeMsInternal());
			st.setInt(7, resource.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(st, log);
		}
	}

	protected ResourceInternal createResourceFromRecord(ResultSet rs) throws SQLException {
		int id = rs.getInt(ATTRIBUTE_ID);
		int folderId = rs.getInt(ATTRIBUTE_FOLDER);
		int siteId = rs.getInt(ATTRIBUTE_SITE);
		String urlString = rs.getString(ATTRIBUTE_URL);
		String headerString = rs.getString(ATTRIBUTE_HEADER);
		int state = rs.getInt(ATTRIBUTE_STATE);
		String mime = rs.getString(ATTRIBUTE_MIME);
		int time = rs.getInt(ATTRIBUTE_TIME);
		int size = rs.getInt(ATTRIBUTE_SIZE);
		int httpStatus = rs.getInt(ATTRIBUTE_HTTP_STATUS);

		FolderInternal folder = storage.getFolderDAO().findById(folderId);

		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			log.error("MalformedURLException", e);
		}
		List<HTTPHeader> head = new ArrayList();
		if (headerString != null) {
			String[] hds = headerString.trim().split("\n");
			for (String hd : hds) {
				String[] h2 = hd.trim().split(":");
				if (h2.length >= 2) {
					if ("".equals(h2[0])) {
						h2[0] = null;
					}
					head.add(new HTTPHeader(h2[0], h2[1]));
				}
			}
		}
		ResourceInternal ri = new ResourceInternal(storage, id, siteId, url, null, folder);
		ri.setFetched(httpStatus, size, time, null, null, head.toArray(new HTTPHeader[head.size()]));
		ri.setSize(size);
		ri.setTime(time);
		ri.setState(state);
		ri.setMime(mime);
		ri.setHttpStatus(httpStatus);
		return ri;
	}

	protected ResourceReferenceInternal createResourceReferenceFromRecord(ResultSet rs) throws SQLException {
		ResourceReferenceInternal rr = null;
		try {
			String refererURL = rs.getString("referer");
			String refereeURL = rs.getString("referee");
			URL referer = new URL(refererURL);
			URL referee = new URL(refereeURL);
			int count = rs.getInt("count");
			rr = new ResourceReferenceInternal(storage, referer, referee, count);
		} catch (MalformedURLException e) {
			log.error("MalformedURLException", e);
		}
		return rr;
	}

}
