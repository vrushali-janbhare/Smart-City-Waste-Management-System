
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Worker;
import mypackage.service.Worker_Service;

@RestController
@RequestMapping("/api/workers")
public class Worker_Controller {

    @Autowired
    private Worker_Service workerService;

    // Add Worker
    @PostMapping
    public Worker addWorker(@RequestBody Worker worker) {
        return workerService.addWorker(worker);
    }

    // Get All Workers
    @GetMapping
    public List<Worker> getAllWorkers() {
        return workerService.getAllWorkers();
    }

    // Get Worker By ID
    @GetMapping("/{id}")
    public Worker getWorkerById(@PathVariable Long id) {
        return workerService.getWorkerById(id);
    }

    // Update Worker
    @PutMapping("/{id}")
    public Worker updateWorker(
            @PathVariable Long id,
            @RequestBody Worker worker) {

        return workerService.updateWorker(id, worker);
    }

    // Delete Worker
    @DeleteMapping("/{id}")
    public String deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return "Worker Deleted Successfully!";
    }
}
