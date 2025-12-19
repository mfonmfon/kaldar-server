package com.kaldar.kaldar.customermdoule.domain.model;
import com.kaldar.kaldar.domain.entities.OrderEntity;
import com.kaldar.kaldar.usermdoule.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class CustomerEntity extends UserEntity {

    private String defaultAddress;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderEntity> orderEntityList;

    public List<OrderEntity> getOrderEntityList() {
        return orderEntityList;
    }

    public void setOrderEntityList(List<OrderEntity> orderEntityList) {
        this.orderEntityList = orderEntityList;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultPickupAddress) {
        this.defaultAddress = defaultPickupAddress;
    }

}
