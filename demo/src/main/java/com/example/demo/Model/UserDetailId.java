package com.example.demo.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;

@Embeddable
@Data
public class UserDetailId {
    private Long index;
    private String userId;


}
