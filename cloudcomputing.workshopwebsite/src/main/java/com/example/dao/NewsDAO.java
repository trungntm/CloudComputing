package com.example.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.models.News;

public interface NewsDAO extends CrudRepository<News, Integer>{

}
