package com.example.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.HomepageDAO;
import com.example.models.HomePage;

@Service("homepageService")
@Transactional
public class HomepageServiceImpl implements HomepageService {
	@Autowired
	public HomepageDAO homepageDAO;
	
	public void savePage(HomePage homepage) {
		homepageDAO.save(homepage);
	}
	
	public HomePage findHomeNews(int id) {
		return homepageDAO.findById(id).get();
	}
	
	public List<HomePage> findAllHomeNewses() {
		List<HomePage> lstNewse=new ArrayList<HomePage>();
		for(HomePage news:homepageDAO.findAll()) {
			lstNewse.add(news);
		}
		return lstNewse;
	}
	
}
