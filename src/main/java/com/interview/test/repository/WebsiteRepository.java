package com.interview.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.test.entity.Websites;

@Repository
public interface WebsiteRepository extends JpaRepository<Websites, String>{

}
