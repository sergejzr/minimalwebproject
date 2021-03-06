package de.bonn.hrz.appmanager.beans;

import java.io.Serializable;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import de.bonn.hrz.appmanager.User;
import de.bonn.hrz.sql.UserManagerInterface;

@ManagedBean
@RequestScoped
public class PasswordChangeBean extends ApplicationBean implements Serializable {
	private static final long serialVersionUID = 2237249691332567548L;

	private String parameter;
	private String password;
	private String confirmPassword;
	private User user = null;

	public PasswordChangeBean() throws SQLException {
		System.out.println("construct PasswordChangeBean");
		if (parameter == null || parameter.equals(""))
			parameter = getFacesContext().getExternalContext()
					.getRequestParameterMap().get("u");

		if (parameter == null) {
			addMessage(FacesMessage.SEVERITY_ERROR, "invalid_request");
			return;
		}

		String[] splits = parameter.split("_");
		if (splits.length != 2) {
			addMessage(FacesMessage.SEVERITY_ERROR, "invalid_request");
			return;
		}
		int userId = Integer.parseInt(splits[0]);
		String hash = splits[1];

		user = getWebapplication().getUserManager().getUser(userId);
		if (null == user || !hash.equals(PasswordBean.createHash(user))) {
			addMessage(FacesMessage.SEVERITY_ERROR, "invalid_request");
			return;
		}
		System.out.println("edit user:" + user.getUsername() + " id "
				+ user.getId());
	}

	public void onChangePassword() {
		System.out.println("onChangePassword");
		UserManagerInterface um = getWebapplication().getUserManager();
		try {
			user.setPassword(password, false);
			um.save(user);
			// um.setPassword(getUser().getId(), password);
			addMessage(FacesMessage.SEVERITY_INFO, "password_changed");
			log("password changed");
			password = "";
			confirmPassword = "";
		} catch (SQLException e) {
			e.printStackTrace();
			addMessage(FacesMessage.SEVERITY_FATAL, "fatal_error");
			log("password change - error");
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	

	public void validatePassword(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		// Find the actual JSF component for the first password field.
		UIInput passwordInput = (UIInput) context.getViewRoot().findComponent(
				"passwordform:password");

		// Get its value, the entered password of the first field.
		String password = (String) passwordInput.getValue();

		if (null != password && !password.equals((String) value)) {
			throw new ValidatorException(getFacesMessage(
					FacesMessage.SEVERITY_ERROR, "passwords_do_not_match"));
		}
	}
}
