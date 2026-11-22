package floodwarning.controller;
import floodwarning.view.DashboardView;
/** MVC controller placeholder for dashboard actions and validation. */
public final class DashboardController {
    private final DashboardView view;
    public DashboardController(DashboardView view) { this.view = view; }
    public void showMessage(String message) { view.showStatus(message); }
}
