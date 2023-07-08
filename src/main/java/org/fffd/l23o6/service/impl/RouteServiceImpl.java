package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.mapper.RouteMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.service.RouteService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteDao routeDao;
    
    /**
     *
     * @param name
     * @param stationIds
     */
    @Override
    public void addRoute(String name, List<Long> stationIds) {
        RouteEntity route = RouteEntity.builder().name(name).stationIds(stationIds).build();
        routeDao.save(route);
    }
    
    /**
     *
     * @return
     */
    @Override
    public List<RouteVO> listRoutes() {
        return routeDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(RouteMapper.INSTANCE::toRouteVO).collect(Collectors.toList());
    }
    
    /**
     *
     * @param id
     * @return
     */
    @Override
    public RouteVO getRoute(Long id) {
        RouteEntity entity = routeDao.findById(id).get();
        return RouteMapper.INSTANCE.toRouteVO(entity);
    }
    
    /**
     *
     * @param id
     * @param name
     * @param stationIds
     */
    @Override
    public void editRoute(Long id, String name, List<Long> stationIds) {
        routeDao.save(routeDao.findById(id).get().setStationIds(stationIds).setName(name));
    }
    
    /**
     *
     * @param id
     */
    @Override
    public void deleteRoute(Long id) {
        routeDao.deleteById(id);
    }
}
