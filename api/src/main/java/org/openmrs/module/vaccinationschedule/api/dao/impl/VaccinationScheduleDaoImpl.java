package org.openmrs.module.vaccinationschedule.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.vaccinationschedule.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.api.dao.VaccinationScheduleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("vaccinationschedule.VaccinationScheduleDao")
public class VaccinationScheduleDaoImpl implements VaccinationScheduleDao {
    
    @Autowired
    private DbSessionFactory sessionFactory;
    
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
}