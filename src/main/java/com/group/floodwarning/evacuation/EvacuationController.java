package com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example;

import java.util.List;
import java.util.Scanner;

public class EvacuationController {
    private final EvacuationService service;

    public EvacuationController(EvacuationService service) {
        if(service == null){
            throw new IllegalArgumentException("Evacuation service is null");
        }
        this.service = service;
    }
    public void start(){
        Scanner input = new Scanner(System.in);
        System.out.println("  FLOOD EVACUATION CENTERS    ");

        List<String> districts = service.getAvailableDistricts();

        while(true){
            System.out.println("\nAvailable Districts");

            for(int i=0; i<districts.size(); i++){
                System.out.println((i +1) + "." +districts.get(i));
            }
            System.out.println("0. Exit");

            System.out.println("\nEnter your district:");

            if(!input.hasNext()){
                System.out.println("Invalid input");
                input.nextLine();
                continue;
            }
            int choice = input.nextInt();
            if(choice == 0){
                System.out.println("Exit");
                break;
            }

            if(choice < 1 || choice > districts.size()){
                System.out.println("Invalid input");
                continue;
            }

            String selectedDistrict = districts.get(choice -1);
            List<EvacuationCenter> centers = service.findCentres(selectedDistrict);

            displayCenters(selectedDistrict, centers);
        }
    }
    private void displayCenters(String district, List<EvacuationCenter> centers){
        System.out.println("  EVACUATION CENTERS");
        System.out.println("district: " + district);

        if(centers.isEmpty()){
            System.out.println("No centers found");
            return;
        }

        for(int i = 0; i < centers.size(); i++){
            System.out.println((i +1) + "." +centers.get(i).getName());
        }
    }
}
