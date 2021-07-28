package de.bonn.hrz.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.bonn.hrz.appmanager.Appmanager;

public class SQLManager {
	private static Logger logger = Logger.getLogger(SQLManager.class);

	protected Appmanager application;

	// for legacy code
	public SQLManager() {
	}

	public SQLManager(Appmanager application) {
		this.application = application;
	}

	protected static void close(ResultSet rs, PreparedStatement ps) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("The result set could not be closed.", e);
			}
		}
		if (null != ps) {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error("The statement could not be closed.", e);
			}
		}
	}

	protected boolean existsTable(String tableName) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = this.application.getConnection().prepareStatement(
					"SHOW TABLES LIKE ?");
			stmt.setString(1, tableName);
			rs = stmt.executeQuery();

			rs.next();
			return rs.isLast();
		} finally {
			close(rs, stmt);
		}
	}
}
