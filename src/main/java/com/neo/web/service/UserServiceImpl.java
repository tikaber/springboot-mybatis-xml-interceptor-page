package com.neo.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.neo.entity.UserEntity;
import com.neo.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {
	
	@Resource
	UserMapper userMapper;

	@Override
	public List<UserEntity> queryStudentsByPage(int currPage, int pageSize) {
		Map<String, Object> data = new HashMap<>();
        data.put("currPage", currPage);
        data.put("pageSize", pageSize);
        return userMapper.queryStudentsByPage(data);
	}

}
