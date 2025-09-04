package com.kaldar.kaldar;

import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.DryCleanerRegistrationResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.kaldarService.DryCleanerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DryCleanerServiceTest {

    private final DryCleanerService dryCleanerService;

    public DryCleanerServiceTest(DryCleanerService dryCleanerService) {
        this.dryCleanerService = dryCleanerService;
    }

    @Test
    public void testThatDryCleanerCanRegister(){
        DryCleanerRegistrationRequest dryCleanerRegistrationRequest = new DryCleanerRegistrationRequest();
        dryCleanerRegistrationRequest.setFirstName("Mercy");
        dryCleanerRegistrationRequest.setLastName("Desmond");
        dryCleanerRegistrationRequest.setEmail("mfon@gmail.com");
        dryCleanerRegistrationRequest.setBusinessName("Business name ");
        dryCleanerRegistrationRequest.setBusinessPhoneNumber("09055433");
        dryCleanerRegistrationRequest.setBusinessEmail("business.co.gmail.com");
        dryCleanerRegistrationRequest.setShopAddress("Lekki phase 1, Lagos Nigeria");
        dryCleanerRegistrationRequest.setPassword("123456");
        SendVerificationEmailResponse dryCleanerRegistrationResponse = dryCleanerService.registerDryCleaner(dryCleanerRegistrationRequest);
        assertThat(dryCleanerRegistrationResponse).isNotNull();
    }
}
