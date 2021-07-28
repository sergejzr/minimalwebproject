package de.bonn.hrz.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.bonn.hrz.appmanager.Appmanager;
import de.bonn.hrz.appmanager.User;
import de.bonn.hrz.util.Cache;
import de.bonn.hrz.util.DummyCache;
import de.bonn.hrz.util.ICache;

/**
 * DAO for the User class. Because there are only a few Users we keep them all
 * in memory
 * 
 * @author Philipp
 * 
 */
public class UserManager extends SQLManager implements UserManagerInterface {

	private static Logger logger = Logger.getLogger(UserManager.class);
	// if you change this, you have to change the constructor of User too
	private final static String COLUMNS = "user_id, username, email, gender, dateofbirth, address, profession,"
			+ " additionalinformation, interest, phone, registration_date, password, last_changed, age, "
			+ " country, mail_notifications, active ";
	private String USERTABLE;

	private Appmanager webapplication;
	private ICache<User> cache;

	public UserManager(Appmanager webapplication) throws SQLException {
		super();
		Properties properties = webapplication.getProperties();
		int userCacheSize = Integer.parseInt(properties.getProperty("USER_CACHE"));

		this.webapplication = webapplication;
		this.cache = ((userCacheSize == 0) ? new DummyCache<User>() : new Cache<User>(userCacheSize));

		USERTABLE = webapplication.getUserTablePrefix() + "user";
	}

	public void resetCache() throws SQLException {
		cache.clear();
	}

	/**
	 * returns a list of all users
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<User> getUsers() throws SQLException {
		List<User> users = new LinkedList<User>();

		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			select = webapplication.getConnection().prepareStatement(
					"SELECT " + COLUMNS + " FROM `" + USERTABLE + "` WHERE deleted = 0 ORDER BY username");
			rs = select.executeQuery();
			if (null == rs)
				return users;

			while (rs.next()) {
				User user = cache.get(rs.getInt("user_id"));
				if (null == user) {
					user = new User(rs);
					user = cache.put(user); // this line makes sure that only on
											// instance of each user exists
				}
				users.add(user);
			}

			return users;
		} finally {
			close(rs, select);
		}
	}

	/**
	 * get a user by username and password
	 * 
	 * @param Username
	 * @param Password
	 * @return null if user not found
	 * @throws SQLException
	 */
	public User getUser(String username, String password) throws SQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {

			select = webapplication.getConnection().prepareStatement(
					"SELECT " + COLUMNS + " FROM " + "`" + USERTABLE + "` WHERE username = ? AND password = MD5(?)");

			select.setString(1, username);
			select.setString(2, password);
			rs = select.executeQuery();

			if (!rs.next())
				return null;

			User user = cache.get(rs.getInt("user_id"));
			if (null == user) {
				user = new User(rs);
				user = cache.put(user);
			}
			return user;
		} finally {
			close(rs, select);
		}
	}

	@Override
	public User getUser(String email) throws SQLException {
		ResultSet rs = null;
		PreparedStatement select = null;
		try {

			select = webapplication.getConnection()
					.prepareStatement("SELECT " + COLUMNS + " FROM " + USERTABLE + " WHERE email = ?");

			select.setString(1, email);
			rs = select.executeQuery();

			if (!rs.next())
				return null;

			User user = cache.get(rs.getInt("user_id"));
			if (null == user) {
				user = new User(rs);
				user = cache.put(user);
			}
			return user;
		} finally {
			close(rs, select);
		}
	}

	/**
	 * Get a User by his id returns null if the user does not exist
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public User getUser(int userId) throws SQLException {
		PreparedStatement pstmtGetUser = null;
		ResultSet rs = null;

		try {
			if (userId < 1) {
				if (userId != 0)
					new IllegalArgumentException("invalid user id was requestet: " + userId).printStackTrace();

				return null;
			}
			User user = cache.get(userId);

			if (null != user)
				return user;

			pstmtGetUser = webapplication.getConnection()
					.prepareStatement("SELECT " + COLUMNS + " FROM `" + USERTABLE + "` WHERE user_id = ?");
			pstmtGetUser.setInt(1, userId);
			rs = pstmtGetUser.executeQuery();

			if (!rs.next())
				new IllegalArgumentException("invalid user id was requested: " + userId).printStackTrace();
			user = new User(rs);

			user = cache.put(user);
			return user;
		} finally {
			close(rs, pstmtGetUser);
		}
	}

	/**
	 * Saves the User to the database. If the User is not yet stored at the
	 * database, a new record will be created and the returned User contains the new
	 * id.
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public User save(User user) throws SQLException {
		PreparedStatement replace = null;
		ResultSet rs = null;
		try {
			//@formatter:off
			replace = webapplication.getConnection().prepareStatement(
					"REPLACE INTO `" + USERTABLE + "` (" + COLUMNS
							+ ") VALUES "
							+ "(?,trim(?),trim(?),?,?,?,?,?,?,?,?,?,?,now(), ?,?,?)", // TODO:
																			// 3-Fragezeichen
					Statement.RETURN_GENERATED_KEYS);
			//@formatter:off
			if (user.getId() < 0) // the User is not yet stored at the database
				replace.setNull(1, java.sql.Types.INTEGER);
			else
				replace.setInt(1, user.getId());
			replace.setString(2, user.getUsername());
			replace.setString(3, user.getEmail());
			replace.setInt(4, user.getGender());
			replace.setDate(5, user.getDateofbirth() == null ? null
					: new java.sql.Date(user.getDateofbirth().getTime()));
			replace.setString(6, user.getAddress());
			replace.setString(7, user.getProfession());
			replace.setString(8, user.getAdditionalInformation());
			replace.setString(9, user.getInterest());
			replace.setString(10, user.getPhone());
			replace.setDate(11, user.getRegistrationDate() == null ? new java.sql.Date((new Date()).getTime())
					: new java.sql.Date(user.getRegistrationDate().getTime()));

			replace.setString(12, user.getPassword());
			replace.setString(13, user.getAge());
			replace.setString(14, user.getCountry());

			replace.setBoolean(15, user.isMailNotifications());
			replace.setBoolean(16, user.isActive());
	
			logger.debug("save(User) executes: " + replace.toString());
			replace.executeUpdate();

			if (user.getId() < 0) // get the assigned id
			{
				rs = replace.getGeneratedKeys();
				if (!rs.next())
					throw new SQLException("database error: no id generated");
				user.setId(rs.getInt(1));

			}
			cache.put(user); // add the User to the cache (update cache state!)
			replace.close();

			return user;
		} finally {
			close(rs, replace);
		}
	}

	/**
	 * Returns true if username is already in use
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	@Override
	public boolean isUsernameAlreadyTaken(String username) throws SQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			select = webapplication.getConnection().prepareStatement(
					"SELECT 1 FROM `" + USERTABLE
							+ "` WHERE trim(username) = trim(?)");
			select.setString(1, username);
			rs = select.executeQuery();

			boolean result = rs.next();
			return result;
		} finally {
			close(rs, select);
		}
	}

	@Override
	public boolean isMailAlreadyTaken(String email) throws SQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			select = webapplication.getConnection().prepareStatement(
					"SELECT 1 FROM `" + USERTABLE
							+ "` WHERE trim(email) = trim(?)");
			select.setString(1, email);
			rs = select.executeQuery();

			boolean result = rs.next();
			return result;
		} finally {
			close(rs, select);
		}
	}

	@Override
	public User registerUser(String username, String password, String email,
			String age, String country, String facebook, int gender)
			throws Exception {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password, false);
		user.setAge(age);
		user.setCountry(country);
		user.setGender(gender);
		user = save(user);
		return user;
	}

	private String generateMTToken(User user) throws SQLException {
		String token = "";
		do {
			token = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		} while (isTokenTaken(token));
		return token;
	}

	private boolean isTokenTaken(String token) throws SQLException {
		ResultSet rs = null;
		PreparedStatement select = null;
		try {

			select = webapplication.getConnection().prepareStatement(
					"SELECT 1 FROM `" + USERTABLE + "` WHERE mt_token = ?");
			select.setString(1, token);
			rs = select.executeQuery();
			boolean result = rs.next();
			return result;
		} finally {
			close(rs, select);
		}
	}

	@Override
	public User getUserByToken(String token) throws SQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			select = webapplication.getConnection().prepareStatement(
					"SELECT " + COLUMNS + " FROM " + USERTABLE
							+ " WHERE mt_token = ?");

			select.setString(1, token);
			rs = select.executeQuery();

			if (!rs.next())
				return null;

			User user = cache.get(rs.getInt("user_id"));
			if (null == user) {
				user = new User(rs);
				user = cache.put(user);
			}
			return user;
		} finally {
			close(rs, select);
		}
	}

	@Override
	public Map<String, User> getInactiveUsers() throws SQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		Map<String,User> users = new HashMap<String,User>();
		try {

			select = webapplication.getConnection().prepareStatement(
					"SELECT " + COLUMNS + " FROM " + "`" + USERTABLE
							+ "` WHERE deleted = 0 AND active = 0 ");

			rs = select.executeQuery();

			while(rs.next()) {
				User user = new User(rs);
				users.put(user.getUsername(), user);				
			}
			return users;
		} finally {
			close(rs, select);
		}
	}
}
