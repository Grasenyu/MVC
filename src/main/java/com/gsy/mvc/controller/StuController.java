package com.gsy.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Spring;

import com.gsy.mvc.annotation.Autowired;
import com.gsy.mvc.annotation.Controller;
import com.gsy.mvc.annotation.RequestMapping;
import com.gsy.mvc.service.StuService;





@Controller
@RequestMapping("/stu")
public class StuController {
	
	@Autowired
	StuService stuservice;	
	
	@RequestMapping("/getstuall")
	public Object getAll(String name) {
			
		//将查询到的结果返回给前段页面
		return"fdsfdsa";
		
	}
	

	
}
