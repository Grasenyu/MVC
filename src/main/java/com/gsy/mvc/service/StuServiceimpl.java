package com.gsy.mvc.service;

import java.util.List;

import com.gsy.mvc.annotation.Service;




@Service("stuservice") //iocMap.put("stuservice",new StuServiceimpl )
public class StuServiceimpl implements StuService {


	@Override
	public String getStr(String name) {

		return name;
	}
	
}
