package com.example.demo.domain.service;

import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Transactional(rollbackFor = SQLException.class)
    public User UserUpdate(String username, String nickname, String phone, String zipcode, String addr1, String addr2) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        user.setNickname(nickname);
        user.setPhone(phone);
        user.setZipcode(zipcode);
        user.setAddr1(addr1);
        user.setAddr2(addr2);

        return userRepository.save(user);
    }

    @Transactional
    public boolean withdrawUser(String email, String password) {
        try {
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (userOptional.isPresent()) {
                User user = userOptional.get(); // Optional에서 User를 얻는 방법
                // 비밀번호 확인
                if (passwordEncoder.matches(password, user.getPassword())) {
                    userRepository.delete(user);
                    return true;
                }
            }
            return false; // 사용자를 찾지 못하거나 비밀번호가 일치하지 않음
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 탈퇴 실패
        }
    }
    @Transactional
    public void updateUserProfilePicture(String email, String profilePicturePath) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setProfile(profilePicturePath);
            userRepository.save(user);
        }
    }

    public boolean joinMember(UserDto dto, Model model, HttpServletRequest request) {

        //=======================================
        //Email 중복체크
        if (isEmailAlreadyTaken(dto.getEmail())) {
            model.addAttribute("email", "이미 사용중인 이메일입니다.");
            return false;
        }
        return false;
    }

    public boolean isEmailAlreadyTaken (String email){
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(email));
        return existingUser.isPresent();
    }

    public List<User> search_nickname(String keyword) {
        List<User> userList = userRepository.findByNickname(keyword);
        System.out.println("userList: " + userList);
        return userList;
    }


    public void updateProfile(User user) {

        userRepository.save(user);
    }

}