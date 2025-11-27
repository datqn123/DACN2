package com.example.dacn2.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionRequest {
    private List<String> permissionNames;
}
