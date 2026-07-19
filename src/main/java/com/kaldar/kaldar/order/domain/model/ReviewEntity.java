package com.kaldar.kaldar.order.domain.model;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ReviewEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "dry_cleaner_id")
    private DryCleanerEntity dryCleaner;

    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }

    public DryCleanerEntity getDryCleaner() { return dryCleaner; }
    public void setDryCleaner(DryCleanerEntity dryCleaner) { this.dryCleaner = dryCleaner; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
