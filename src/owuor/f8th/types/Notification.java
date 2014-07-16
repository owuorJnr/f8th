package owuor.f8th.types;

public class Notification {

	private byte[] senderPhoto;
	private String NID;
	private String notifyId;
	private String message;
	private String senderId;
	private String sender;
	private String sentAt;
	private String isRead;//read, unread

	public Notification(String NID,String notifyId,String message,String senderId,String sender,String dateSent,String status){
		this.NID = NID;
		this.notifyId = notifyId;
		this.message = message;
		this.senderId = senderId;
		this.sender = sender;
		this.sentAt = dateSent;
		this.isRead = status;
	}
	
	public void setUserPhoto(byte[] photo){
		this.senderPhoto = photo;
	}
	
	public byte[] getUserPhoto(){
		return senderPhoto;
	}
	
	public String getNID(){
		return NID;
	}
	
	public String getNotifyId(){
		return notifyId;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public String getSenderId(){
		return senderId;
	}
	
	public String getSender(){
		return this.sender;
	}
	
	public String getDateSent(){
		return this.sentAt;
	}
	
	public String getStatus(){
		return this.isRead;
	}
	
}
