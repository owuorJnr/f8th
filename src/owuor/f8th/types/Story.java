package owuor.f8th.types;

public class Story {

	private String SID;
	private String favId;
	private String storyId;
	private String story;
	private String authorId;
	private String author;
	private String favorites;//no of favs
	private String isFavorite;//user's favorite?
	private String isOwner;//is user the author?
	private String groupVisibility;

	public Story(String favId,String SID,String storyId,String detail,String authorId,String author, String favorite, String isFav,String isOwner,String visibility){
		
		this.favId = favId;
		this.SID = SID;
		this.storyId = storyId;
		this.story = detail;
		this.authorId = authorId;
		this.author = author;
		this.favorites = favorite;
		this.isFavorite = isFav;
		this.isOwner = isOwner;
		this.groupVisibility = visibility;
	}
	
	public String getSID(){
		return SID;
	}
	
	public String getFavId(){
		return favId;
	}
	
	public String getStoryId(){
		return storyId;
	}
	
	public String getStory(){
		return this.story;
	}
	
	public String getAuthorId(){
		return authorId;
	}
	
	public String getAuthor(){
		return this.author;
	}
	
	public String getFavorite(){
		return this.favorites;
	}
	
	public String getIsFavorite(){
		return this.isFavorite;
	}
	
	public String getIsOwner(){
		return this.isOwner;
	}
	
	public String getVisibility(){
		return this.groupVisibility;
	}
	
}
