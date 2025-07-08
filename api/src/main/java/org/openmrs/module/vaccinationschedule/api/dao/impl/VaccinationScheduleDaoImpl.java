package org.openmrs.module.vaccinationschedule.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.vaccinationschedule.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.api.dao.VaccinationScheduleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("vaccinationschedule.VaccinationScheduleDao")
public class VaccinationScheduleDaoImpl implements VaccinationScheduleDao {
    
    @Autowired
    private DbSessionFactory sessionFactory;
    
    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public VaccinationSchedule getVaccinationSchedule(Integer scheduleId) {
        return (VaccinationSchedule) getSession().get(VaccinationSchedule.class, scheduleId);
    }
    
    @Override
    public VaccinationSchedule getVaccinationScheduleByUuid(String uuid) {
        return (VaccinationSchedule) getSession().createCriteria(VaccinationSchedule.class)
                .add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }
    
    @Override
    public VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule) {
        getSession().saveOrUpdate(schedule);
        return schedule;
    }
    
    @Override
    public void deleteVaccinationSchedule(VaccinationSchedule schedule) {
        getSession().delete(schedule);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<VaccinationSchedule> getAllSchedules(boolean includeRetired) {
        Criteria criteria = getSession().createCriteria(VaccinationSchedule.class);
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        return criteria.list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired) {
        Criteria criteria = getSession().createCriteria(VaccinationSchedule.class)
                .add(Restrictions.eq("countryCode", countryCode));
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        return criteria.list();
    }
    
    @Override
    public VaccinationSchedule getActiveScheduleByCountry(String countryCode) {
        return (VaccinationSchedule) getSession().createCriteria(VaccinationSchedule.class)
                .add(Restrictions.eq("countryCode", countryCode))
                .add(Restrictions.eq("retired", false))
                .uniqueResult();
    }
    
    @Override
    public VaccinationScheduleEntry getScheduleEntry(Integer entryId) {
        return (VaccinationScheduleEntry) getSession().get(VaccinationScheduleEntry.class, entryId);
    }
    
    @Override
    public VaccinationScheduleEntry getScheduleEntryByUuid(String uuid) {
        return (VaccinationScheduleEntry) getSession().createCriteria(VaccinationScheduleEntry.class)
                .add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }
    
    @Override
    public VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry) {
        getSession().saveOrUpdate(entry);
        return entry;
    }
    
    @Override
    public void deleteScheduleEntry(VaccinationScheduleEntry entry) {
        getSession().delete(entry);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule) {
        return getSession().createCriteria(VaccinationScheduleEntry.class)
                .add(Restrictions.eq("vaccinationSchedule", schedule))
                .add(Restrictions.eq("voided", false))
                .list();
    }
}