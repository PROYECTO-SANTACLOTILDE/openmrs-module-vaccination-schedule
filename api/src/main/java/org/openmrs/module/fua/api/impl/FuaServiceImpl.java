package org.openmrs.module.fua.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;
//import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fua.Fua;
import org.openmrs.module.fua.FuaEstado;
import org.openmrs.module.fua.api.FuaEstadoService;
import org.openmrs.module.fua.api.FuaService;
import org.openmrs.module.fua.api.dao.FuaDao;

import org.apache.commons.lang3.StringUtils; // Asegúrate de importar esto

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FuaServiceImpl extends BaseOpenmrsService implements FuaService {
	
	private FuaEstadoService fuaEstadoService;
	
	private FuaDao dao;
	
	public void setDao(FuaDao dao) {
		this.dao = dao;
	}
	
	@Override
	public List<Fua> getAllFuas() throws APIException {
		return dao.getAllFuas();
	}
	
	@Override
	public Fua getFua(Integer fuaId) throws APIException {
		return dao.getFua(fuaId);
	}
	
	@Override
	public Fua saveFua(Fua fua) throws APIException {
		if (StringUtils.isBlank(fua.getUuid())) {
			fua.setUuid(UUID.randomUUID().toString());
		}
		return dao.saveFua(fua);
	}
	
	@Override
	public void purgeFua(Fua fua) throws APIException {
		dao.purgeFua(fua);
	}
	
	@Override
	public Fua updateEstadoFua(Integer fuaId, FuaEstado nuevoEstado) throws APIException {
		Fua fua = dao.getFua(fuaId);
		
		if (fua == null) {
			throw new APIException("FUA no encontrado con ID: " + fuaId);
		}
		if (nuevoEstado == null) {
			throw new APIException("Estado FUA no encontrado con ID: " + nuevoEstado.getId());
		}
		
		fua.setFuaEstado(nuevoEstado);
		return dao.saveFua(fua);
	}
	
	public Fua getFuaByUuid(String uuid) throws APIException {
		return dao.getFuaByUuid(uuid);
	}
	
	@Override
	public List<Fua> getFuasFiltrados(String estado, LocalDate inicio, LocalDate fin, int page, int size) {
		int offset = (page - 1) * size;
		return dao.getFuasFiltrados(estado, inicio, fin, offset, size);
	}
	
}
