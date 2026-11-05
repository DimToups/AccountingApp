package com.dimtoups.accountingApp.core.tests.controller;

import com.dimtoups.accountingApp.core.controller.dto.authentification.PostLoginRequestDto;
import com.dimtoups.accountingApp.core.controller.dto.authentification.PostSignupRequestDto;
import com.dimtoups.accountingApp.core.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import java.util.Map;

import static com.dimtoups.accountingApp.core.constants.DefaultUsersConstants.DEFAULT_PASSWORD;
import static com.dimtoups.accountingApp.core.constants.DefaultUsersConstants.DEFAULT_USERNAME;
import static com.dimtoups.accountingApp.core.constants.IncorrectCredentialFormatsConstants.*;
import static com.dimtoups.accountingApp.core.constants.UnknownUsersConstants.UNKNOWN_PASSWORD;
import static com.dimtoups.accountingApp.core.constants.UnknownUsersConstants.UNKNOWN_USERNAME;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@SqlGroup({
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateDefaultUser.sql"),
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteDefaultUser.sql")
})
public class AuthentificationControllerTests {

  //
  // Constants
  //

  public static final String JWT_REGEX_MATCHER = "(^[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)";
  public static final String CSRF_TOKEN_REGEX_MATCHER = "([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$)";


  //
  // Fields
  //

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

  private MockMvc mockMvc;


  //
  // Methods
  //

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .build();
  }


  //
  // Tests
  //

  @Nested
  public class LoginTests {

    //
    // Refactoring methods
    //

    private static String createJsonStringForLogin(String username, String password) {
      PostLoginRequestDto requestDto = new PostLoginRequestDto(username, password);
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      return ow.writeValueAsString(requestDto);
    }


    //
    // Tests
    //

    @Test
    public void isCsrfProtectionEnabled() throws Exception {
      // Given
      String body = createJsonStringForLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/login")
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void isJwtProducedWithCorrectUsernameAndPassword() throws Exception {
      // Given
      String body = createJsonStringForLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/login")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isReturnedJwtValid() throws Exception {
      // Given
      String body = createJsonStringForLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/login")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("token").value(Matchers.matchesRegex(JWT_REGEX_MATCHER)));
    }

    @Test
    public void isJwtNotProducedWithIncorrectPassword() throws Exception {
      // Given
      String body = createJsonStringForLogin(DEFAULT_USERNAME, UNKNOWN_PASSWORD);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/login")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isUnauthorized())
          .andExpect(MockMvcResultMatchers.jsonPath("token").doesNotExist());
    }

    @Test
    public void isJwtNotProducedWithIncorrectUsername() throws Exception {
      // Given
      String body = createJsonStringForLogin(UNKNOWN_USERNAME, DEFAULT_PASSWORD);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/login")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isUnauthorized())
          .andExpect(MockMvcResultMatchers.jsonPath("token").doesNotExist());
    }

    @Nested
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/CreateUsersWithIncorrectCredentialFormats.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/DeleteUsersWithIncorrectCredentialFormats.sql")
    })
    public class ParameterFormatsTests {

      @Test
      public void areEmptyUsernamesRefused() throws Exception {
        // Given
        String body = createJsonStringForLogin(EMPTY_USERNAME, EMPTY_PASSWORD);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/login")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("token").doesNotExist());
      }

      @Test
      public void areBlankUsernamesRefused() throws Exception {
        // Given
        String body = createJsonStringForLogin(BLANK_USERNAME, BLANK_PASSWORD);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/login")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("token").doesNotExist());
      }

      @Test
      public void areSpecialCharactersUsernamesRefused() throws Exception {
        // Given
        String body = createJsonStringForLogin(SPECIAL_CHARACTERS_USERNAME, SPECIAL_CHARACTERS_PASSWORD);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/login")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("token").doesNotExist());
      }

      @Test
      public void areLongUsernamesRefused() throws Exception {
        // Given
        String body = createJsonStringForLogin(LONG_USERNAME, LONG_PASSWORD);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/login")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("token").doesNotExist());
      }
    }
  }

  @Nested
  public class SignupTests {

    //
    // Refactoring methods
    //

    private static String createBody(String username, String password, String firstname, String lastname) {
      PostSignupRequestDto requestDto = new PostSignupRequestDto(username, password, firstname, lastname);
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      return ow.writeValueAsString(requestDto);
    }


    //
    // Tests
    //

    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/unknownUsers/DeleteUnknownUsers.sql")
    })
    @Test
    public void isUserCreatedWithValidUnknownCredentialsAndNames() throws Exception {
      // Given
      String body = createBody(UNKNOWN_USERNAME, UNKNOWN_PASSWORD, "firstname", "lastname");

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/signup")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isCreated());

      assertTrue(userRepository.findByUsername(UNKNOWN_USERNAME).isPresent());
    }

    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/unknownUsers/DeleteUnknownUsers.sql")
    })
    @Test
    public void isCsrfEnabled() throws Exception {
      // Given
      String body = createBody(UNKNOWN_USERNAME, UNKNOWN_PASSWORD, null, null);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/signup")
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isForbidden());

      assertTrue(userRepository.findByUsername(UNKNOWN_USERNAME).isEmpty());
    }

    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/unknownUsers/DeleteUnknownUsers.sql")
    })
    @Test
    public void isUserCreatedWithValidUnknownCredentialsAndNoNames() throws Exception {
      // Given
      String body = createBody(UNKNOWN_USERNAME, UNKNOWN_PASSWORD, null, null);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/signup")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isCreated());

      assertTrue(userRepository.findByUsername(UNKNOWN_USERNAME).isPresent());
    }

    @Test
    public void isUserNotCreatedWithExistingUsername() throws Exception {
      // Given
      String body = createBody(DEFAULT_USERNAME, UNKNOWN_PASSWORD, null, null);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/signup")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void isUserNotCreatedWithNullCredentials() throws Exception {
      // Given
      String body = createBody(null, null, null, null);

      // When
      mockMvc
          .perform(MockMvcRequestBuilders.post("/signup")
              .with(csrf().asHeader())
              .contentType("application/json;charset=UTF-8")
              .content(body))
          // Then
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      assertTrue(userRepository.findByUsername(null).isEmpty());
    }

    @Nested
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/DeleteUsersWithIncorrectCredentialFormats.sql")
    })
    public class ParameterFormatsTests {

      @Test
      public void isUserNotCreatedWithEmptyCredentials() throws Exception {
        // Given
        String body = createBody(EMPTY_USERNAME, EMPTY_PASSWORD, null, null);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/signup")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(userRepository.findByUsername(EMPTY_USERNAME).isEmpty());
      }

      @Test
      public void isUserNotCreatedWithBlankCredentials() throws Exception {
        // Given
        String body = createBody(BLANK_USERNAME, BLANK_PASSWORD, null, null);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/signup")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(userRepository.findByUsername(BLANK_USERNAME).isEmpty());
      }

      @Test
      public void isUserNotCreatedWithSpecialCharactersCredentials() throws Exception {
        // Given
        String body = createBody(SPECIAL_CHARACTERS_USERNAME, SPECIAL_CHARACTERS_PASSWORD, null, null);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/signup")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(userRepository.findByUsername(SPECIAL_CHARACTERS_USERNAME).isEmpty());
      }

      @Test
      public void isUserNotCreatedWithLongCredentials() throws Exception {
        // Given
        String body = createBody(LONG_USERNAME, LONG_PASSWORD, null, null);

        // When
        mockMvc
            .perform(MockMvcRequestBuilders.post("/signup")
                .with(csrf().asHeader())
                .contentType("application/json;charset=UTF-8")
                .content(body))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(userRepository.findByUsername(LONG_USERNAME).isEmpty());
      }
    }
  }

  @Nested
  public class CsrfTokenTests {


    //
    // Refactoring methods
    //

    private String getCsrfToken() throws Exception {
      String jsonStrResultOne = mockMvc
          .perform(MockMvcRequestBuilders.get("/csrfToken")
              .contentType("application/json;charset=UTF-8"))
          .andReturn()
          .getResponse()
          .getContentAsString();

      return (String) (new ObjectMapper().readValue(jsonStrResultOne, Map.class).get("token"));
    }


    //
    // Tests
    //

    @Test
    public void isCsrfDisabled() throws Exception {
      // Given /When
      mockMvc
          .perform(MockMvcRequestBuilders.get("/csrfToken")
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isReturnedTokenCsrfValid() throws Exception {
      // Given / When
      mockMvc
          .perform(MockMvcRequestBuilders.get("/csrfToken")
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("token").value(Matchers.matchesRegex(CSRF_TOKEN_REGEX_MATCHER)));
    }

    @Test
    public void areTokensRandom() throws Exception {
      // Given / When
      String tokenOne = getCsrfToken();
      String tokenTwo = getCsrfToken();

      // Then
      assertNotEquals(tokenOne, tokenTwo);
    }
  }
}
