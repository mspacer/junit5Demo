package com.msp.junit.service;

import com.msp.junit.dto.User;
import com.msp.junit.extension.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("full")
@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith({
        GlobalExtension.class,
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        ThrowableExtension.class
})
class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "111");
    private static final User PETR = User.of(2, "Petr", "222");
    private static final User SERG = User.of(3, "Serg", "333");

    private int countTestExecuted;
    private UserService userService;
    @Includetest
    private UtilUnit utilUnit;

    UserServiceTest(TestInfo info) {
        System.out.println("UserServiceTest info: " + info);
    }

    @BeforeAll
    void beforeAll() {
        System.out.println("beforeAll. utilUnit: " + utilUnit);
    }

    @BeforeEach
    void beforeEach(UserService userService) {
        // System.out.println("beforeEach + " + this);
        this.userService = userService;
        // System.out.println("userService + " + userService);
        countTestExecuted++;
    }

    @Test
    @Tag("user")
    void usersEmptyIfNoAdded() {
        var all = userService.getAll();
        assertTrue(all.isEmpty(), "users should be empty");
    }

    @Test
    @Tag("user")
    @Order(1)
    @DisplayName("тест добавления пользователя")
    void userAdd() {
        userService.add(IVAN);
        userService.add(PETR);
        userService.add(SERG);

        var all = userService.getAll();
        //assertEquals(3, all.size());
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    @Tag("user")
    @Order(2)
    @DisplayName("Конвертация в Map")
    void usersConvertedToMapById(UserService userService) {
        userService.add(IVAN, PETR);
        Map<Integer, User> userMap = userService.getAllConvertedById();

        MatcherAssert.assertThat(userMap, IsMapContaining.hasKey(PETR.getId()));
        assertAll(
                () -> assertThat(userMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(userMap).containsValues(IVAN, PETR)
        );
    }

    @Test
    void testWithThrow() throws Exception {
        if (true) {
            throw new /*IOException*/RuntimeException();
        }
    }

    @AfterEach
    void afterEach() {
        //System.out.println("afterEach " + this);
    }

    @AfterAll
    void afterAll() {
        System.out.println("afterAll countTestExecuted= " + countTestExecuted);
    }

    @Nested
    @Tag("login")
    @DisplayName("тест логина")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class LoginTest {

        @Test
        @Tag("login")
        @DisplayName("поиск пользователя")
        void loginSuccessIfUserPresent() {
            userService.add(IVAN);

            Optional<User> user = userService.login(IVAN.getUsername(), IVAN.getPassword());

            user.ifPresent(user1 -> assertThat(user1).isEqualTo(IVAN));
        }

        @Test
        @DisplayName("поиск по некорректному логину")
        void loginFailIfPasswordIncorrect() {
            User maybeUser = userService.login(PETR.getUsername(), "dfsfsdf").orElse(null);
            assertThat(maybeUser).isNull();
        }

        @Test
        @DisplayName("тест исключения")
        void throwExceptionIfLoginOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "")),
                    () -> {
                        IllegalArgumentException aThrows = assertThrows(IllegalArgumentException.class, () -> userService.login("null", null));
                        assertThat(aThrows.getMessage()).isEqualTo("username or password is null");
                    }
            );
        }

        @ParameterizedTest
/*
        @NullSource
        @EmptySource
        @ValueSource(strings = {
            "Ivan", "Serg"
        })
*/
        //@MethodSource("getArgumentsForLoginTest")
        @MethodSource("com.msp.junit.service.UserServiceTest#getArgumentsForLoginTest2")
        void loginParameterizedTest(String username, String password, User validUser) {
            userService.add(IVAN, SERG);

            User user = userService.login(username, password).orElse(null);
            assertAll(
                    () -> assertNotNull(user),
                    () -> assertThat(user).isEqualTo(validUser)
            );
        }

        @ParameterizedTest
/*
        @CsvFileSource(resources = {
                "/login-test-data.csv"
        }, delimiter = ',', numLinesToSkip = 1)
*/
        @CsvSource({
                "Serg,333"
        })
        void loginParameterizedCsvTest(String username, String password) {
            userService.add(IVAN, PETR, SERG);

            User user = userService.login(username, password).orElse(null);
            assertNotNull(user);
        }

        // до 16 версии статические переменные и методы во внутренних (не статических) классах запрещены
        static Stream<Arguments> getArgumentsForLoginTest() {
            return Stream.<Arguments>builder()
                    .add(Arguments.of("Serg", "333", SERG))
                    .build();
        }
    }

    static Stream<Arguments> getArgumentsForLoginTest2() {
        return Stream.of(
                Arguments.of("Serg", "333", SERG),
                Arguments.of("Ivan", "111", IVAN)
        );
    }

}
