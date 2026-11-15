
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Worker;
import mypackage.service.Worker_Service;

@RestController
@RequestMapping("/api/workers")
public class Worker_Controller {

    @Autowired
    private Worker_Service workerService;

    // Insert new worker
    @PostMapping
    public Worker saveWorker(@RequestBody Worker worker) {
        return workerService.saveWorker(worker);
    }

    // Get all workers
    @GetMapping
    public List<Worker> getAllWorkers() {
        return workerService.getAllWorkers();
    }

    // Get worker by ID
    @GetMapping("/{workerId}")
    public ResponseEntity<Worker> getWorkerById(
            @PathVariable Long workerId) {

        return workerService.getWorkerById(workerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update worker by ID
    @PutMapping("/{workerId}")
    public ResponseEntity<Worker> updateWorker(
            @PathVariable Long workerId,
            @RequestBody Worker worker) {

        return workerService.getWorkerById(workerId)
                .map(existingWorker -> {
                    worker.setWorkerId(workerId);
                    return ResponseEntity.ok(
                            workerService.saveWorker(worker));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete worker by ID
    @DeleteMapping("/{workerId}")
    public ResponseEntity<Void> deleteWorker(
            @PathVariable Long workerId) {

        return workerService.getWorkerById(workerId)
                .map(existingWorker -> {
                    workerService.deleteWorker(workerId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
