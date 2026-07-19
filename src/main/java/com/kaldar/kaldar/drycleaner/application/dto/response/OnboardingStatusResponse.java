package com.kaldar.kaldar.drycleaner.application.dto.response;

public class OnboardingStatusResponse {
    private boolean businessVerified;
    private boolean storeProfileSetup;
    private boolean payoutAccountAdded;
    private boolean businessOperationsSetup;
    private boolean storeInventorySetup;

    public OnboardingStatusResponse() {}

    public boolean isBusinessVerified() { return businessVerified; }
    public void setBusinessVerified(boolean businessVerified) { this.businessVerified = businessVerified; }

    public boolean isStoreProfileSetup() { return storeProfileSetup; }
    public void setStoreProfileSetup(boolean storeProfileSetup) { this.storeProfileSetup = storeProfileSetup; }

    public boolean isPayoutAccountAdded() { return payoutAccountAdded; }
    public void setPayoutAccountAdded(boolean payoutAccountAdded) { this.payoutAccountAdded = payoutAccountAdded; }

    public boolean isBusinessOperationsSetup() { return businessOperationsSetup; }
    public void setBusinessOperationsSetup(boolean businessOperationsSetup) { this.businessOperationsSetup = businessOperationsSetup; }

    public boolean isStoreInventorySetup() { return storeInventorySetup; }
    public void setStoreInventorySetup(boolean storeInventorySetup) { this.storeInventorySetup = storeInventorySetup; }
}
