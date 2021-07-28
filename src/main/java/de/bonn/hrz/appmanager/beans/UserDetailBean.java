package de.bonn.hrz.appmanager.beans;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.bonn.hrz.appmanager.User;

@ManagedBean
@RequestScoped
public class UserDetailBean extends ApplicationBean {

	private int userId;
	private User user;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public User getUser() 
	{
		return user;
	}	
	
	public void loadUser() {

		if (0 == userId) {
			addMessage(FacesMessage.SEVERITY_ERROR, "invalid user id");
			return;
		}

		try {
			user = getWebapplication().getUserManager().getUser(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (null == user)
			addMessage(FacesMessage.SEVERITY_ERROR, "invalid user id");
	}
}
