package com.kaldar.kaldar.dispatch.service;

import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryRequest;
import com.kaldar.kaldar.dispatch.application.dto.LogisticsDeliveryResponse;
import com.kaldar.kaldar.dispatch.application.service.impl.ChowdeckLogisticsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChowdeckLogisticsProvider Unit Tests")
class ChowdeckLogisticsProviderTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ChowdeckLogisticsProvider provider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(provider, "baseUrl", "https://api.chowdeck.com/relay");
        ReflectionTestUtils.setField(provider, "restTemplate", restTemplate);
    }

    private LogisticsDeliveryRequest buildRequest() {
        LogisticsDeliveryRequest request = new LogisticsDeliveryRequest();
        request.setOrderId(1L);
        request.setPickupAddress("Customer Address");
        request.setPickupName("Jane Customer");
        request.setPickupPhone("+2348000000000");
        request.setDropoffAddress("DryCleaner Address");
        request.setDropoffName("Express Drycleaners");
        request.setDropoffPhone("+2349000000000");
        return request;
    }

    @Test
    @DisplayName("should fallback to simulated mockup response when API key is empty")
    void shouldFallbackWhenApiKeyIsEmpty() {
        ReflectionTestUtils.setField(provider, "apiKey", "");

        LogisticsDeliveryRequest request = buildRequest();
        LogisticsDeliveryResponse response = provider.requestDelivery(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("preparing");
        assertThat(response.getExternalDeliveryId()).contains("chowdeck-mock");
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("should cancel delivery successfully in mockup mode when API key is empty")
    void shouldCancelSuccessfullyInMockMode() {
        ReflectionTestUtils.setField(provider, "apiKey", "");

        provider.cancelDelivery("some-mock-reference");
        verifyNoInteractions(restTemplate);
    }
}
