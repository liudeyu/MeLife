package com.gzmelife.app.bean;

import java.io.Serializable;


public class CookBookMenuBookStepsBean implements Serializable{
		private String id;
		private String minute;
		private String second;
		private String describes;
		private String foods;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getMinute() {
			return minute;
		}
		public void setMinute(String minute) {
			this.minute = minute;
		}
		public String getSecond() {
			return second;
		}
		public void setSecond(String second) {
			this.second = second;
		}
		public String getDescribes() {
			return describes;
		}
		public void setDescribes(String describes) {
			this.describes = describes;
		}
		public String getFoods() {
			return foods;
		}
		public void setFoods(String foods) {
			this.foods = foods;
		}
		
		
}
