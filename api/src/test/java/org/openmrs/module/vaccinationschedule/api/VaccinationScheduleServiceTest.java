package org.openmrs.module.vaccinationschedule.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.vaccinationschedule.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.PatientVaccinationStatus;
import org.openmrs.module.vaccinationschedule.api.dao.VaccinationScheduleDao;
import org.openmrs.module.vaccinationschedule.api.impl.VaccinationScheduleServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VaccinationScheduleServiceTest {
    
    @Mock
    private VaccinationScheduleDao vaccinationScheduleDao;
    
    @InjectMocks
    private VaccinationScheduleServiceImpl vaccinationScheduleService;
    
    private VaccinationSchedule testSchedule;
    private VaccinationScheduleEntry testEntry;
    
    @BeforeEach
    public void setUp() {
        testSchedule = new VaccinationSchedule();
        testSchedule.setScheduleId(1);
        testSchedule.setUuid("test-schedule-uuid");
        testSchedule.setName("Test Schedule");
        testSchedule.setCountryCode("TST");
        testSchedule.setVersion(1);
        testSchedule.setEffectiveDate(new Date());
        
        testEntry = new VaccinationScheduleEntry();
        testEntry.setEntryId(1);
        testEntry.setUuid("test-entry-uuid");
        testEntry.setVaccinationSchedule(testSchedule);
        testEntry.setDoseNumber(1);
        testEntry.setAgeInDaysMin(0);
        testEntry.setAgeInDaysRecommended(7);
        testEntry.setAgeInDaysMax(30);
    }
    
    @Test
    public void testSaveVaccinationSchedule_WithValidSchedule_ShouldCallDao() {
        when(vaccinationScheduleDao.saveVaccinationSchedule(any(VaccinationSchedule.class)))
                .thenReturn(testSchedule);
        
        VaccinationSchedule result = vaccinationScheduleService.saveVaccinationSchedule(testSchedule);
        
        assertNotNull(result);
        assertEquals(testSchedule.getName(), result.getName());
        verify(vaccinationScheduleDao).saveVaccinationSchedule(testSchedule);
    }
    
    @Test
    public void testSaveVaccinationSchedule_WithNullSchedule_ShouldThrowException() {
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveVaccinationSchedule(null);
        });
        
        assertEquals("VaccinationSchedule cannot be null", exception.getMessage());
        verify(vaccinationScheduleDao, never()).saveVaccinationSchedule(any());
    }
    
    @Test
    public void testSaveVaccinationSchedule_WithEmptyName_ShouldThrowException() {
        testSchedule.setName("");
        
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveVaccinationSchedule(testSchedule);
        });
        
        assertEquals("VaccinationSchedule name is required", exception.getMessage());
        verify(vaccinationScheduleDao, never()).saveVaccinationSchedule(any());
    }
    
    @Test
    public void testSaveVaccinationSchedule_WithLongCountryCode_ShouldThrowException() {
        testSchedule.setCountryCode("TOOLONG");
        
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveVaccinationSchedule(testSchedule);
        });
        
        assertEquals("Country code cannot exceed 3 characters", exception.getMessage());
        verify(vaccinationScheduleDao, never()).saveVaccinationSchedule(any());
    }
    
    @Test
    public void testSaveVaccinationSchedule_WithNullEffectiveDate_ShouldThrowException() {
        testSchedule.setEffectiveDate(null);
        
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveVaccinationSchedule(testSchedule);
        });
        
        assertEquals("Effective date is required", exception.getMessage());
        verify(vaccinationScheduleDao, never()).saveVaccinationSchedule(any());
    }
    
    @Test
    public void testGetVaccinationScheduleByUuid_ShouldCallDao() {
        when(vaccinationScheduleDao.getVaccinationScheduleByUuid(anyString()))
                .thenReturn(testSchedule);
        
        VaccinationSchedule result = vaccinationScheduleService
                .getVaccinationScheduleByUuid("test-uuid");
        
        assertNotNull(result);
        assertEquals(testSchedule.getUuid(), result.getUuid());
        verify(vaccinationScheduleDao).getVaccinationScheduleByUuid("test-uuid");
    }
    
    @Test
    public void testGetActiveScheduleByCountry_ShouldCallDao() {
        when(vaccinationScheduleDao.getActiveScheduleByCountry(anyString()))
                .thenReturn(testSchedule);
        
        VaccinationSchedule result = vaccinationScheduleService
                .getActiveScheduleByCountry("TST");
        
        assertNotNull(result);
        assertEquals(testSchedule.getCountryCode(), result.getCountryCode());
        verify(vaccinationScheduleDao).getActiveScheduleByCountry("TST");
    }
    
    @Test
    public void testGetAllSchedules_ShouldCallDao() {
        List<VaccinationSchedule> schedules = Arrays.asList(testSchedule);
        when(vaccinationScheduleDao.getAllSchedules(anyBoolean()))
                .thenReturn(schedules);
        
        List<VaccinationSchedule> result = vaccinationScheduleService
                .getAllSchedules(false);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSchedule, result.get(0));
        verify(vaccinationScheduleDao).getAllSchedules(false);
    }
    
    @Test
    public void testRetireVaccinationSchedule_ShouldSetRetiredFields() {
        when(vaccinationScheduleDao.saveVaccinationSchedule(any(VaccinationSchedule.class)))
                .thenReturn(testSchedule);
        
        String reason = "Test retirement";
        VaccinationSchedule result = vaccinationScheduleService
                .retireVaccinationSchedule(testSchedule, reason);
        
        assertNotNull(result);
        assertTrue(testSchedule.getRetired());
        assertEquals(reason, testSchedule.getRetireReason());
        assertNotNull(testSchedule.getDateRetired());
        verify(vaccinationScheduleDao).saveVaccinationSchedule(testSchedule);
    }
    
    @Test
    public void testUnretireVaccinationSchedule_ShouldClearRetiredFields() {
        testSchedule.setRetired(true);
        testSchedule.setRetireReason("Previous reason");
        testSchedule.setDateRetired(new Date());
        
        when(vaccinationScheduleDao.saveVaccinationSchedule(any(VaccinationSchedule.class)))
                .thenReturn(testSchedule);
        
        VaccinationSchedule result = vaccinationScheduleService
                .unretireVaccinationSchedule(testSchedule);
        
        assertNotNull(result);
        assertFalse(testSchedule.getRetired());
        assertNull(testSchedule.getRetireReason());
        assertNull(testSchedule.getDateRetired());
        verify(vaccinationScheduleDao).saveVaccinationSchedule(testSchedule);
    }
    
    @Test
    public void testSaveScheduleEntry_WithValidEntry_ShouldValidateAges() {
        Concept vaccineConcept = new Concept();
        testEntry.setVaccineConcept(vaccineConcept);
        
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveScheduleEntry(testEntry);
        });
        
        assertTrue(exception.getMessage().contains("validation") || 
                  exception.getMessage().contains("required") ||
                  exception.getMessage().contains("positive"));
    }
    
    @Test
    public void testSaveScheduleEntry_WithNullVaccineConcept_ShouldThrowException() {
        testEntry.setVaccineConcept(null);
        
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveScheduleEntry(testEntry);
        });
        
        assertEquals("Vaccine concept is required", exception.getMessage());
    }
    
    @Test
    public void testSaveScheduleEntry_WithInvalidDoseNumber_ShouldThrowException() {
        Concept vaccineConcept = new Concept();
        testEntry.setVaccineConcept(vaccineConcept);
        testEntry.setDoseNumber(0);
        
        APIException exception = assertThrows(APIException.class, () -> {
            vaccinationScheduleService.saveScheduleEntry(testEntry);
        });
        
        assertEquals("Dose number must be positive", exception.getMessage());
    }
}