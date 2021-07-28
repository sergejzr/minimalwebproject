package de.bonn.hrz.appmanager.beans;

import java.io.Serializable;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.bonn.hrz.appmanager.Appmanager;
import de.bonn.hrz.appmanager.User;
import de.bonn.hrz.util.MD5;

@ManagedBean
@RequestScoped
public class PasswordBean extends ApplicationBean implements Serializable {
	private static final long serialVersionUID = 2237249691336567548L;

	private String email;

	public static class GMailAuthenticator extends Authenticator {
		String user;
		String pw;

		public GMailAuthenticator(String username, String password) {
			super();
			this.user = username;
			this.pw = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pw);
		}
	}

	public void onGetPassword() {
		try {
			User user = getWebapplication().getUserManager().getUser(email);

			if (null == user) {
				addMessage(FacesMessage.SEVERITY_ERROR, "unknown_email");
				return;
			}

			sendMail(user);

			addMessage(FacesMessage.SEVERITY_INFO, "email_has_been_send");
		} catch (Exception e) {
			addFatalMessage(e);
		}
	}

	public static String createHash(User user) {
		return MD5.hash(Appmanager.salt1 + user.getId() + user.getPassword()
				+ Appmanager.salt2);
	}

	private void sendMail(User user) throws AddressException,
			MessagingException {

		Properties props = System.getProperties();

		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "25");
		Session session1 = Session.getDefaultInstance(props);

		Message message = new MimeMessage(session1);

		message.setFrom(new InternetAddress("crowdgames@l3s.de"));

		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(user.getEmail(), false));
		message.setRecipients(Message.RecipientType.BCC,
				InternetAddress.parse("rokicki@l3s.de", false));

		String compTitle = UtilBean.getLocaleMessage("competitionTitle");

		message.setSubject("Retrieve " + compTitle + " password");

		String link = UtilBean.getApplicationBean().getContextUrl()
				+ "/mt/user/change_password.jsf?u=" + user.getId() + "_"
				+ createHash(user);

		message.setText("Hi " + user.getUsername()
				+ ",\nif you want to change the password of your " + compTitle
				+ " account click here:\n" + link);
		message.saveChanges();
		Transport.send(message);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
