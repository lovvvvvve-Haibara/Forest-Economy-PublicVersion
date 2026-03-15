package edu.scau.forestproject.service;

import edu.scau.forestproject.dto.LoginRequest;
import edu.scau.forestproject.dto.RegisterRequest;
import edu.scau.forestproject.dto.UserBriefResponse;
import edu.scau.forestproject.pojo.LoginInfo;

public interface UserService {

    LoginInfo login(LoginRequest request);

    void delete(Integer id);

    void register(RegisterRequest request);

    UserBriefResponse getUserBrief(Integer id);
}
