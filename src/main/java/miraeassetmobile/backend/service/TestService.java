package miraeassetmobile.backend.service;

import miraeassetmobile.backend.repository.TestRepository;
import miraeassetmobile.backend.domain.TestInfo;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestRepository testRepository;


    public TestService(TestRepository testRepository){
        this.testRepository = testRepository;
    }



    public TestInfo getTestInfo(int id){
        return testRepository.findById(id);
    }



}
