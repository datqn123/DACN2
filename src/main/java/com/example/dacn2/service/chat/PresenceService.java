package com.example.dacn2.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PresenceService {

    // Set lưu trữ danh sách ID của các Admin đang online
    // Dùng Set để tránh trùng lặp, ConcurrentHashMap để an toàn đa luồng
    private final Set<Long> onlineAdminIds = ConcurrentHashMap.newKeySet();

    public void addAdmin(Long adminId) {
        onlineAdminIds.add(adminId);
        log.info("Admin #{} is ONLINE. Total Online Admins: {}", adminId, onlineAdminIds.size());
    }

    public void removeAdmin(Long adminId) {
        onlineAdminIds.remove(adminId);
        log.info("Admin #{} is OFFLINE. Total Online Admins: {}", adminId, onlineAdminIds.size());
    }

    public boolean isAdminOnline(Long adminId) {
        return onlineAdminIds.contains(adminId);
    }

    public Set<Long> getOnlineAdmins() {
        return onlineAdminIds;
    }
}
