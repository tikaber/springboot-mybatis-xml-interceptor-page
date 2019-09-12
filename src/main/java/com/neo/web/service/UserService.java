package com.neo.web.service;

import java.util.List;

import com.neo.entity.UserEntity;

public interface UserService {

	List<UserEntity> queryStudentsByPage(int currPage,int pageSize);
}
