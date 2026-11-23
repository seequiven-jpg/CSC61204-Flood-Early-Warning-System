package com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example;

import java.util.List;

public interface EvacuationRepository {
    List<EvacuationCenter> getCentersByDistrict(String district);

    List<String> getAllDistricts();
}
