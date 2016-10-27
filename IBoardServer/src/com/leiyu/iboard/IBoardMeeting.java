package com.leiyu.iboard;

import java.util.ArrayList;
import java.util.List;

public class IBoardMeeting {
	private String teacherName;
	private List<String> studentsList = new ArrayList<>();
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public List<String> getStudentsList() {
		List<String> rtnList = null;
		synchronized(this) {
			if (studentsList.size() == 0) {
				return null;
			}
			rtnList = new ArrayList<>();
			for(String student : studentsList) {
				rtnList.add(student);
			}
		}
		
		return rtnList;
	}
	public void addStudent(String student) {
		synchronized(this) {
			studentsList.add(student);
		}
	}
}
