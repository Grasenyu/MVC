package com.gsy.mvc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.gsy.mvc.annotation.Autowired;
import com.gsy.mvc.annotation.Controller;
import com.gsy.mvc.annotation.RequestMapping;
import com.gsy.mvc.annotation.RequestParam;
import com.gsy.mvc.service.StuService;


@Controller
@RequestMapping("/stu")
public class StuController {
	
	@Autowired
	private StuService stuservice;	
	
	@RequestMapping("/getstuall")
	public void getAll(HttpServletRequest req, HttpServletResponse resp, @RequestParam("age") String name) throws IOException {
			
		PrintWriter writer = resp.getWriter();
		writer.write(stuservice.getStr(name));
		
	}   
	

	
}
