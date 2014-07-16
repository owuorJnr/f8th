package owuor.f8th.types;

public class UserProfile extends User{
	
	
	private String favVerse;
	private String why;
	private String desire;
	private String location;
	private String views;
	
	public UserProfile(String uid,String userId,String fname,String lname,String gender,String country,String favVerse,String why,String desire,String views,String location){
		super(uid,userId,fname,lname,gender,country);
		this.favVerse = favVerse;
		this.why = why;
		this.desire = desire;
		this.views = views;
		this.location = location;
	}
	
	public UserProfile(String uid,String userId,String fname,String lname,String gender,String country,String favVerse,String why,String desire,String views){
		super(uid,userId,fname,lname,gender,country);
		this.favVerse = favVerse;
		this.why = why;
		this.desire = desire;
		this.views = views;
		this.location = "server";
	}
	
	
	
	public String getWhy(){
		return why;
	}
	
	public String getDesire(){
		return desire;
	}
	
	public String getFavVerse(){
		return favVerse;
	}
	
	
	public String getLocation(){
		return location;
	}
	
	public String getViews(){
		return views;
	}
}//END OF CLASS User