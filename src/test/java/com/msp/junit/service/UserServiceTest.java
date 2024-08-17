package com.msp.junit.service;

import com.msp.junit.dto.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    /*static*/ int countTestExecuted;

    private UserService userService;

    @BeforeAll
    /*static*/ void beforeAll() {
        System.out.println("beforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach + " + this);
        userService = new UserService();
        System.out.println("userService + " + userService);
        countTestExecuted++;
    }

    @Test
    void usersEmptyIfNoAdded() {
        var all = userService.getAll();
        assertTrue(all.isEmpty(), "users should be empty");
    }

    @Test
    void userAdd() {
        userService.add(new User(1, "", ""));
        userService.add(new User(2, "", ""));
        userService.add(new User(3, "", ""));

        var all = userService.getAll();
        assertEquals(3, all.size());
    }

    @AfterEach
    void afterEach() {
        System.out.println("afterEach " + this);
    }

    @AfterAll
    /*static*/ void afterAll() {
        System.out.println("afterAll countTestExecuted= " + countTestExecuted);
    }
}
