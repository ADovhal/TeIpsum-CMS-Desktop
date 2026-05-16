package com.teipsum.shopcmsdesktop.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record AdminRegisterRequest(
        String email,
        String password,
        String name,
        String surname,
        String phone,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dob
) {}
