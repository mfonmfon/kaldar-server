package com.kaldar.kaldar.domain.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class ServiceOfferings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clothesType;
    private BigDecimal unitPrice; //price per clothes or items
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

    public String getClothesType() {
        return clothesType;
    }

    public void setClothesType(String turnAroundHours) {
        this.clothesType = turnAroundHours;
    }

    public DryCleanerEntity getDryCleaner() {
        return dryCleaner;
    }

    public void setDryCleaner(DryCleanerEntity dryCleaner) {
        this.dryCleaner = dryCleaner;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getExpressAvailable() {
        return expressAvailable;
    }

    public void setExpressAvailable(Boolean expressAvailable) {
        this.expressAvailable = expressAvailable;
    }
}
