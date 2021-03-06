package de.bonn.hrz.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import de.bonn.hrz.appmanager.Appmanager;

public class ApplicationProperties extends SQLManager {
	private String PROPERTIESTABLE;
	private Appmanager webapplication;

	private long checkInterval;
	private long lastCheck = 0L;

	private static Logger logger = Logger
			.getLogger(ApplicationProperties.class);

	private HashMap<String, String> properties;

	public ApplicationProperties(Appmanager webapplication) {
		this.webapplication = webapplication;

		// init checkInterval
		try {
			this.checkInterval = Long.parseLong(webapplication.getProperties()
					.getProperty("PROPERTIES_CHECK_INTERVAL"));
		} catch (NumberFormatException e) {
			logger.error(
					"Error parsing checkInterval! Setting to default value of 1000 milliseconds",
					e);
			this.checkInterval = 1000L;
		}
		this.PROPERTIESTABLE = webapplication.getTablePrefix()
				+ "application_property";
		logger.info("Use table `" + this.PROPERTIESTABLE
				+ "`. Initial check of properties..");
		checkProperties();
		logger.info(properties.size() + " properties retrieved.");
	}

	public String getProperty(String key) {
		checkProperties();
		return properties.get(key);
	}

	private synchronized void checkProperties() {
		if (this.lastCheck + this.checkInterval < System.currentTimeMillis()) {
			try {
				this.properties = getProperties();
			} catch (SQLException e) {
				logger.error("error while retrieving properties.", e);
			}
			this.lastCheck = System.currentTimeMillis();
		}
	}

	private HashMap<String, String> getProperties() throws SQLException {
		HashMap<String, String> properties = new HashMap<String, String>();
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			pStmt = webapplication.getConnection().prepareStatement(
					"SELECT ap.`key`, ap.`value` FROM `" + PROPERTIESTABLE
							+ "` ap");
			logger.debug("getProperties() executes: " + pStmt.toString());
			rs = pStmt.executeQuery();
			while (rs.next()) {
				try {
					properties.put(rs.getString(1), rs.getString(2));
				} catch (SQLException e) {
					logger.error("error adding property", e);
				}
			}
		} finally {
			close(rs, pStmt);
		}

		return properties;
	}

}
