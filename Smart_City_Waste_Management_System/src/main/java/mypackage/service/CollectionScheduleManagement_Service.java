
package mypackage.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mypackage.dto.CollectionScheduleRequestDTO;
import mypackage.dto.CollectionScheduleResponseDTO;
import mypackage.model.Area;
import mypackage.model.CollectionCrewMember;
import mypackage.model.CollectionSchedule;
import mypackage.model.CollectionScheduleDay;
import mypackage.model.Vehicle;
import mypackage.model.Worker;
import mypackage.repository.Area_Repository;
import mypackage.repository.CollectionCrewMember_Repository;
import mypackage.repository.CollectionRecord_Repository;
import mypackage.repository.CollectionScheduleDay_Repository;
import mypackage.repository.CollectionSchedule_Repository;
import mypackage.repository.Vehicle_Repository;
import mypackage.repository.Worker_Repository;

@Service
public class CollectionScheduleManagement_Service {

    @Autowired
    private CollectionSchedule_Repository scheduleRepository;

    @Autowired
    private CollectionScheduleDay_Repository dayRepository;

    @Autowired
    private CollectionCrewMember_Repository crewRepository;

    @Autowired
    private CollectionRecord_Repository recordRepository;

    @Autowired
    private Area_Repository areaRepository;

    @Autowired
    private Vehicle_Repository vehicleRepository;

    @Autowired
    private Worker_Repository workerRepository;

    @Transactional
    public CollectionScheduleResponseDTO createSchedule(
            CollectionScheduleRequestDTO request) {

        validateRequest(request);

        CollectionSchedule schedule = new CollectionSchedule();
        applyScheduleFields(schedule, request);

        schedule = scheduleRepository.save(schedule);
        saveDaysAndWorkers(schedule, request);

        return toResponse(schedule);
    }

    @Transactional
    public CollectionScheduleResponseDTO updateSchedule(
            Long scheduleId,
            CollectionScheduleRequestDTO request) {

        validateRequest(request);

        CollectionSchedule schedule = findSchedule(scheduleId);

        applyScheduleFields(schedule, request);

        scheduleRepository.save(schedule);

        dayRepository.deleteByScheduleScheduleId(scheduleId);
        crewRepository.deleteByScheduleScheduleId(scheduleId);

        dayRepository.flush();
        crewRepository.flush();

        saveDaysAndWorkers(schedule, request);

        return toResponse(schedule);
    }

    @Transactional
    public CollectionScheduleResponseDTO updateScheduleStatus(
            Long scheduleId, String status) {

        CollectionSchedule schedule = findSchedule(scheduleId);

        schedule.setScheduleStatus(normalizeStatus(status));

        scheduleRepository.save(schedule);

        return toResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        CollectionSchedule schedule = findSchedule(scheduleId);

        boolean hasRecords = recordRepository.findAll()
                .stream()
                .anyMatch(record ->
                        record.getSchedule() != null
                        && scheduleId.equals(
                                record.getSchedule().getScheduleId()));

        if (hasRecords) {
            throw new IllegalStateException(
                    "Cannot delete schedule because collection records exist");
        }

        dayRepository.deleteByScheduleScheduleId(scheduleId);
        crewRepository.deleteByScheduleScheduleId(scheduleId);

        dayRepository.flush();
        crewRepository.flush();

        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly = true)
    public List<CollectionScheduleResponseDTO> getAllSchedules() {

        List<CollectionScheduleResponseDTO> result =
                new ArrayList<>();

        for (CollectionSchedule schedule :
                scheduleRepository.findAll()) {

            result.add(toResponse(schedule));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public CollectionScheduleResponseDTO getScheduleById(
            Long scheduleId) {

        return toResponse(findSchedule(scheduleId));
    }

    private CollectionSchedule findSchedule(Long scheduleId) {

        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Schedule not found: " + scheduleId));
    }

    private void applyScheduleFields(
            CollectionSchedule schedule,
            CollectionScheduleRequestDTO request) {

        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Area not found"));

        Vehicle vehicle = vehicleRepository
                .findById(request.getVehicleId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Vehicle not found"));

        schedule.setScheduleName(request.getScheduleName().trim());
        schedule.setArea(area);
        schedule.setVehicle(vehicle);
        schedule.setStartDate(request.getStartDate());
        schedule.setEndDate(request.getEndDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setScheduleStatus(
                normalizeStatus(request.getScheduleStatus()));
    }

    private void saveDaysAndWorkers(
            CollectionSchedule schedule,
            CollectionScheduleRequestDTO request) {

        Set<String> uniqueDays = new HashSet<>();

        for (String day : request.getCollectionDays()) {
            uniqueDays.add(day.trim().toUpperCase());
        }

        for (String dayName : uniqueDays) {

            CollectionScheduleDay scheduleDay =
                    new CollectionScheduleDay();

            scheduleDay.setSchedule(schedule);
            scheduleDay.setDayOfWeek(dayName);

            dayRepository.save(scheduleDay);
        }

        Set<Long> uniqueWorkers =
                new HashSet<>(request.getWorkerIds());

        for (Long workerId : uniqueWorkers) {

            Worker worker = workerRepository.findById(workerId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Worker not found: " + workerId));

            CollectionCrewMember crew =
                    new CollectionCrewMember();

            crew.setSchedule(schedule);
            crew.setWorker(worker);

            crewRepository.save(crew);
        }
    }

    private String normalizeStatus(String status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Schedule status is required");
        }

        String normalized = status.trim().toUpperCase();

        if (!normalized.equals("ACTIVE")
                && !normalized.equals("INACTIVE")
                && !normalized.equals("COMPLETED")) {

            throw new IllegalArgumentException(
                    "Status must be ACTIVE, INACTIVE or COMPLETED");
        }

        return normalized;
    }

    private void validateRequest(
            CollectionScheduleRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule details are required");
        }

        if (request.getScheduleName() == null
                || request.getScheduleName().trim().isEmpty()
                || request.getScheduleName().trim().length() > 100) {

            throw new IllegalArgumentException(
                    "Schedule name must contain 1 to 100 characters");
        }

        if (request.getAreaId() == null
                || request.getVehicleId() == null) {

            throw new IllegalArgumentException(
                    "Area and vehicle are required");
        }

        if (request.getStartDate() == null
                || request.getStartTime() == null
                || request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Start date and collection times are required");
        }

        if (request.getEndDate() != null
                && request.getEndDate()
                        .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        normalizeStatus(request.getScheduleStatus());

        if (request.getCollectionDays() == null
                || request.getCollectionDays().isEmpty()) {

            throw new IllegalArgumentException(
                    "Select at least one collection day");
        }

        for (String day : request.getCollectionDays()) {

            if (day == null) {
                throw new IllegalArgumentException(
                        "Invalid collection day");
            }

            try {
                DayOfWeek.valueOf(day.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Invalid collection day: " + day);
            }
        }

        if (request.getWorkerIds() == null
                || request.getWorkerIds().isEmpty()
                || request.getWorkerIds().contains(null)) {

            throw new IllegalArgumentException(
                    "Select at least one valid worker");
        }
    }

    private CollectionScheduleResponseDTO toResponse(
            CollectionSchedule schedule) {

        CollectionScheduleResponseDTO response =
                new CollectionScheduleResponseDTO();

        response.setScheduleId(schedule.getScheduleId());
        response.setScheduleName(schedule.getScheduleName());

        response.setAreaId(schedule.getArea().getAreaId());
        response.setAreaName(schedule.getArea().getAreaName());

        response.setVehicleId(schedule.getVehicle().getVehicleId());
        response.setVehicleNumber(
                schedule.getVehicle().getVehicleNumber());

        response.setStartDate(schedule.getStartDate());
        response.setEndDate(schedule.getEndDate());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        response.setScheduleStatus(schedule.getScheduleStatus());

        List<String> days = new ArrayList<>();

        for (CollectionScheduleDay day :
                dayRepository.findByScheduleScheduleId(
                        schedule.getScheduleId())) {

            days.add(day.getDayOfWeek());
        }

        response.setCollectionDays(days);

        List<Long> workerIds = new ArrayList<>();

        for (CollectionCrewMember crew :
                crewRepository.findByScheduleScheduleId(
                        schedule.getScheduleId())) {

            workerIds.add(crew.getWorker().getWorkerId());
        }

        response.setWorkerIds(workerIds);

        return response;
    }
}
