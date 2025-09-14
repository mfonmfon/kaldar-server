package com.kaldar.kaldar;

import com.kaldar.kaldar.dtos.request.AcceptOrderRequest;
import com.kaldar.kaldar.dtos.request.AcceptOrderResponse;
import com.kaldar.kaldar.dtos.request.DryCleanerRegistrationRequest;
import com.kaldar.kaldar.dtos.response.SendVerificationEmailResponse;
import com.kaldar.kaldar.kaldarService.interfaces.DryCleanerService;
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

    @Test
    public void testThatDryCleanerCanAcceptOrders(){
        Long orderId = 1L;
        Long dryCleanerId = 1L;
        AcceptOrderRequest acceptOrderRequest = new AcceptOrderRequest();
        acceptOrderRequest.setOrderId(orderId);
        acceptOrderRequest.setDryCleanerId(dryCleanerId);
//        acceptOrderRequest
        AcceptOrderResponse acceptOrderResponse = dryCleanerService.acceptOrder(acceptOrderRequest);
    }

}
