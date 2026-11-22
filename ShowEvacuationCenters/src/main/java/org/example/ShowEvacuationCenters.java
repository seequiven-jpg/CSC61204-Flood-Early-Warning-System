package org.example;

public class ShowEvacuationCenters {
    public static void main(String[] args) {
        EvacuationRepository repository = new JSONEvacuationRepository();

        EvacuationService service = new EvacuationService(repository);

        EvacuationController controller = new EvacuationController(service);

        controller.start();
    }
}
