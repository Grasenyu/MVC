package com.gsy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Spring;

import com.gsy.annotation.Autowired;
import com.gsy.annotation.Controller;
import com.gsy.annotation.RequestMapping;
import com.gsy.service.StuService;



@Controller
public class StuController {
	
	@Autowired
	StuService stuservice;	
	
	@RequestMapping("/getstuall")
	public Object getAll(String name) {
			
		//将查询到的结果返回给前段页面
		return name;
		
	}
	

	
}
