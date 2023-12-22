package com.example.patterns.service;

import com.example.patterns.model.Charity;
import com.example.patterns.model.User;
import com.example.patterns.repository.CharityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharityService {
    @Autowired
    private CharityRepository charityRepository;

    public void saveCharity(Charity charity) {
        charityRepository.save(charity);
    }

    public List<Charity> getCharitiesByUser(User user) {
        return charityRepository.findAllByUser(user);
    }

    public List<Charity> getCharities() {
        return charityRepository.findAll();
    }

    public Charity getCharity(Long charityId) {
        return charityRepository.findById(charityId).orElse(null);

    }

    public void deleteCharity(Long charityId) {
        charityRepository.deleteById(charityId);
    }

    public void markHandled(Long charityId) {
        Charity charity = charityRepository.findById(charityId).orElse(null);
        if(charity != null){
            charity.setHandled(true);
            charityRepository.save(charity);
        }

    }
}
