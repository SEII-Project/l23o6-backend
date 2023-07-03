package org.fffd.l23o6.mapper;

import javax.annotation.processing.Generated;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.pojo.vo.station.StationVO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-03T10:48:10+0800",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (Amazon.com Inc.)"
)
public class StationMapperImpl implements StationMapper {

    @Override
    public StationVO toStationVO(StationEntity stationEntity) {
        if ( stationEntity == null ) {
            return null;
        }

        Long id = null;
        String name = null;

        id = stationEntity.getId();
        name = stationEntity.getName();

        StationVO stationVO = new StationVO( id, name );

        return stationVO;
    }
}
