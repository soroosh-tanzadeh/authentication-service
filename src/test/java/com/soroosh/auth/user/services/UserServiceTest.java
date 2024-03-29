package com.soroosh.auth.user.services;


import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.soroosh.auth.BaseTest;
import com.soroosh.auth.user.models.User;
import com.soroosh.auth.user.models.UserDto;
import com.soroosh.auth.user.services.otp.OTPService;
import jakarta.mail.internet.MimeMessage;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
class UserServiceTest extends BaseTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);
    @Autowired
    private OTPService otpService;

    @Autowired
    private UserService userService;

    @Test
    void test_create_user_withOtp() {
        String email = this.faker.internet().emailAddress();
        this.otpService.sendOtp(email);
        Awaitility.await().atMost(1, SECONDS).until(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            MimeMessage receivedMessage = receivedMessages[receivedMessages.length - 1];
            String code = GreenMailUtil.getBody(receivedMessage).replace("Your Verification Code is: ", "");

            User user = this.userService.createUserWithOtp(code, email, new UserDto(
                    this.faker.name().firstName(),
                    this.faker.name().lastName(),
                    email,
                    null,
                    this.faker.internet().password()
            ));
            UserDetails userDetails = this.userService.loadUserByUsername(email);
            Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
            return true;
        });
    }

    @Test
    void test_create_user() {
        String email = this.faker.internet().emailAddress();
        String mobile = this.faker.phoneNumber().phoneNumber();
        User user = this.userService.createUser(new UserDto(
                this.faker.name().firstName(),
                this.faker.name().lastName(),
                email,
                mobile,
                this.faker.internet().password()
        ));
        UserDetails userDetails = this.userService.loadUserByUsername(email);
        Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    void test_findByEmail() {
        String email = this.faker.internet().emailAddress();
        String mobile = this.faker.phoneNumber().phoneNumber();
        this.userService.createUser(new UserDto(
                this.faker.name().firstName(),
                this.faker.name().lastName(),
                email,
                mobile,
                this.faker.internet().password()
        ));

        Optional<User> userDetails = this.userService.findByEmail(email);
        Assertions.assertTrue(userDetails.isPresent());
        Assertions.assertEquals(email, userDetails.get().getEmail());
    }

    @Test
    void test_findByMobile() {
        String email = this.faker.internet().emailAddress();
        String mobile = this.faker.phoneNumber().phoneNumber();
        this.userService.createUser(new UserDto(
                this.faker.name().firstName(),
                this.faker.name().lastName(),
                email,
                mobile,
                this.faker.internet().password()
        ));

        Optional<User> userDetails = this.userService.findByMobile(mobile);
        Assertions.assertTrue(userDetails.isPresent());
        Assertions.assertEquals(email, userDetails.get().getEmail());
    }

    @Test
    void test_findById() {
        String email = this.faker.internet().emailAddress();
        String mobile = this.faker.phoneNumber().phoneNumber();
        User user = this.userService.createUser(new UserDto(
                this.faker.name().firstName(),
                this.faker.name().lastName(),
                email,
                mobile,
                this.faker.internet().password()
        ));

        User userDetails = this.userService.findById(user.getId());
        Assertions.assertEquals(email, userDetails.getEmail());
    }

}