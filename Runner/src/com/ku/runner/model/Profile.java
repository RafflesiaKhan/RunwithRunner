package com.ku.runner.model;

public class Profile {

	
	private String  name, email,gender, imageUrl;
	private int _id, heightFt, heightInch, weight,age;
	
	
	
	public Profile(int _id, String name,  String gender, int age,int heightFt, int heightInch, int weight,
			String email, String imageUrl) {
		super();
		this._id = _id;
		this.name = name;
		this.email = email;
		this.gender = gender;
		this.heightFt = heightFt;
		this.heightInch = heightInch;
		this.weight = weight;
		this.imageUrl = imageUrl;
		this.age = age;
	}
	public Profile(String name,  String gender, int age,int heightFt, int heightInch, int weight,String email , String imageUrl) {
		super();
		this.name = name;
		this.email = email;
		this.gender = gender;
		this.heightFt = heightFt;
		this.heightInch = heightInch;
		this.weight = weight;
		this.age = age;this.imageUrl = imageUrl;
	}
	public Profile() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getHeightFt() {
		return heightFt;
	}
	public void setHeightFt(int heightFt) {
		this.heightFt = heightFt;
	}
	public int getHeightInch() {
		return heightInch;
	}
	public void setHeightInch(int heightInch) {
		this.heightInch = heightInch;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	} 
	
}
