
package mypackage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Worker;
import mypackage.repository.Worker_Repository;

@Service
public class Worker_Service {

    @Autowired
    private Worker_Repository workerRepository;

    // Add Worker
    public Worker addWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    // Get All Workers
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    // Get Worker By ID
    public Worker getWorkerById(Long id) {
        return workerRepository.findById(id).orElse(null);
    }

    // Update Worker
    public Worker updateWorker(Long id, Worker worker) {

        Worker existingWorker = workerRepository.findById(id).orElse(null);

        if (existingWorker != null) {

            existingWorker.setJoining_date(worker.getJoining_date());
            existingWorker.setAvailability_status(worker.getAvailability_status());
            existingWorker.setUsers(worker.getUsers());

            return workerRepository.save(existingWorker);
        }

        return null;
    }

    // Delete Worker
    public void deleteWorker(Long id) {
        workerRepository.deleteById(id);
    }
}
