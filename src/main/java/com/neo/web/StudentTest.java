package com.neo.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neo.entity.Student;
import com.neo.mapper.UserMapper;
import com.neo.utils.SqlSessionFactoryUtil;

public class StudentTest {

	private SqlSession sqlSession = null;
	private UserMapper userMapper = null;
	
	@Before
	public void setUp() throws Exception{
		sqlSession = SqlSessionFactoryUtil.openSession();
		userMapper = sqlSession.getMapper(UserMapper.class);
	}
	
	@After
	public void tearDown() throws Exception{
		sqlSession.close();
	}

	@Test
	public void batchInsertStudentPage() {
		List<Student>list = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			Student student = new Student();
			student.setId(i);
			student.setName("test" + i);
			list.add(student);
		}
		try {
			save(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save(List<Student> uidCodeList) throws Exception {
		String s = "sadfsd";
		String[]st =new String[] {};
		
		SqlSession batchSqlSession = null;
		batchSqlSession = SqlSessionFactoryUtil.openSession();
		int batchCount = 1000;
		int batchLastIndex = batchCount -1;
		for (int index  = 0; index  < uidCodeList.size();) {
			if (batchLastIndex > uidCodeList.size() - 1) {
				batchLastIndex = uidCodeList.size()-1;
				batchSqlSession.insert("com.neo.mapper.batchInsertStudent",uidCodeList.subList(index, batchLastIndex+1));
				
				
			}
		}
		
	}
	
}
