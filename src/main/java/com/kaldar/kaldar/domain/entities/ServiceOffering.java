package com.kaldar.kaldar.domain.entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class ServiceOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serviceType;
    private String clothType;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "dry_cleaner_id")
    private DryCleanerEntity dryCleaner;
    private Boolean expressAvailable;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getClothType() {
        return clothType;
    }

    public void setClothType(String clothType) {
        this.clothType = clothType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public DryCleanerEntity getDryCleaner() {
        return dryCleaner;
    }

    public void setDryCleaner(DryCleanerEntity dryCleaner) {
        this.dryCleaner = dryCleaner;
    }

    public Boolean getExpressAvailable() {
        return expressAvailable;
    }

    public void setExpressAvailable(Boolean expressAvailable) {
        this.expressAvailable = expressAvailable;
    }

}
