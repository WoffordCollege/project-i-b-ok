package edu.wofford.wocoin;

/**
 * This class represents the information stored in a message in the Wocoin database.
 * If id is negative, the field has not been populated.
 * If submitDateTime is null, the field has not been populated.
 */
public class Message {
	private int id;
	private String message;
	private String senderUsername;
	private String submitDateTime;
	private Product product;

	public Message(int id, String senderUsername, String message, String submitDateTime, Product product) {
		this.id = id;
		this.senderUsername = senderUsername;
		this.submitDateTime = submitDateTime;
		this.message = message;
		this.product = product;
	}

	public Message(String senderUsername, String message, Product product) {
		this(-1, senderUsername, message, null, product);
	}

	public int getId() {
		return id;
	}

	public String getRecipientUsername() {
		return product.getSeller();
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public String getSubmitDateTime() {
		return submitDateTime;
	}

	public Product getProduct() {
		return product;
	}

	public String getMessage() {
		return message;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSubmitDateTime(String submitDateTime) {
		this.submitDateTime = submitDateTime;
	}

	@Override
	public String toString() {
		return String.format("%s [%s] %s", message, product.getName(), this.submitDateTime);
	}
}
