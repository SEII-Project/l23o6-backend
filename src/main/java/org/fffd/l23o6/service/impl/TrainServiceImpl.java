package org.fffd.l23o6.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainStatus;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy.GSeriesSeatType;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy.KSeriesSeatType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainDetailVO getTrain(Long trainId) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfosText()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date) {
        // First, get all routes contains [startCity, endCity]
        List<RouteEntity> routeEntities = routeDao.findAll().stream()
                .filter(route -> {
                    int startIndex = route.getStationIds().indexOf(startStationId);
                    int endIndex = route.getStationIds().indexOf(endStationId);
                    return startIndex != -1 && endIndex != -1 && startIndex < endIndex;
                })
                .collect(Collectors.toList()); // 收集所有符合条件的路线信息

        if (routeEntities.isEmpty()) {
            return Collections.emptyList(); // 返回一个空的列表
        }

        // Then, Get all trains on that day with the wanted routes
        List<TrainEntity> trains = trainDao.findAll().stream()
                .filter(train -> train.getDate().equals(date) && routeEntities.stream().anyMatch(route -> route.getId().equals(train.getRouteId())))
                .collect(Collectors.toList()); // 收集所有符合条件的列车信息
        
        List<TrainVO> trainVOs = new ArrayList<>();
        if (trains.isEmpty()) {
            return Collections.emptyList(); // 返回一个空的列表
        } else {
            for (TrainEntity trainEntity: trains) {
                List<TicketInfo> ticketInfos = new ArrayList<>();
                RouteEntity routeEntity = routeDao.findById(trainEntity.getRouteId()).get();
                TrainVO trainVO = TrainMapper.INSTANCE.toTrainVO(trainEntity);
                
                int startIndex = routeEntity.getStationIds().indexOf(startStationId);
                int endIndex = routeEntity.getStationIds().indexOf(endStationId);

                switch (trainEntity.getTrainType()) {
                    case HIGH_SPEED -> {
                        Map<GSeriesSeatStrategy.GSeriesSeatType, Integer> gSeriesSeatMap = GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startIndex, endIndex, trainEntity.getSeats());
                        ticketInfos.add(new TicketInfo("商务座", gSeriesSeatMap.get(GSeriesSeatType.BUSINESS_SEAT), GSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, GSeriesSeatType.BUSINESS_SEAT)));
                        ticketInfos.add(new TicketInfo("一等座", gSeriesSeatMap.get(GSeriesSeatType.FIRST_CLASS_SEAT), GSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, GSeriesSeatType.FIRST_CLASS_SEAT)));
                        ticketInfos.add(new TicketInfo("二等座", gSeriesSeatMap.get(GSeriesSeatType.SECOND_CLASS_SEAT), GSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, GSeriesSeatType.SECOND_CLASS_SEAT)));
                        ticketInfos.add(new TicketInfo("无座", gSeriesSeatMap.get(GSeriesSeatType.NO_SEAT), GSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, GSeriesSeatType.NO_SEAT)));
                    }
                    case NORMAL_SPEED -> {
                        Map<KSeriesSeatStrategy.KSeriesSeatType, Integer> kSeriesSeatMap = KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startIndex, endIndex, trainEntity.getSeats());
                        ticketInfos.add(new TicketInfo("硬座", kSeriesSeatMap.get(KSeriesSeatType.HARD_SEAT), KSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, KSeriesSeatType.HARD_SEAT)));
                        ticketInfos.add(new TicketInfo("软座", kSeriesSeatMap.get(KSeriesSeatType.SOFT_SEAT), KSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, KSeriesSeatType.SOFT_SEAT)));
                        ticketInfos.add(new TicketInfo("硬卧", kSeriesSeatMap.get(KSeriesSeatType.HARD_SLEEPER_SEAT), KSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, KSeriesSeatType.HARD_SLEEPER_SEAT)));
                        ticketInfos.add(new TicketInfo("软卧", kSeriesSeatMap.get(KSeriesSeatType.SOFT_SLEEPER_SEAT), KSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, KSeriesSeatType.SOFT_SLEEPER_SEAT)));
                        ticketInfos.add(new TicketInfo("无座", kSeriesSeatMap.get(KSeriesSeatType.NO_SEAT), KSeriesSeatStrategy.INSTANCE.getPrice(startIndex, endIndex, KSeriesSeatType.NO_SEAT)));
                    }
                }
                trainVO.setTicketInfo(ticketInfos);
                trainVO.setStartStationId(routeDao.findById(trainEntity.getRouteId()).get().getStationIds().get(startIndex));
                trainVO.setEndStationId(routeDao.findById(trainEntity.getRouteId()).get().getStationIds().get(endIndex));
                trainVO.setDepartureTime(trainEntity.getDepartureTimes().get(startIndex));
                trainVO.setArrivalTime(trainEntity.getArrivalTimes().get(endIndex));
                trainVOs.add(trainVO);
            }
        }

        // Finally, return the trains
        return trainVOs;
    }

    @Override
    public List<AdminTrainVO> listTrainsAdmin() {
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                         List<Date> departureTimes, List<TrainStatus> extraInfos) {
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(extraInfos);
        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                            List<Date> departureTimes, List<TrainStatus> extraInfos) {
        TrainEntity entity = trainDao.findById(id).get();
        entity.setName(name).setRouteId(routeId).setTrainType(type).setDate(date).setArrivalTimes(arrivalTimes).setDepartureTimes(departureTimes).setExtraInfos(extraInfos);
        trainDao.save(entity);
    }

    @Override
    public void deleteTrain(Long id) {
        trainDao.deleteById(id);
    }
}
