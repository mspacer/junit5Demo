package com.msp.junit.service;

import com.msp.junit.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "");
    private static final User PETR = User.of(2, "Petr", "");
    private static final User SERG = User.of(3, "Serg", "");

    /*static*/ int countTestExecuted;

    private UserService userService;

    @BeforeAll
    /*static*/ void beforeAll() {
        //System.out.println("beforeAll");
    }

    @BeforeEach
    void beforeEach() {
       // System.out.println("beforeEach + " + this);
        userService = new UserService();
       // System.out.println("userService + " + userService);
        countTestExecuted++;
    }

    @Test
    void usersEmptyIfNoAdded() {
        var all = userService.getAll();
        assertTrue(all.isEmpty(), "users should be empty");
    }

    @Test
    void userAdd() {
        userService.add(IVAN);
        userService.add(PETR);
        userService.add(SERG);

        var all = userService.getAll();
        //assertEquals(3, all.size());
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    void loginSuccessIfUserPresent(){
        userService.add(IVAN);

        Optional<User> user = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertThat(user).isPresent();
        user.ifPresent(user1 -> assertThat(user1).isEqualTo(IVAN));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> userMap = userService.getAllConvertedById();

        MatcherAssert.assertThat(userMap, IsMapContaining.hasKey(PETR.getId()));
        assertAll(
                () -> assertThat(userMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(userMap).containsValues(IVAN, PETR)
        );
    }

    @Test
    void throwExceptionIfLoginOrPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "")),
                () -> {
                    IllegalArgumentException aThrows = assertThrows(IllegalArgumentException.class, () -> userService.login("null", null));
                    assertThat(aThrows.getMessage()).isEqualTo("username or password is null");
                }
        );
/*
        try {
            userService.login(null, "");
            fail();
        } catch (IllegalArgumentException iex) {
            assertTrue(true);
        }
*/
    }

    @AfterEach
    void afterEach() {
        //System.out.println("afterEach " + this);
    }

    @AfterAll
    /*static*/ void afterAll() {
        //System.out.println("afterAll countTestExecuted= " + countTestExecuted);
    }
}
