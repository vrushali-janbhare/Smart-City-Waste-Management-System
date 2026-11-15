
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Worker;
import mypackage.repository.Worker_Repository;

@Service
public class Worker_Service {

    @Autowired
    private Worker_Repository workerRepository;

    // Insert or update worker
    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    // Get all workers
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    // Get worker by ID
    public Optional<Worker> getWorkerById(Long workerId) {
        return workerRepository.findById(workerId);
    }

    // Delete worker by ID
    public void deleteWorker(Long workerId) {
        workerRepository.deleteById(workerId);
    }
}
