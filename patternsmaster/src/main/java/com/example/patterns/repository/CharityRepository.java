package com.example.patterns.repository;
import com.example.patterns.model.Charity;
import com.example.patterns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharityRepository extends JpaRepository<Charity,Long>{
    List<Charity> findAllByUser(User user);
}
