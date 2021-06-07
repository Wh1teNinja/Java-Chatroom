package ca.senecacollege.ws9;

public class RoomData {
	String title;
	String description;
	int peopleNum;
	int maxSlots;
	
	public RoomData(String title, String description, int peopleNum, int maxSlots) {
		this.title = title;
		this.description = description;
		this.peopleNum = peopleNum;
		this.maxSlots = maxSlots;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPeopleNum() {
		return peopleNum;
	}
	public void setPeopleNum(int peopleNum) {
		this.peopleNum = peopleNum;
	}
	public int getMaxSlots() {
		return maxSlots;
	}
	public void setMaxSlots(int maxSlots) {
		this.maxSlots = maxSlots;
	}
	public String getStringSlots() {
		return getPeopleNum() + "/" + getMaxSlots();
	}
}
