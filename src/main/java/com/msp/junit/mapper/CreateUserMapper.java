package com.msp.junit.mapper;

import com.msp.junit.dto.CreateUserDto;
import com.msp.junit.entity.Gender;
import com.msp.junit.entity.Role;
import com.msp.junit.entity.User;
import com.msp.junit.util.LocalDateFormatter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CreateUserMapper implements Mapper<CreateUserDto, User> {

    private static final CreateUserMapper INSTANCE = new CreateUserMapper();

    public static CreateUserMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User map(CreateUserDto object) {
        return User.builder()
                .name(object.getName())
                .birthday(LocalDateFormatter.format(object.getBirthday()))
                .email(object.getEmail())
                .password(object.getPassword())
                .gender(Gender.findOpt(object.getGender()).orElse(null))
                .role(Role.findOpt(object.getRole()).orElse(null))
                .build();
    }
}
