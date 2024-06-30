package com.example.springbatch.part5;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private int amount;

    private LocalDate createdDate;

    private Long userId;

    @Builder
    public Orders(String itemName, int amount, LocalDate createdDate) {
        this.itemName = itemName;
        this.amount = amount;
        this.createdDate = createdDate;
    }
}
