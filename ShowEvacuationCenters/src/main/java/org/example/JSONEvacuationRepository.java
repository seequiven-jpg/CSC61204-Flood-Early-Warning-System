package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONEvacuationRepository implements EvacuationRepository {
    private final Map<String, List<String>> districtData;

    public JSONEvacuationRepository() {
        ObjectMapper mapper = new ObjectMapper();

        try(InputStream file = getClass().getResourceAsStream("/flood-centers.json")){
            if(file == null){
                throw new IllegalArgumentException("File not found");
            }
            districtData = mapper.readValue(file, new TypeReference<Map<String, List<String>>>() {});
        }
        catch (IOException e) {
            throw new IllegalArgumentException("File not found", e);
        }
    }
    @Override
    public List<EvacuationCenter> getCentersByDistrict(String district) {
        List<EvacuationCenter> centers = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : districtData.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(district.trim())){
                for(String name :  entry.getValue()){
                    centers.add(new EvacuationCenter(name));
                }
                break;
            }
        }
        return centers;
    }
    @Override
    public List<String> getAllDistricts() {

        return new ArrayList<>(districtData.keySet());
    }
}
