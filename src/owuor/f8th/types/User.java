package owuor.f8th.types;

public class User {
	
	private byte[] photo;
	private String UID;
	private String userId;
	private String fname;
	private String lname;
	private String gender;
	private String country;
	
	public User(String uid,String userId,String fname,String lname,String gender,String country){
		this.UID = uid;
		this.userId = userId;
		this.fname = fname;
		this.lname = lname;
		this.gender = gender;
		this.country = country;
		
	}
	
	public void setUserPhoto(byte[] photo){
		this.photo = photo;
	}
	
	public byte[] getUserPhoto(){
		return photo;
	}
	
	public String getU_ID(){
		return UID;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public String getFname(){
		return fname;
	}
	
	public String getLname(){
		return lname;
	}
	
	public String getGender(){
		return gender;
	}
	
	public String getCountry(){
		return country;
	}
	
	
}//END OF CLASS User
