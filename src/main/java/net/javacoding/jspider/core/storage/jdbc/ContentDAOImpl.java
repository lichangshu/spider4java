package net.javacoding.jspider.core.storage.jdbc;

import net.javacoding.jspider.core.storage.spi.ContentDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.sql.*;

/**
 * $Id: ContentDAOImpl.java,v 1.3 2003/04/11 16:37:05 vanrogu Exp $
 */
class ContentDAOImpl implements ContentDAOSPI {

	protected Log log;

	protected DBUtil dbUtil;
	protected StorageSPI storage;

	public ContentDAOImpl(StorageSPI storage, DBUtil dbUtil) {
		this.log = LogFactory.getLog(ContentDAOSPI.class);
		this.dbUtil = dbUtil;
		this.storage = storage;
	}

	private static String INSERT_JSPIDER_CONTENT_SQL = "insert into jspider_content ( id, content ) values ( ?, ? )";

	public void setBytes(int id, byte[] bytes) {
		try {
			Connection connection = dbUtil.getConnection();
			PreparedStatement ps = connection.prepareStatement(INSERT_JSPIDER_CONTENT_SQL);
			ps.setInt(1, id);
			ps.setBytes(2, bytes);
			try {
				ps.execute();
			} catch (IllegalArgumentException e) {
				log.error("IllegalArgumentException", e);
			} finally {
				dbUtil.safeClose(ps, log);
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		}
	}

	private static final String FIND_CONTENT_FROM_JSPIDER_CONTENT = "select content from jspider_content where id=?";

	public InputStream getInputStream(int id) {
		byte[] bytes = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection connection = dbUtil.getConnection();
			ps = connection.prepareStatement(FIND_CONTENT_FROM_JSPIDER_CONTENT);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				bytes = rs.getBytes("content");
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			dbUtil.safeClose(rs, log);
			dbUtil.safeClose(ps, log);
		}
		return new ByteArrayInputStream(bytes);
	}

}
