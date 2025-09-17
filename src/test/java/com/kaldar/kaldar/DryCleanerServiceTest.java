package com.kaldar.kaldar;

import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.request.UpdateDryCleanerProfileRequest;
import com.kaldar.kaldar.dtos.response.DryCleanerProfileResponse;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.kaldarService.interfaces.DryCleanerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DryCleanerServiceTest {

    @Autowired
    private DryCleanerService dryCleanerService;



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

    @Test
    public void testThatDryCleanerCanUpdate_profile(){
        Long dryCleanerId = 1L;
        LocalDateTime updatedAt = LocalDateTime.now();
        UpdateDryCleanerProfileRequest updateDryCleanerProfileRequest = new UpdateDryCleanerProfileRequest();
        updateDryCleanerProfileRequest.setDryCleanerId(dryCleanerId);
        updateDryCleanerProfileRequest.setFirstName("Mfon");
        updateDryCleanerProfileRequest.setLastName("Mfon");
        updateDryCleanerProfileRequest.setBusinessName("MMPlus");
        updateDryCleanerProfileRequest.setBusinessPhoneNumber("081475494");
        updateDryCleanerProfileRequest.setShopAddress("Lagos, Nigeria");
        updateDryCleanerProfileRequest.setUpdatedAt(updatedAt);
        DryCleanerProfileResponse dryCleanerProfileResponse = dryCleanerService.editProfile(updateDryCleanerProfileRequest);
        assertThat(dryCleanerProfileResponse).isNotNull();
    }


}
