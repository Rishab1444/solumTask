package com.example.demo.Repository;

import com.example.demo.Model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDetailRepository extends JpaRepository<UserDetail,Long> {

}
