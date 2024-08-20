package com.msp.junit.service;

import com.msp.junit.dao.UserDao;
import com.msp.junit.dao.UserDaoImpl;
import com.msp.junit.dto.User;
import com.msp.junit.extension.ConditionalExtension;
import com.msp.junit.extension.GlobalExtension;
import com.msp.junit.extension.PostProcessingExtension;
import com.msp.junit.extension.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
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
        MockitoExtension.class
        //ThrowableExtension.class
})
class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "111");
    private static final User PETR = User.of(2, "Petr", "222");
    private static final User SERG = User.of(3, "Serg", "333");

    @Spy
    private UserDao userDao;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<Integer> requestCaptor;

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
    void beforeEach() {
        // System.out.println("beforeEach + " + this);
    }

    @Test
    void deleteUserById() {
        System.out.println("userDao is mock: " + Mockito.mockingDetails(userDao).isMock());

        userService.add(IVAN);

        //Stab - объект для ответа на вызов метода
        //Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
        //Mockito.doReturn(true).when(userDao).delete(Mockito.anyInt());
        Mockito.when(userDao.delete(IVAN.getId()))
                /*.thenReturn(true)
                .thenReturn(false)*/
                .thenReturn(true, false);

        boolean isDeleted = userService.deleteUserById(IVAN.getId());

        assertThat(isDeleted).isTrue();

        // Верификация: метод был вызван с заданными параметрами
        Mockito.verify(userDao).delete(IVAN.getId());

        System.out.println("second call userService.deleteUserById(): " +
                userService.deleteUserById(IVAN.getId()));

        // Верификация: метод delete был вызван дважды
        Mockito.verify(userDao, Mockito.times(2))
                .delete(IVAN.getId());
        Mockito.verify(userDao, Mockito.never()).delete(PETR.getId());

        Mockito.doNothing().when(userDao).anyMethod();
        userService.anyMethod();

        // Тест проходит если посл двух вызовов delete идет вызов anyMethod
        Mockito.inOrder(userDao)
                .verify(userDao, Mockito.calls(2))
                .delete(IVAN.getId());
        Mockito.inOrder(userDao)
                .verify(userDao)
                .anyMethod();

        Mockito.reset(userDao);
        //задание условия аргументу: true будет только если deleteUserById вызывается с 2 и выше
        Mockito.when(userDao.delete(Mockito.argThat(arg -> arg > 1 )))
                .thenReturn(true);
        //assertThat(userService.deleteUserById(IVAN.getId())).isFalse();
        assertThat(userService.deleteUserById(SERG.getId())).isTrue();

        //Проверка аргумента. Вполне возможен вариант, что метод userService.deleteUserById
        // имеет логику в соответствии с которой userDao.delete() вызывается с другим аргументом.
        // существует возможность получить этот аргумент и проверить его
        Mockito.reset(userDao);
        userService.deleteUserByAnotherId(IVAN.getId());

        Mockito.verify(userDao, Mockito.times(1)).delete(requestCaptor.capture());
        assertThat(requestCaptor.getAllValues()).hasSize(1);
        Integer capturedArgument = requestCaptor.getValue();
        assertThat(capturedArgument).isEqualTo(25);
    }

    @Test
    void deleteUserByIdWithSpy() {
        userService.add(IVAN, PETR, SERG);

        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());

        assertThat(userService.deleteUserById(IVAN.getId())).isTrue();
    }

    @Test
    void whenExceptionThenReturnsError() throws SQLException {
        userService.add(IVAN);

        //тест пройдет, поскольку реальная реализация заменится дефольным значение - false
        System.out.println("userService.processConnection(): " + userService.processConnection());

        Mockito.when(userDao.getConnection()).thenReturn(true);
        String result = userService.processConnection();
        assertThat(result).isEqualTo("connection is ok");

        // Мокирование исключения
        Mockito.when(userDao.getConnection()).thenThrow(new SQLException());
        result = userService.processConnection();
        assertThat(result).isEqualTo("connection is fail");
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
        System.out.println("afterAll");
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
