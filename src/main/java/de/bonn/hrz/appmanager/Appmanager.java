package de.bonn.hrz.appmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

//import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.bonn.hrz.sql.ApplicationProperties;
import de.bonn.hrz.sql.LogManager;
import de.bonn.hrz.sql.UserManager;
import de.bonn.hrz.sql.UserManagerInterface;

@ManagedBean
@ApplicationScoped
public class Appmanager extends Observable {
	public final static String salt1 = "ff4a9ff19306ee0407cf69d592";
	public final static String salt2 = "3a129713cc1b33650816d61450";

	private Connection dbConnection;

	private Properties properties;
	private String contextUrl;

	// Manager (Data Access Objects):
	private UserManagerInterface userManager;
	private LogManager logManager;

	private ApplicationProperties applicationProperties;

	// table prefix
	private String tablePrefix;
	private String userTablePrefix;


	private static Appmanager webapplication = null;
	private static final Logger logger = Logger.getLogger(Appmanager.class);

	private HashMap<Integer, String> sessionMap;

	public static Appmanager getInstance(String contextUrl)
			throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		if (webapplication == null) {
			webapplication = new Appmanager(contextUrl);
		}
		return webapplication;
	}


	public static Appmanager getInstance() {
		try {
			return getInstance("http://learnweb.l3s.uni-hannover.de");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param contextUrl The servername + contextpath. For the default installation
	 *                   this is: http://learnweb.l3s.uni-hannover.de
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private Appmanager(String contextUrl)
			throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		this.contextUrl = contextUrl;

		try {
			PropertyConfigurator.configure(new Properties() {
				private static final long serialVersionUID = 3522475775881727293L;
				{
					load(getClass().getClassLoader().getResourceAsStream("de/bonn/webapplication/config/log4j.properties"));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.properties = new Properties();
		String propertiesFileName = "mechanical";
		try {

			if ((new File("/home/markus")).exists()) // don't change this. Add
														// an elseif statement,
														// if you want to use
														// another properties
														// file
				propertiesFileName = "mt_local_markus";

			InputStream loader = getClass().getClassLoader()
					.getResourceAsStream("de/bonn/webapplication/config/" + propertiesFileName + ".properties");

			properties.load(getClass().getClassLoader()
					.getResourceAsStream("de/bonn/webapplication/config/" + propertiesFileName + ".properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// connect db
		Class.forName("com.mysql.jdbc.Driver");
		connect();

		// init DAOs etc
		this.tablePrefix = properties.getProperty("table_prefix");
		this.userTablePrefix = properties.getProperty("user_table_prefix");
		this.userManager = new UserManager(this);
		this.logManager = new LogManager(this);

		this.sessionMap = new HashMap<Integer, String>();
		this.applicationProperties = new ApplicationProperties(this);
	}



	public static void main(String[] args) {
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));

		cal.set(2016, 7, 15, 2, 10);

		System.out.println(cal.getTime());
	}

	/*
	 * public FedoraManager getFedoraManager() { return fedoraManager; }
	 */

	public UserManagerInterface getUserManager() {
		return userManager;
	}

	private void connect() throws SQLException {
		dbConnection = DriverManager.getConnection(properties.getProperty("mysql_url"),
				properties.getProperty("mysql_user"), properties.getProperty("mysql_password"));
		setChanged();
		notifyObservers();
	}

	private Long lastCheck = 0L;

	/**
	 * 
	 * @return true if new connection was established
	 * @throws SQLException
	 */
	public void checkConnection() throws SQLException {
		synchronized (lastCheck) {
			// exit if last check was one or less seconds ago
			if (lastCheck > System.currentTimeMillis() - 2000)
				return;

			if (!dbConnection.isValid(1)) {
				logger.error("Database connection invalid try to reconnect");

				try {
					dbConnection.close();
				} catch (SQLException e) {
				}
				try {
					// small backoff in case there is a problem with db server
					Thread.sleep(20L);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					connect();
				} catch (SQLException e) {
					logger.error(e.getLocalizedMessage(), e);
					// may be the result of missing driver
					DriverManager.registerDriver(new com.mysql.jdbc.Driver());
					connect();
				}
			}

			lastCheck = System.currentTimeMillis();
		}
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * This method should be called before the system shuts down
	 */
	// @PreDestroy
	public void onDestroy() {
		getLogManager().onDestroy();

		try {
			dbConnection.close();
		} catch (SQLException e) {
		} // ignore
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.info("deregistered driver: " + driver);
			} catch (SQLException e) {
				logger.error("error while deregistering driver: " + driver, e);
			}
		}
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for (Thread t : threadArray) {
			if (t.getName().contains("Abandoned connection cleanup thread")) {
				synchronized (t) {
					t.stop(); // don't complain, it works
				}
			}
		}
		org.apache.log4j.LogManager.shutdown();
	}

	/**
	 * Will be deprecated in the future
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnectionStatic() throws SQLException {
		Appmanager lw = getInstance();
		lw.checkConnection();

		return lw.dbConnection;
	}

	// should be used instead of the static method
	public Connection getConnection() throws SQLException {
		checkConnection();

		return dbConnection;
	}

	/**
	 * 
	 * @return Returns the servername + contextpath. For the default installation
	 *         this is: http://learnweb.l3s.uni-hannover.de
	 */
	public String getContextUrl() {
		if (null == contextUrl) {
			ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

			if (ext.getRequestServerPort() == 80 || ext.getRequestServerPort() == 443)
				contextUrl = ext.getRequestScheme() + "://" + ext.getRequestServerName() + ext.getRequestContextPath();
			else
				contextUrl = ext.getRequestScheme() + "://" + ext.getRequestServerName() + ":"
						+ ext.getRequestServerPort() + ext.getRequestContextPath();
			logger.info("context url is set to '" + contextUrl + "'");
		}
		return contextUrl; // because we don't use httpS we can cache the url,
							// change it if you want to use httpS too
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}


	public LogManager getLogManager() {
		return this.logManager;
	}

	public HashMap<Integer, String> getSessionMap() {
		return sessionMap;
	}

	public ApplicationProperties getApplicationProperties() {
		return this.applicationProperties;
	}

	public String getUserTablePrefix() {
		return userTablePrefix;
	}

	public void setUserTablePrefix(String userTablePrefix) {
		this.userTablePrefix = userTablePrefix;
	}


}
