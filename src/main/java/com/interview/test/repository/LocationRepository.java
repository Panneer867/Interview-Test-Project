package com.interview.test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.interview.test.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

	List<Location> findByCityName(String cityName);

}
