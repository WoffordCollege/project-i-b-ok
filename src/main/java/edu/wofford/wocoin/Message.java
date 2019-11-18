package edu.wofford.wocoin;

/**
 * This class represents the information stored in a message in the Wocoin database.
 * If id is negative, the field has not been populated.
 * If submitDateTime is null, the field has not been populated.
 */
public class Message {
	private int id;
	private String recipientUsername;
	private String senderUsername;
	private String submitDateTime;
	private Product product;

	public Message(int id, String recipientUsername, String senderUsername, String submitDateTime, Product product) {
		this.id = id;
		this.recipientUsername = recipientUsername;
		this.senderUsername = senderUsername;
		this.submitDateTime = submitDateTime;
		this.product = product;
	}

	public Message(String recipientUsername, String senderUsername, String submitDateTime, Product product) {
		this(-1, recipientUsername, senderUsername, submitDateTime, product);
	}

	public Message(String recipientUsername, String senderUsername, Product product) {
		this(-1, recipientUsername, senderUsername, null, product);
	}

	public int getId() {
		return id;
	}

	public String getRecipientUsername() {
		return recipientUsername;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setSubmitDateTime(String submitDateTime) {
		this.submitDateTime = submitDateTime;
	}
}
