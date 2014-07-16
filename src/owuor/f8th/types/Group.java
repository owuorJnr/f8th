package owuor.f8th.types;

public class Group {
	
	private byte[] photo;
	private String MID;
	private String GID;
	private String grpId;
	private String grpOwnerId;
	private String grpOwnerName;
	private String grpName;
	private String grpType;
	private String grpSize;//no of members
	private String grpCity;
	private String grpCountry;
	private String userType;//manager, member or non-member
	
	private boolean selected;

	public Group(String MID,String GID,String grpId,String ownerId,String ownerName,String grpName,String type,String size,String city,String country,String userType){
		this.MID = MID;
		this.GID = GID;
		this.grpId = grpId;
		this.grpOwnerId = ownerId;
		this.grpOwnerName = ownerName;
		this.grpName = grpName;
		this.grpType = type;
		this.grpSize = size;
		this.grpCity = city;
		this.grpCountry = country;
		this.userType = userType;
	}
	

	public String getGroupId(){
		return grpId;
	}
	
	public void setGrpPhoto(byte[] photo){
		this.photo = photo;
	}
	
	public byte[] getGrpPhoto(){
		return photo;
	}

	public String getName(){
		return this.grpName;
	}
	
	public String getOwnerId(){
		return this.grpOwnerId;
	}
	
	public String getOwnerName(){
		return this.grpOwnerName;
	}
	
	public String getCity(){
		return this.grpCity;
	}
	
	public String getCountry(){
		return this.grpCountry;
	}
	
	public String getType(){
		return this.grpType;
	}
	
	public String getM_ID(){
		return this.MID;
	}
	
	public String getG_ID(){
		return this.GID;
	}
	
	public String getSize(){
		return this.grpSize;
	}
	
	public String getUserType(){
		return this.userType;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
}
