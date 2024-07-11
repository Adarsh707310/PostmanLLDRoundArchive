package org.example.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RateLimitWrapper {

    private final List<ApiInstances> listIpInstances;
    private final Long timeWindow = 15000L;
    private final Integer hitsAllowed = 3;

    public RateLimitWrapper() {
        this.listIpInstances = new ArrayList<>();
    }

    public Boolean getRateLimitExceeded(String ip) {

        ApiInstances currentIpInstance = listIpInstances.stream().filter(each -> each.getIp().equals(ip)).findFirst().orElse(null);

        if(Objects.isNull(currentIpInstance)){
            ApiInstances newApiInstance = new ApiInstances();
            newApiInstance.setIp(ip);
            newApiInstance.setOldInstances(new ArrayList<>());
            newApiInstance.getOldInstances().add(System.currentTimeMillis());
            listIpInstances.add(newApiInstance);
            return false;
        }

        removeExpiredInstances(currentIpInstance);

        if(currentIpInstance.getOldInstances().size() >= hitsAllowed){
            // throw new RuntimeException("IP Already Accessed the Api for max permit, ip: "+ip); -> log
            return true;
        }

        currentIpInstance.getOldInstances().add(System.currentTimeMillis());

        return false;
    }

    private void removeExpiredInstances(ApiInstances currentIpInstance) {
        List<Long> unExpiredOldInstances = new ArrayList<>();

        for(Long instances: currentIpInstance.getOldInstances()){
            if(instances + timeWindow > System.currentTimeMillis()){
                unExpiredOldInstances.add(instances);
            }
        }
        currentIpInstance.setOldInstances(unExpiredOldInstances);
    }

}

// allowed 5
// window 40
// ip1 -> 1 27 28 29 30 41 42 43 44

//  27 28 29 30 41
