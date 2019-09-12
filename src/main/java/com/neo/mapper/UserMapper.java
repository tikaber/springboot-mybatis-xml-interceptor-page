package com.neo.mapper;

import java.util.List;
import java.util.Map;

import com.neo.entity.Student;
import com.neo.entity.UserEntity;

public interface UserMapper {
	
	List<UserEntity> getAll();
	
	UserEntity getOne(Long id);

	void insert(UserEntity user);

	void update(UserEntity user);

	void delete(Long id);
	
	public int batchInsertStudent(List<Student> list);

	List<UserEntity> queryStudentsByPage(Map<String, Object> data);
}