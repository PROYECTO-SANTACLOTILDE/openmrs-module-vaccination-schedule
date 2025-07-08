package org.openmrs.module.vaccinationschedule;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "vaccination_schedule", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"country_code", "version", "retired"}))
public class VaccinationSchedule extends BaseOpenmrsMetadata {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "country_code", length = 3)
    private String countryCode;
    
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    @Column(name = "effective_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date effectiveDate;
    
    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    
    @Column(name = "source_authority", length = 255)
    private String sourceAuthority;
    
    @OneToMany(mappedBy = "vaccinationSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<VaccinationScheduleEntry> entries;
    
    @OneToMany(mappedBy = "vaccinationSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VaccinationScheduleRule> rules;
    
    public VaccinationSchedule() {
        super();
    }
    
    public VaccinationSchedule(String name, String countryCode, Date effectiveDate) {
        this();
        this.name = name;
        this.countryCode = countryCode;
        this.effectiveDate = effectiveDate;
    }
    
    @Override
    public Integer getId() {
        return scheduleId;
    }
    
    @Override
    public void setId(Integer id) {
        this.scheduleId = id;
    }
    
    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getSourceAuthority() {
        return sourceAuthority;
    }
    
    public void setSourceAuthority(String sourceAuthority) {
        this.sourceAuthority = sourceAuthority;
    }
    
    public List<VaccinationScheduleEntry> getEntries() {
        return entries;
    }
    
    public void setEntries(List<VaccinationScheduleEntry> entries) {
        this.entries = entries;
    }
    
    public List<VaccinationScheduleRule> getRules() {
        return rules;
    }
    
    public void setRules(List<VaccinationScheduleRule> rules) {
        this.rules = rules;
    }
    
    public boolean isActive() {
        Date now = new Date();
        if (getRetired()) {
            return false;
        }
        if (effectiveDate != null && effectiveDate.after(now)) {
            return false;
        }
        if (expiryDate != null && expiryDate.before(now)) {
            return false;
        }
        return true;
    }
    
    public List<VaccinationScheduleEntry> getEntriesByAge(int ageInDays) {
        return entries.stream()
                .filter(entry -> !entry.getVoided() && entry.isValidForAge(ageInDays))
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<VaccinationScheduleEntry> getActiveEntries() {
        return entries.stream()
                .filter(entry -> !entry.getVoided())
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "VaccinationSchedule{" +
                "scheduleId=" + scheduleId +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", version=" + version +
                ", effectiveDate=" + effectiveDate +
                ", expiryDate=" + expiryDate +
                '}';
    }
}