package com.dimtoups.accountingApp.core.tests.controller;

import com.dimtoups.accountingApp.core.controller.dto.users.DeleteUserRequestDto;
import com.dimtoups.accountingApp.core.controller.dto.users.UpdateUserRequestDto;
import com.dimtoups.accountingApp.core.helper.JwtHelper;
import com.dimtoups.accountingApp.core.repository.UserRepository;
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

import static com.dimtoups.accountingApp.core.constants.DefaultUsersConstants.*;
import static com.dimtoups.accountingApp.core.constants.IncorrectCredentialFormatsConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@SqlGroup({
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateDefaultUser.sql"),
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteDefaultUser.sql")
})
public class UserControllerTests {

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
  public class GetUser {

    @Nested
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/CreateUsersWithIncorrectCredentialFormats.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/DeleteUsersWithIncorrectCredentialFormats.sql")
    })
    public class ParameterFormatsTests {
      @Test
      public void areEmptyUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(EMPTY_USERNAME);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", EMPTY_USERNAME)
                .header("Authorization", String.format("Bearer %s", jwt))
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());
      }

      @Test
      public void areBlankUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(BLANK_USERNAME);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", BLANK_USERNAME)
                .header("Authorization", String.format("Bearer %s", jwt))
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());
      }

      @Test
      public void areSpecialCharactersUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(SPECIAL_CHARACTERS_USERNAME);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", SPECIAL_CHARACTERS_USERNAME)
                .header("Authorization", String.format("Bearer %s", jwt))
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());
      }

      @Test
      public void areLongUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(LONG_USERNAME);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", LONG_USERNAME)
                .header("Authorization", String.format("Bearer %s", jwt))
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());
      }
    }


    @Test
    public void isJwtProtectionActivated() throws Exception {
      // Given /

      // When
      mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", DEFAULT_USERNAME)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void isUserAccessibleWithCsrfAndJwtWithCorrectParamUsername() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);

      // When
      mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", DEFAULT_USERNAME)
              .header("Authorization", String.format("Bearer %s", jwt))
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("firstname").isNotEmpty())
          .andExpect(MockMvcResultMatchers.jsonPath("lastname").isNotEmpty());
    }

    @Test
    public void isPasswordNotSent() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);

      // When
      mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", DEFAULT_USERNAME)
              .header("Authorization", String.format("Bearer %s", jwt))
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("password").doesNotExist());
    }

    @Test
    public void isUsernameNotSent() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);

      // When
      mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", DEFAULT_USERNAME)
              .header("Authorization", String.format("Bearer %s", jwt))
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("username").doesNotExist());
    }

    @Test
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateSecondaryDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteSecondaryDefaultUser.sql")
    })
    public void isUserNotAccessibleWithUnmatchingJwt() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(SECONDARY_DEFAULT_USERNAME);

      // When
      mockMvc.perform(MockMvcRequestBuilders.get("/user/{username}", DEFAULT_USERNAME)
              .header("Authorization", String.format("Bearer %s", jwt))
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isUnauthorized())
          .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
          .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());
    }
  }

  @Nested
  public class UpdateUser {

    //
    // Refactoring methods
    //

    private String createJsonStringForUpdate(UpdateUserRequestDto updateUserRequestDto) {
      return new ObjectMapper().writer().writeValueAsString(updateUserRequestDto);
    }


    //
    // Tests
    //

    @Test
    public void areUserNamesUpdatedWithCsrfAndJwtAndWithCorrectBody() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
      String newFirstname = "Flammekueche";
      String newLastname = "Munster";
      String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

      // When
      mockMvc.perform(MockMvcRequestBuilders.patch("/user")
              .with(csrf().asHeader())
              .header("Authorization", String.format("Bearer %s", jwt))
              .content(body)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
          .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

      assertEquals(newFirstname, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
      assertEquals(newLastname, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
    }

    @Test
    public void isCsrfProtectionEnabled() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
      String newFirstname = "Flammekueche";
      String newLastname = "Munster";
      String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

      // When
      mockMvc.perform(MockMvcRequestBuilders.patch("/user")
              .header("Authorization", String.format("Bearer %s", jwt))
              .content(body)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void isJwtProtectionEnabled() throws Exception {
      // Given
      String newFirstname = "Flammekueche";
      String newLastname = "Munster";
      String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

      // When
      mockMvc.perform(MockMvcRequestBuilders.patch("/user")
              .with(csrf().asHeader())
              .content(body)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void areNullValuesIgnored() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
      String defaultFirstname = DEFAULT_FIRSTNAME;
      String defaultLastname = DEFAULT_LASTNAME;
      String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, null, null));

      // When
      mockMvc.perform(MockMvcRequestBuilders.patch("/user")
              .with(csrf().asHeader())
              .header("Authorization", String.format("Bearer %s", jwt))
              .content(body)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
          .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

      assertEquals(defaultFirstname, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
      assertEquals(defaultLastname, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
    }

    @Test
    public void areEmptyValuesAccepted() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
      String newFirstname = "";
      String newLastname = "";
      String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

      // When
      mockMvc.perform(MockMvcRequestBuilders.patch("/user")
              .with(csrf().asHeader())
              .header("Authorization", String.format("Bearer %s", jwt))
              .content(body)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
          .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

      assertNull(userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
      assertNull(userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
    }

    @Test
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateSecondaryDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteSecondaryDefaultUser.sql")
    })
    public void isRequestNotAccessibleWithUnmatchingJwtAndUsername() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(SECONDARY_DEFAULT_USERNAME);
      String newFirstname = "Flammekueche";
      String newLastname = "Munster";
      String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

      // When
      mockMvc.perform(MockMvcRequestBuilders.patch("/user")
              .with(csrf().asHeader())
              .header("Authorization", String.format("Bearer %s", jwt))
              .content(body)
              .contentType("application/json;charset=UTF-8"))
          // Then
          .andExpect(MockMvcResultMatchers.status().isUnauthorized())
          .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
          .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

      assertEquals(DEFAULT_FIRSTNAME, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
      assertEquals(DEFAULT_LASTNAME, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
    }

    @Nested
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/CreateUsersWithIncorrectCredentialFormats.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/DeleteUsersWithIncorrectCredentialFormats.sql")
    })
    public class UsernameParameterFormatTests {
      @Test
      public void areBlankUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(BLANK_USERNAME);
        String newFirstname = "Flammekueche";
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(BLANK_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertNull(userRepository.findByUsername(BLANK_USERNAME).get().getFirstname());
        assertNull(userRepository.findByUsername(BLANK_USERNAME).get().getLastname());
      }

      @Test
      public void areEmptyUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(EMPTY_USERNAME);
        String newFirstname = "Flammekueche";
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(EMPTY_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertNull(userRepository.findByUsername(EMPTY_USERNAME).get().getFirstname());
        assertNull(userRepository.findByUsername(EMPTY_USERNAME).get().getLastname());
      }

      @Test
      public void areSpecialCharactersUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(SPECIAL_CHARACTERS_USERNAME);
        String newFirstname = "Flammekueche";
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(SPECIAL_CHARACTERS_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertNull(userRepository.findByUsername(SPECIAL_CHARACTERS_USERNAME).get().getFirstname());
        assertNull(userRepository.findByUsername(SPECIAL_CHARACTERS_USERNAME).get().getLastname());
      }

      @Test
      public void areLongUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(LONG_USERNAME);
        String newFirstname = "Flammekueche";
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(LONG_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertNull(userRepository.findByUsername(LONG_USERNAME).get().getFirstname());
        assertNull(userRepository.findByUsername(LONG_USERNAME).get().getLastname());
      }
    }

    @Nested
    public class FirstnameParameterFormatTests {

      @Test
      public void isUserFirstnameNotUpdatedWithNullValue() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
        String defaultFirstname = DEFAULT_FIRSTNAME;
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, null, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertEquals(defaultFirstname, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
        assertEquals(newLastname, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
      }

      @Test
      public void isUserFirstnameSetToNullWithEmptyValue() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
        String newFirstname = "";
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertNull(userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
        assertEquals(newLastname, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
      }

      @Test
      public void areLongFirstnamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
        String newFirstname = LONG_FIRSTNAME;
        String newLastname = "Munster";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertEquals(DEFAULT_FIRSTNAME, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
        assertEquals(DEFAULT_LASTNAME, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
      }
    }

    @Nested
    public class LastnameParameterFormatTests {

      @Test
      public void isUserLastnameNotUpdatedWithNullValue() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
        String newFirstname = "Flammekueche";
        String defaultLastname = DEFAULT_LASTNAME;
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, null));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertEquals(newFirstname, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
        assertEquals(defaultLastname, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
      }

      @Test
      public void isUserLastnameSetToNullWithEmptyValue() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
        String newFirstname = "Flammekueche";
        String newLastname = "";
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").doesNotExist());

        assertEquals(newFirstname, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
        assertNull(userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
      }

      @Test
      public void areLongLastnamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
        String newFirstname = "Flammekueche";
        String newLastname = LONG_LASTNAME;
        String body = createJsonStringForUpdate(new UpdateUserRequestDto(DEFAULT_USERNAME, newFirstname, newLastname));

        // When
        mockMvc.perform(MockMvcRequestBuilders.patch("/user")
                .with(csrf().asHeader())
                .header("Authorization", String.format("Bearer %s", jwt))
                .content(body)
                .contentType("application/json;charset=UTF-8"))
            // Then
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("firstname").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("lastname").isNotEmpty());

        assertEquals(DEFAULT_FIRSTNAME, userRepository.findByUsername(DEFAULT_USERNAME).get().getFirstname());
        assertEquals(DEFAULT_LASTNAME, userRepository.findByUsername(DEFAULT_USERNAME).get().getLastname());
      }
    }
  }

  @Nested
  public class DeleteUser {
    private String createJsonStringForDeletion(DeleteUserRequestDto dto) {
      return new ObjectMapper().writer().writeValueAsString(dto);
    }

    @Test
    public void isUserDeletedWithCorrectUsername() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
      String body = createJsonStringForDeletion(new DeleteUserRequestDto(DEFAULT_USERNAME));

      // When
      mockMvc.perform(
              MockMvcRequestBuilders.delete("/user")
                  .with(csrf().asHeader())
                  .header("Authorization", String.format("Bearer %s", jwt))
                  .contentType("application/json;charset=UTF-8")
                  .content(body))
          .andExpect(MockMvcResultMatchers.status().isOk());

      assertTrue(userRepository.findByUsername(DEFAULT_USERNAME).isEmpty());
    }

    @Test
    public void isCsrfProtectionActivated() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(DEFAULT_USERNAME);
      String body = createJsonStringForDeletion(new DeleteUserRequestDto(DEFAULT_USERNAME));

      // When
      mockMvc.perform(
              MockMvcRequestBuilders.delete("/user")
                  .header("Authorization", String.format("Bearer %s", jwt))
                  .contentType("application/json;charset=UTF-8")
                  .content(body))
          .andExpect(MockMvcResultMatchers.status().isForbidden());

      assertTrue(userRepository.findByUsername(DEFAULT_USERNAME).isPresent());
    }

    @Test
    public void isJwtProtectionActivated() throws Exception {
      // Given
      String body = createJsonStringForDeletion(new DeleteUserRequestDto(DEFAULT_USERNAME));

      // When
      mockMvc.perform(
              MockMvcRequestBuilders.delete("/user")
                  .with(csrf())
                  .contentType("application/json;charset=UTF-8")
                  .content(body))
          .andExpect(MockMvcResultMatchers.status().isUnauthorized());

      assertTrue(userRepository.findByUsername(DEFAULT_USERNAME).isPresent());
    }

    @Test
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/default/CreateSecondaryDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteDefaultUser.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/default/DeleteSecondaryDefaultUser.sql")
    })
    public void isRequestNotAccessibleWithUnmatchingJwtAndUsername() throws Exception {
      // Given
      String jwt = JwtHelper.generateToken(SECONDARY_DEFAULT_USERNAME);
      String body = createJsonStringForDeletion(new DeleteUserRequestDto(DEFAULT_USERNAME));

      // When
      mockMvc.perform(
              MockMvcRequestBuilders.delete("/user")
                  .with(csrf().asHeader())
                  .header("Authorization", String.format("Bearer %s", jwt))
                  .contentType("application/json;charset=UTF-8")
                  .content(body))
          .andExpect(MockMvcResultMatchers.status().isUnauthorized());

      assertTrue(userRepository.findByUsername(DEFAULT_USERNAME).isPresent());
    }

    @Nested
    @SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/CreateUsersWithIncorrectCredentialFormats.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sampleDatabases/incorrectCredentials/DeleteUsersWithIncorrectCredentialFormats.sql")
    })
    public class ParameterFormatTests {

      @Test
      public void areEmptyUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(EMPTY_USERNAME);
        String body = createJsonStringForDeletion(new DeleteUserRequestDto(EMPTY_USERNAME));

        // When
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user")
                    .with(csrf().asHeader())
                    .header("Authorization", String.format("Bearer %s", jwt))
                    .contentType("application/json;charset=UTF-8")
                    .content(body))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        assertTrue(userRepository.findByUsername(EMPTY_USERNAME).isPresent());
      }

      @Test
      public void areBlankUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(BLANK_USERNAME);
        String body = createJsonStringForDeletion(new DeleteUserRequestDto(BLANK_USERNAME));

        // When
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user")
                    .with(csrf().asHeader())
                    .header("Authorization", String.format("Bearer %s", jwt))
                    .contentType("application/json;charset=UTF-8")
                    .content(body))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        assertTrue(userRepository.findByUsername(BLANK_USERNAME).isPresent());
      }

      @Test
      public void areSpecialCharacterUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(SPECIAL_CHARACTERS_USERNAME);
        String body = createJsonStringForDeletion(new DeleteUserRequestDto(SPECIAL_CHARACTERS_USERNAME));

        // When
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user")
                    .with(csrf().asHeader())
                    .header("Authorization", String.format("Bearer %s", jwt))
                    .contentType("application/json;charset=UTF-8")
                    .content(body))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(userRepository.findByUsername(SPECIAL_CHARACTERS_USERNAME).isPresent());
      }

      @Test
      public void areLongUsernamesRefused() throws Exception {
        // Given
        String jwt = JwtHelper.generateToken(LONG_USERNAME);
        String body = createJsonStringForDeletion(new DeleteUserRequestDto(LONG_USERNAME));

        // When
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user")
                    .with(csrf().asHeader())
                    .header("Authorization", String.format("Bearer %s", jwt))
                    .contentType("application/json;charset=UTF-8")
                    .content(body))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(userRepository.findByUsername(LONG_USERNAME).isPresent());
      }
    }
  }
}
