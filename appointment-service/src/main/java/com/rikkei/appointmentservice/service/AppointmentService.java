package com.rikkei.appointmentservice.service;

import com.rikkei.appointmentservice.dto.ApiResponseError;
import com.rikkei.appointmentservice.dto.AppointmentRequest;
import com.rikkei.appointmentservice.dto.AppointmentResponse;
import com.rikkei.appointmentservice.dto.DoctorSummary;
import com.rikkei.appointmentservice.dto.PatientSummary;
import com.rikkei.appointmentservice.entity.Appointment;
import com.rikkei.appointmentservice.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorClientService doctorClientService;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorClientService doctorClientService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorClientService = doctorClientService;
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        verifyPatient(request.patientId());
        Object doctorResult;
        try {
            doctorResult = doctorClientService.getDoctor(request.doctorId());
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bác sĩ không tồn tại: " + request.doctorId(), ex);
        }
        boolean doctorAvailable = !(doctorResult instanceof ApiResponseError);
        if (!doctorAvailable && true) {
            ApiResponseError error = (ApiResponseError) doctorResult;
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, error.message());
        }
        Appointment appointment = new Appointment();
        appointment.setPatientId(request.patientId());
        appointment.setDoctorId(request.doctorId());
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setReason(request.reason());
        appointment.setStatus("PENDING");
        return toResponse(appointmentRepository.save(appointment));
    }

    private void verifyPatient(Long patientId) {
        try {
            Object result = doctorClientService.getPatient(patientId);
            if (result instanceof ApiResponseError error) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, error.message());
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bệnh nhân không tồn tại: " + patientId, ex);
        }
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(appointment.getId(), appointment.getPatientId(), appointment.getDoctorId(),
                appointment.getAppointmentDate(), appointment.getReason(), appointment.getStatus());
    }
}
